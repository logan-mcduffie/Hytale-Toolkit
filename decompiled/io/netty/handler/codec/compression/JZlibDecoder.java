package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class JZlibDecoder extends ZlibDecoder {
   private final Inflater z = new Inflater();
   private byte[] dictionary;
   private boolean needsRead;
   private volatile boolean finished;

   @Deprecated
   public JZlibDecoder() {
      this(ZlibWrapper.ZLIB, 0);
   }

   public JZlibDecoder(int maxAllocation) {
      this(ZlibWrapper.ZLIB, maxAllocation);
   }

   @Deprecated
   public JZlibDecoder(ZlibWrapper wrapper) {
      this(wrapper, 0);
   }

   public JZlibDecoder(ZlibWrapper wrapper, int maxAllocation) {
      super(maxAllocation);
      ObjectUtil.checkNotNull(wrapper, "wrapper");
      int resultCode = this.z.init(ZlibUtil.convertWrapperType(wrapper));
      if (resultCode != 0) {
         ZlibUtil.fail(this.z, "initialization failure", resultCode);
      }
   }

   @Deprecated
   public JZlibDecoder(byte[] dictionary) {
      this(dictionary, 0);
   }

   public JZlibDecoder(byte[] dictionary, int maxAllocation) {
      super(maxAllocation);
      this.dictionary = ObjectUtil.checkNotNull(dictionary, "dictionary");
      int resultCode = this.z.inflateInit(JZlib.W_ZLIB);
      if (resultCode != 0) {
         ZlibUtil.fail(this.z, "initialization failure", resultCode);
      }
   }

   @Override
   public boolean isClosed() {
      return this.finished;
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      this.needsRead = true;
      if (this.finished) {
         in.skipBytes(in.readableBytes());
      } else {
         int inputLength = in.readableBytes();
         if (inputLength != 0) {
            try {
               this.z.avail_in = inputLength;
               if (in.hasArray()) {
                  this.z.next_in = in.array();
                  this.z.next_in_index = in.arrayOffset() + in.readerIndex();
               } else {
                  byte[] array = new byte[inputLength];
                  in.getBytes(in.readerIndex(), array);
                  this.z.next_in = array;
                  this.z.next_in_index = 0;
               }

               int oldNextInIndex = this.z.next_in_index;
               ByteBuf decompressed = this.prepareDecompressBuffer(ctx, null, inputLength << 1);

               try {
                  while (true) {
                     decompressed = this.prepareDecompressBuffer(ctx, decompressed, this.z.avail_in << 1);
                     this.z.avail_out = decompressed.writableBytes();
                     this.z.next_out = decompressed.array();
                     this.z.next_out_index = decompressed.arrayOffset() + decompressed.writerIndex();
                     int oldNextOutIndex = this.z.next_out_index;
                     int resultCode = this.z.inflate(2);
                     int outputLength = this.z.next_out_index - oldNextOutIndex;
                     if (outputLength > 0) {
                        decompressed.writerIndex(decompressed.writerIndex() + outputLength);
                        if (this.maxAllocation == 0) {
                           ByteBuf buffer = decompressed;
                           decompressed = null;
                           this.needsRead = false;
                           ctx.fireChannelRead(buffer);
                        }
                     }

                     switch (resultCode) {
                        case -5:
                           if (this.z.avail_in <= 0) {
                              return;
                           }
                           break;
                        case -4:
                        case -3:
                        case -2:
                        case -1:
                        default:
                           ZlibUtil.fail(this.z, "decompression failure", resultCode);
                        case 0:
                           break;
                        case 1:
                           this.finished = true;
                           this.z.inflateEnd();
                           return;
                        case 2:
                           if (this.dictionary == null) {
                              ZlibUtil.fail(this.z, "decompression failure", resultCode);
                           } else {
                              resultCode = this.z.inflateSetDictionary(this.dictionary, this.dictionary.length);
                              if (resultCode != 0) {
                                 ZlibUtil.fail(this.z, "failed to set the dictionary", resultCode);
                              }
                           }
                     }
                  }
               } finally {
                  in.skipBytes(this.z.next_in_index - oldNextInIndex);
                  if (decompressed != null) {
                     if (decompressed.isReadable()) {
                        this.needsRead = false;
                        ctx.fireChannelRead(decompressed);
                     } else {
                        decompressed.release();
                     }
                  }
               }
            } finally {
               this.z.next_in = null;
               this.z.next_out = null;
            }
         }
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      this.discardSomeReadBytes();
      if (this.needsRead && !ctx.channel().config().isAutoRead()) {
         ctx.read();
      }

      ctx.fireChannelReadComplete();
   }

   @Override
   protected void decompressionBufferExhausted(ByteBuf buffer) {
      this.finished = true;
   }
}
