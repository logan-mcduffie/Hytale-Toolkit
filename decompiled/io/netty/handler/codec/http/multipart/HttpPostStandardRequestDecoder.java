package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostStandardRequestDecoder implements InterfaceHttpPostRequestDecoder {
   private final HttpDataFactory factory;
   private final HttpRequest request;
   private final Charset charset;
   private final int maxFields;
   private final int maxBufferedBytes;
   private boolean isLastChunk;
   private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<>();
   private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<>(CaseIgnoringComparator.INSTANCE);
   private ByteBuf undecodedChunk;
   private int bodyListHttpDataRank;
   private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
   private Attribute currentAttribute;
   private boolean destroyed;
   private int discardThreshold = 10485760;

   public HttpPostStandardRequestDecoder(HttpRequest request) {
      this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request) {
      this(factory, request, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
      this(factory, request, charset, 128, 1024);
   }

   public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset, int maxFields, int maxBufferedBytes) {
      this.request = ObjectUtil.checkNotNull(request, "request");
      this.charset = ObjectUtil.checkNotNull(charset, "charset");
      this.factory = ObjectUtil.checkNotNull(factory, "factory");
      this.maxFields = maxFields;
      this.maxBufferedBytes = maxBufferedBytes;

      try {
         if (request instanceof HttpContent) {
            this.offer((HttpContent)request);
         } else {
            this.parseBody();
         }
      } catch (Throwable var7) {
         this.destroy();
         PlatformDependent.throwException(var7);
      }
   }

   private void checkDestroyed() {
      if (this.destroyed) {
         throw new IllegalStateException(HttpPostStandardRequestDecoder.class.getSimpleName() + " was destroyed already");
      }
   }

   @Override
   public boolean isMultipart() {
      this.checkDestroyed();
      return false;
   }

   @Override
   public void setDiscardThreshold(int discardThreshold) {
      this.discardThreshold = ObjectUtil.checkPositiveOrZero(discardThreshold, "discardThreshold");
   }

   @Override
   public int getDiscardThreshold() {
      return this.discardThreshold;
   }

   @Override
   public List<InterfaceHttpData> getBodyHttpDatas() {
      this.checkDestroyed();
      if (!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         return this.bodyListHttpData;
      }
   }

   @Override
   public List<InterfaceHttpData> getBodyHttpDatas(String name) {
      this.checkDestroyed();
      if (!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         return this.bodyMapHttpData.get(name);
      }
   }

   @Override
   public InterfaceHttpData getBodyHttpData(String name) {
      this.checkDestroyed();
      if (!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         List<InterfaceHttpData> list = this.bodyMapHttpData.get(name);
         return list != null ? list.get(0) : null;
      }
   }

   public HttpPostStandardRequestDecoder offer(HttpContent content) {
      this.checkDestroyed();
      if (content instanceof LastHttpContent) {
         this.isLastChunk = true;
      }

      ByteBuf buf = content.content();
      if (this.undecodedChunk == null) {
         this.undecodedChunk = buf.alloc().buffer(buf.readableBytes()).writeBytes(buf);
      } else {
         this.undecodedChunk.writeBytes(buf);
      }

      this.parseBody();
      if (this.maxBufferedBytes > 0 && this.undecodedChunk != null && this.undecodedChunk.readableBytes() > this.maxBufferedBytes) {
         throw new HttpPostRequestDecoder.TooLongFormFieldException();
      } else {
         if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
            if (this.undecodedChunk.refCnt() == 1) {
               this.undecodedChunk.discardReadBytes();
            } else {
               ByteBuf buffer = this.undecodedChunk.alloc().buffer(this.undecodedChunk.readableBytes());
               buffer.writeBytes(this.undecodedChunk);
               this.undecodedChunk.release();
               this.undecodedChunk = buffer;
            }
         }

         return this;
      }
   }

   @Override
   public boolean hasNext() {
      this.checkDestroyed();
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
         throw new HttpPostRequestDecoder.EndOfDataDecoderException();
      } else {
         return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
      }
   }

   @Override
   public InterfaceHttpData next() {
      this.checkDestroyed();
      return this.hasNext() ? this.bodyListHttpData.get(this.bodyListHttpDataRank++) : null;
   }

   @Override
   public InterfaceHttpData currentPartialHttpData() {
      return this.currentAttribute;
   }

   private void parseBody() {
      if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE && this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
         this.parseBodyAttributes();
      } else {
         if (this.isLastChunk) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
         }
      }
   }

   protected void addHttpData(InterfaceHttpData data) {
      if (data != null) {
         if (this.maxFields > 0 && this.bodyListHttpData.size() >= this.maxFields) {
            throw new HttpPostRequestDecoder.TooManyFormFieldsException();
         } else {
            List<InterfaceHttpData> datas = this.bodyMapHttpData.get(data.getName());
            if (datas == null) {
               datas = new ArrayList<>(1);
               this.bodyMapHttpData.put(data.getName(), datas);
            }

            datas.add(data);
            this.bodyListHttpData.add(data);
         }
      }
   }

   private void parseBodyAttributesStandard() {
      int firstpos = this.undecodedChunk.readerIndex();
      int currentpos = firstpos;
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
      }

      boolean contRead = true;

      try {
         while (this.undecodedChunk.isReadable() && contRead) {
            char read = (char)this.undecodedChunk.readUnsignedByte();
            currentpos++;
            switch (this.currentStatus) {
               case DISPOSITION:
                  if (read == '=') {
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                     int equalpos = currentpos - 1;
                     String key = decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                     this.currentAttribute = this.factory.createAttribute(this.request, key);
                     firstpos = currentpos;
                  } else {
                     if (read != '&' && (!this.isLastChunk || this.undecodedChunk.isReadable() || !this.hasFormBody())) {
                        continue;
                     }

                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                     int ampersandpos = read == '&' ? currentpos - 1 : currentpos;
                     String key = decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                     if (!key.isEmpty()) {
                        this.currentAttribute = this.factory.createAttribute(this.request, key);
                        this.currentAttribute.setValue("");
                        this.addHttpData(this.currentAttribute);
                     }

                     this.currentAttribute = null;
                     firstpos = currentpos;
                     contRead = true;
                  }
                  break;
               case FIELD:
                  if (read == '&') {
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                     int ampersandpos = currentpos - 1;
                     this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                     firstpos = currentpos;
                     contRead = true;
                  } else if (read == '\r') {
                     if (this.undecodedChunk.isReadable()) {
                        read = (char)this.undecodedChunk.readUnsignedByte();
                        currentpos++;
                        if (read != '\n') {
                           throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                        }

                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                        int ampersandpos = currentpos - 2;
                        this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                        firstpos = currentpos;
                        contRead = false;
                     } else {
                        currentpos--;
                     }
                  } else if (read == '\n') {
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                     int ampersandpos = currentpos - 1;
                     this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                     firstpos = currentpos;
                     contRead = false;
                  }
                  break;
               default:
                  contRead = false;
            }
         }

         if (this.isLastChunk && this.currentAttribute != null) {
            if (currentpos > firstpos) {
               this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, currentpos - firstpos));
            } else if (!this.currentAttribute.isCompleted()) {
               this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
            }

            firstpos = currentpos;
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
         } else if (contRead && this.currentAttribute != null && this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
            this.currentAttribute.addContent(this.undecodedChunk.retainedSlice(firstpos, currentpos - firstpos), false);
            firstpos = currentpos;
         }

         this.undecodedChunk.readerIndex(firstpos);
      } catch (HttpPostRequestDecoder.ErrorDataDecoderException var8) {
         this.undecodedChunk.readerIndex(firstpos);
         throw var8;
      } catch (IllegalArgumentException | IOException var9) {
         this.undecodedChunk.readerIndex(firstpos);
         throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
      }
   }

   private void parseBodyAttributes() {
      if (this.undecodedChunk != null) {
         this.parseBodyAttributesStandard();
      }
   }

   private void setFinalBuffer(ByteBuf buffer) throws IOException {
      this.currentAttribute.addContent(buffer, true);
      ByteBuf decodedBuf = decodeAttribute(this.currentAttribute.getByteBuf(), this.charset);
      if (decodedBuf != null) {
         this.currentAttribute.setContent(decodedBuf);
      }

      this.addHttpData(this.currentAttribute);
      this.currentAttribute = null;
   }

   private static String decodeAttribute(String s, Charset charset) {
      try {
         return QueryStringDecoder.decodeComponent(s, charset);
      } catch (IllegalArgumentException var3) {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: '" + s + '\'', var3);
      }
   }

   private static ByteBuf decodeAttribute(ByteBuf b, Charset charset) {
      int firstEscaped = b.forEachByte(new HttpPostStandardRequestDecoder.UrlEncodedDetector());
      if (firstEscaped == -1) {
         return null;
      } else {
         ByteBuf buf = b.alloc().buffer(b.readableBytes());
         HttpPostStandardRequestDecoder.UrlDecoder urlDecode = new HttpPostStandardRequestDecoder.UrlDecoder(buf);
         int idx = b.forEachByte(urlDecode);
         if (urlDecode.nextEscapedIdx != 0) {
            if (idx == -1) {
               idx = b.readableBytes() - 1;
            }

            idx -= urlDecode.nextEscapedIdx - 1;
            buf.release();
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(
               String.format("Invalid hex byte at index '%d' in string: '%s'", idx, b.toString(charset))
            );
         } else {
            return buf;
         }
      }
   }

   @Override
   public void destroy() {
      this.cleanFiles();

      for (InterfaceHttpData httpData : this.bodyListHttpData) {
         if (httpData.refCnt() > 0) {
            httpData.release();
         }
      }

      this.destroyed = true;
      if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
         this.undecodedChunk.release();
         this.undecodedChunk = null;
      }
   }

   @Override
   public void cleanFiles() {
      this.checkDestroyed();
      this.factory.cleanRequestHttpData(this.request);
   }

   @Override
   public void removeHttpDataFromClean(InterfaceHttpData data) {
      this.checkDestroyed();
      this.factory.removeHttpDataFromClean(this.request, data);
   }

   private boolean hasFormBody() {
      String contentHeaderValue = this.request.headers().get(HttpHeaderNames.CONTENT_TYPE);
      return contentHeaderValue == null
         ? false
         : HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.contentEquals(contentHeaderValue)
            || HttpHeaderValues.MULTIPART_FORM_DATA.contentEquals(contentHeaderValue);
   }

   private static final class UrlDecoder implements ByteProcessor {
      private final ByteBuf output;
      private int nextEscapedIdx;
      private byte hiByte;

      UrlDecoder(ByteBuf output) {
         this.output = output;
      }

      @Override
      public boolean process(byte value) {
         if (this.nextEscapedIdx != 0) {
            if (this.nextEscapedIdx == 1) {
               this.hiByte = value;
               this.nextEscapedIdx++;
            } else {
               int hi = StringUtil.decodeHexNibble((char)this.hiByte);
               int lo = StringUtil.decodeHexNibble((char)value);
               if (hi == -1 || lo == -1) {
                  this.nextEscapedIdx++;
                  return false;
               }

               this.output.writeByte((hi << 4) + lo);
               this.nextEscapedIdx = 0;
            }
         } else if (value == 37) {
            this.nextEscapedIdx = 1;
         } else if (value == 43) {
            this.output.writeByte(32);
         } else {
            this.output.writeByte(value);
         }

         return true;
      }
   }

   private static final class UrlEncodedDetector implements ByteProcessor {
      private UrlEncodedDetector() {
      }

      @Override
      public boolean process(byte value) throws Exception {
         return value != 37 && value != 43;
      }
   }
}
