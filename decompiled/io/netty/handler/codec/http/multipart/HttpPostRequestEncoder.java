package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;

public class HttpPostRequestEncoder implements ChunkedInput<HttpContent> {
   private static final String ASTERISK = "*";
   private static final String PLUS = "+";
   private static final String TILDE = "~";
   private static final String ASTERISK_REPLACEMENT = "%2A";
   private static final String PLUS_REPLACEMENT = "%20";
   private static final String TILDE_REPLACEMENT = "%7E";
   private final HttpDataFactory factory;
   private final HttpRequest request;
   private final Charset charset;
   private boolean isChunked;
   private final List<InterfaceHttpData> bodyListDatas;
   final List<InterfaceHttpData> multipartHttpDatas;
   private final boolean isMultipart;
   String multipartDataBoundary;
   String multipartMixedBoundary;
   private boolean headerFinalized;
   private final HttpPostRequestEncoder.EncoderMode encoderMode;
   private boolean isLastChunk;
   private boolean isLastChunkSent;
   private FileUpload currentFileUpload;
   private boolean duringMixedMode;
   private long globalBodySize;
   private long globalProgress;
   private ListIterator<InterfaceHttpData> iterator;
   private ByteBuf currentBuffer;
   private InterfaceHttpData currentData;
   private boolean isKey = true;

   public HttpPostRequestEncoder(HttpRequest request, boolean multipart) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this(new DefaultHttpDataFactory(16384L), request, multipart, HttpConstants.DEFAULT_CHARSET, HttpPostRequestEncoder.EncoderMode.RFC1738);
   }

   public HttpPostRequestEncoder(HttpDataFactory factory, HttpRequest request, boolean multipart) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this(factory, request, multipart, HttpConstants.DEFAULT_CHARSET, HttpPostRequestEncoder.EncoderMode.RFC1738);
   }

   public HttpPostRequestEncoder(
      HttpDataFactory factory, HttpRequest request, boolean multipart, Charset charset, HttpPostRequestEncoder.EncoderMode encoderMode
   ) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this.request = ObjectUtil.checkNotNull(request, "request");
      this.charset = ObjectUtil.checkNotNull(charset, "charset");
      this.factory = ObjectUtil.checkNotNull(factory, "factory");
      if (HttpMethod.TRACE.equals(request.method())) {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException("Cannot create a Encoder if request is a TRACE");
      } else {
         this.bodyListDatas = new ArrayList<>();
         this.isLastChunk = false;
         this.isLastChunkSent = false;
         this.isMultipart = multipart;
         this.multipartHttpDatas = new ArrayList<>();
         this.encoderMode = encoderMode;
         if (this.isMultipart) {
            this.initDataMultipart();
         }
      }
   }

   public void cleanFiles() {
      this.factory.cleanRequestHttpData(this.request);
   }

   public boolean isMultipart() {
      return this.isMultipart;
   }

   private void initDataMultipart() {
      this.multipartDataBoundary = getNewMultipartDelimiter();
   }

   private void initMixedMultipart() {
      this.multipartMixedBoundary = getNewMultipartDelimiter();
   }

   private static String getNewMultipartDelimiter() {
      return Long.toHexString(ThreadLocalRandom.current().nextLong());
   }

   public List<InterfaceHttpData> getBodyListAttributes() {
      return this.bodyListDatas;
   }

   public void setBodyHttpDatas(List<InterfaceHttpData> datas) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      ObjectUtil.checkNotNull(datas, "datas");
      this.globalBodySize = 0L;
      this.bodyListDatas.clear();
      this.currentFileUpload = null;
      this.duringMixedMode = false;
      this.multipartHttpDatas.clear();

      for (InterfaceHttpData data : datas) {
         this.addBodyHttpData(data);
      }
   }

   public void addBodyAttribute(String name, String value) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      String svalue = value != null ? value : "";
      Attribute data = this.factory.createAttribute(this.request, ObjectUtil.checkNotNull(name, "name"), svalue);
      this.addBodyHttpData(data);
   }

   public void addBodyFileUpload(String name, File file, String contentType, boolean isText) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this.addBodyFileUpload(name, file.getName(), file, contentType, isText);
   }

   public void addBodyFileUpload(String name, String filename, File file, String contentType, boolean isText) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      ObjectUtil.checkNotNull(name, "name");
      ObjectUtil.checkNotNull(file, "file");
      if (filename == null) {
         filename = "";
      }

      String scontentType = contentType;
      String contentTransferEncoding = null;
      if (contentType == null) {
         if (isText) {
            scontentType = "text/plain";
         } else {
            scontentType = "application/octet-stream";
         }
      }

      if (!isText) {
         contentTransferEncoding = HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value();
      }

      FileUpload fileUpload = this.factory.createFileUpload(this.request, name, filename, scontentType, contentTransferEncoding, null, file.length());

      try {
         fileUpload.setContent(file);
      } catch (IOException var10) {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException(var10);
      }

      this.addBodyHttpData(fileUpload);
   }

   public void addBodyFileUploads(String name, File[] file, String[] contentType, boolean[] isText) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (file.length != contentType.length && file.length != isText.length) {
         throw new IllegalArgumentException("Different array length");
      } else {
         for (int i = 0; i < file.length; i++) {
            this.addBodyFileUpload(name, file[i], contentType[i], isText[i]);
         }
      }
   }

   public void addBodyHttpData(InterfaceHttpData data) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.headerFinalized) {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException("Cannot add value once finalized");
      } else {
         this.bodyListDatas.add(ObjectUtil.checkNotNull(data, "data"));
         if (!this.isMultipart) {
            if (data instanceof Attribute) {
               Attribute attribute = (Attribute)data;

               try {
                  String key = this.encodeAttribute(attribute.getName(), this.charset);
                  String value = this.encodeAttribute(attribute.getValue(), this.charset);
                  Attribute newattribute = this.factory.createAttribute(this.request, key, value);
                  this.multipartHttpDatas.add(newattribute);
                  this.globalBodySize = this.globalBodySize + newattribute.getName().length() + 1 + newattribute.length() + 1L;
               } catch (IOException var7) {
                  throw new HttpPostRequestEncoder.ErrorDataEncoderException(var7);
               }
            } else if (data instanceof FileUpload) {
               FileUpload fileUpload = (FileUpload)data;
               String key = this.encodeAttribute(fileUpload.getName(), this.charset);
               String value = this.encodeAttribute(fileUpload.getFilename(), this.charset);
               Attribute newattribute = this.factory.createAttribute(this.request, key, value);
               this.multipartHttpDatas.add(newattribute);
               this.globalBodySize = this.globalBodySize + newattribute.getName().length() + 1 + newattribute.length() + 1L;
            }
         } else {
            if (data instanceof Attribute) {
               if (this.duringMixedMode) {
                  InternalAttribute internal = new InternalAttribute(this.charset);
                  internal.addValue("\r\n--" + this.multipartMixedBoundary + "--");
                  this.multipartHttpDatas.add(internal);
                  this.multipartMixedBoundary = null;
                  this.currentFileUpload = null;
                  this.duringMixedMode = false;
               }

               InternalAttribute internal = new InternalAttribute(this.charset);
               if (!this.multipartHttpDatas.isEmpty()) {
                  internal.addValue("\r\n");
               }

               internal.addValue("--" + this.multipartDataBoundary + "\r\n");
               Attribute attribute = (Attribute)data;
               internal.addValue(
                  HttpHeaderNames.CONTENT_DISPOSITION
                     + ": "
                     + HttpHeaderValues.FORM_DATA
                     + "; "
                     + HttpHeaderValues.NAME
                     + "=\""
                     + attribute.getName()
                     + "\"\r\n"
               );
               internal.addValue(HttpHeaderNames.CONTENT_LENGTH + ": " + attribute.length() + "\r\n");
               Charset localcharset = attribute.getCharset();
               if (localcharset != null) {
                  internal.addValue(HttpHeaderNames.CONTENT_TYPE + ": " + "text/plain" + "; " + HttpHeaderValues.CHARSET + '=' + localcharset.name() + "\r\n");
               }

               internal.addValue("\r\n");
               this.multipartHttpDatas.add(internal);
               this.multipartHttpDatas.add(data);
               this.globalBodySize = this.globalBodySize + attribute.length() + internal.size();
            } else if (data instanceof FileUpload) {
               FileUpload fileUpload = (FileUpload)data;
               InternalAttribute internalx = new InternalAttribute(this.charset);
               if (!this.multipartHttpDatas.isEmpty()) {
                  internalx.addValue("\r\n");
               }

               boolean localMixed;
               if (this.duringMixedMode) {
                  if (this.currentFileUpload != null && this.currentFileUpload.getName().equals(fileUpload.getName())) {
                     localMixed = true;
                  } else {
                     internalx.addValue("--" + this.multipartMixedBoundary + "--");
                     this.multipartHttpDatas.add(internalx);
                     this.multipartMixedBoundary = null;
                     internalx = new InternalAttribute(this.charset);
                     internalx.addValue("\r\n");
                     localMixed = false;
                     this.currentFileUpload = fileUpload;
                     this.duringMixedMode = false;
                  }
               } else if (this.encoderMode != HttpPostRequestEncoder.EncoderMode.HTML5
                  && this.currentFileUpload != null
                  && this.currentFileUpload.getName().equals(fileUpload.getName())) {
                  this.initMixedMultipart();
                  InternalAttribute pastAttribute = (InternalAttribute)this.multipartHttpDatas.get(this.multipartHttpDatas.size() - 2);
                  this.globalBodySize = this.globalBodySize - pastAttribute.size();
                  StringBuilder replacement = new StringBuilder(
                        139
                           + this.multipartDataBoundary.length()
                           + this.multipartMixedBoundary.length() * 2
                           + fileUpload.getFilename().length()
                           + fileUpload.getName().length()
                     )
                     .append("--")
                     .append(this.multipartDataBoundary)
                     .append("\r\n")
                     .append((CharSequence)HttpHeaderNames.CONTENT_DISPOSITION)
                     .append(": ")
                     .append((CharSequence)HttpHeaderValues.FORM_DATA)
                     .append("; ")
                     .append((CharSequence)HttpHeaderValues.NAME)
                     .append("=\"")
                     .append(fileUpload.getName())
                     .append("\"\r\n")
                     .append((CharSequence)HttpHeaderNames.CONTENT_TYPE)
                     .append(": ")
                     .append((CharSequence)HttpHeaderValues.MULTIPART_MIXED)
                     .append("; ")
                     .append((CharSequence)HttpHeaderValues.BOUNDARY)
                     .append('=')
                     .append(this.multipartMixedBoundary)
                     .append("\r\n\r\n")
                     .append("--")
                     .append(this.multipartMixedBoundary)
                     .append("\r\n")
                     .append((CharSequence)HttpHeaderNames.CONTENT_DISPOSITION)
                     .append(": ")
                     .append((CharSequence)HttpHeaderValues.ATTACHMENT);
                  if (!fileUpload.getFilename().isEmpty()) {
                     replacement.append("; ")
                        .append((CharSequence)HttpHeaderValues.FILENAME)
                        .append("=\"")
                        .append(this.currentFileUpload.getFilename())
                        .append('"');
                  }

                  replacement.append("\r\n");
                  pastAttribute.setValue(replacement.toString(), 1);
                  pastAttribute.setValue("", 2);
                  this.globalBodySize = this.globalBodySize + pastAttribute.size();
                  localMixed = true;
                  this.duringMixedMode = true;
               } else {
                  localMixed = false;
                  this.currentFileUpload = fileUpload;
                  this.duringMixedMode = false;
               }

               if (localMixed) {
                  internalx.addValue("--" + this.multipartMixedBoundary + "\r\n");
                  if (fileUpload.getFilename().isEmpty()) {
                     internalx.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.ATTACHMENT + "\r\n");
                  } else {
                     internalx.addValue(
                        HttpHeaderNames.CONTENT_DISPOSITION
                           + ": "
                           + HttpHeaderValues.ATTACHMENT
                           + "; "
                           + HttpHeaderValues.FILENAME
                           + "=\""
                           + fileUpload.getFilename()
                           + "\"\r\n"
                     );
                  }
               } else {
                  internalx.addValue("--" + this.multipartDataBoundary + "\r\n");
                  if (fileUpload.getFilename().isEmpty()) {
                     internalx.addValue(
                        HttpHeaderNames.CONTENT_DISPOSITION
                           + ": "
                           + HttpHeaderValues.FORM_DATA
                           + "; "
                           + HttpHeaderValues.NAME
                           + "=\""
                           + fileUpload.getName()
                           + "\"\r\n"
                     );
                  } else {
                     internalx.addValue(
                        HttpHeaderNames.CONTENT_DISPOSITION
                           + ": "
                           + HttpHeaderValues.FORM_DATA
                           + "; "
                           + HttpHeaderValues.NAME
                           + "=\""
                           + fileUpload.getName()
                           + "\"; "
                           + HttpHeaderValues.FILENAME
                           + "=\""
                           + fileUpload.getFilename()
                           + "\"\r\n"
                     );
                  }
               }

               internalx.addValue(HttpHeaderNames.CONTENT_LENGTH + ": " + fileUpload.length() + "\r\n");
               internalx.addValue(HttpHeaderNames.CONTENT_TYPE + ": " + fileUpload.getContentType());
               String contentTransferEncoding = fileUpload.getContentTransferEncoding();
               if (contentTransferEncoding != null && contentTransferEncoding.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
                  internalx.addValue(
                     "\r\n" + HttpHeaderNames.CONTENT_TRANSFER_ENCODING + ": " + HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value() + "\r\n\r\n"
                  );
               } else if (fileUpload.getCharset() != null) {
                  internalx.addValue("; " + HttpHeaderValues.CHARSET + '=' + fileUpload.getCharset().name() + "\r\n\r\n");
               } else {
                  internalx.addValue("\r\n\r\n");
               }

               this.multipartHttpDatas.add(internalx);
               this.multipartHttpDatas.add(data);
               this.globalBodySize = this.globalBodySize + fileUpload.length() + internalx.size();
            }
         }
      }
   }

   public HttpRequest finalizeRequest() throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (!this.headerFinalized) {
         if (this.isMultipart) {
            InternalAttribute internal = new InternalAttribute(this.charset);
            if (this.duringMixedMode) {
               internal.addValue("\r\n--" + this.multipartMixedBoundary + "--");
            }

            internal.addValue("\r\n--" + this.multipartDataBoundary + "--\r\n");
            this.multipartHttpDatas.add(internal);
            this.multipartMixedBoundary = null;
            this.currentFileUpload = null;
            this.duringMixedMode = false;
            this.globalBodySize = this.globalBodySize + internal.size();
         }

         this.headerFinalized = true;
         HttpHeaders headers = this.request.headers();
         List contentTypes = headers.getAll(HttpHeaderNames.CONTENT_TYPE);
         List transferEncoding = headers.getAll(HttpHeaderNames.TRANSFER_ENCODING);
         if (contentTypes != null) {
            headers.remove(HttpHeaderNames.CONTENT_TYPE);

            for (String contentType : contentTypes) {
               String lowercased = contentType.toLowerCase();
               if (!lowercased.startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString())
                  && !lowercased.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())) {
                  headers.add(HttpHeaderNames.CONTENT_TYPE, contentType);
               }
            }
         }

         if (this.isMultipart) {
            String value = HttpHeaderValues.MULTIPART_FORM_DATA + "; " + HttpHeaderValues.BOUNDARY + '=' + this.multipartDataBoundary;
            headers.add(HttpHeaderNames.CONTENT_TYPE, value);
         } else {
            headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
         }

         long realSize = this.globalBodySize;
         if (!this.isMultipart) {
            realSize--;
         }

         this.iterator = this.multipartHttpDatas.listIterator();
         headers.set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(realSize));
         if (realSize <= 8096L && !this.isMultipart) {
            HttpContent chunk = this.nextChunk();
            if (this.request instanceof FullHttpRequest) {
               FullHttpRequest fullRequest = (FullHttpRequest)this.request;
               ByteBuf chunkContent = chunk.content();
               if (fullRequest.content() != chunkContent) {
                  fullRequest.content().clear().writeBytes(chunkContent);
                  chunkContent.release();
               }

               return fullRequest;
            } else {
               return new HttpPostRequestEncoder.WrappedFullHttpRequest(this.request, chunk);
            }
         } else {
            this.isChunked = true;
            if (transferEncoding != null) {
               headers.remove(HttpHeaderNames.TRANSFER_ENCODING);

               for (CharSequence v : transferEncoding) {
                  if (!HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase(v)) {
                     headers.add(HttpHeaderNames.TRANSFER_ENCODING, v);
                  }
               }
            }

            HttpUtil.setTransferEncodingChunked(this.request, true);
            return new HttpPostRequestEncoder.WrappedHttpRequest(this.request);
         }
      } else {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException("Header already encoded");
      }
   }

   public boolean isChunked() {
      return this.isChunked;
   }

   private String encodeAttribute(String s, Charset charset) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (s == null) {
         return "";
      } else {
         try {
            String encoded = URLEncoder.encode(s, charset.name());
            if (this.encoderMode == HttpPostRequestEncoder.EncoderMode.RFC3986) {
               encoded = encoded.replace("*", "%2A").replace("+", "%20").replace("~", "%7E");
            }

            return encoded;
         } catch (UnsupportedEncodingException var4) {
            throw new HttpPostRequestEncoder.ErrorDataEncoderException(charset.name(), var4);
         }
      }
   }

   private ByteBuf fillByteBuf() {
      int length = this.currentBuffer.readableBytes();
      if (length > 8096) {
         return this.currentBuffer.readRetainedSlice(8096);
      } else {
         ByteBuf slice = this.currentBuffer;
         this.currentBuffer = null;
         return slice;
      }
   }

   private HttpContent encodeNextChunkMultipart(int sizeleft) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.currentData == null) {
         return null;
      } else {
         ByteBuf buffer;
         if (this.currentData instanceof InternalAttribute) {
            buffer = ((InternalAttribute)this.currentData).toByteBuf();
            this.currentData = null;
         } else {
            try {
               buffer = ((HttpData)this.currentData).getChunk(sizeleft);
            } catch (IOException var4) {
               throw new HttpPostRequestEncoder.ErrorDataEncoderException(var4);
            }

            if (buffer.capacity() == 0) {
               this.currentData = null;
               return null;
            }
         }

         if (this.currentBuffer == null) {
            this.currentBuffer = buffer;
         } else {
            this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, buffer);
         }

         if (this.currentBuffer.readableBytes() < 8096) {
            this.currentData = null;
            return null;
         } else {
            buffer = this.fillByteBuf();
            return new DefaultHttpContent(buffer);
         }
      }
   }

   private HttpContent encodeNextChunkUrlEncoded(int sizeleft) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.currentData == null) {
         return null;
      } else {
         int size = sizeleft;
         if (this.isKey) {
            String key = this.currentData.getName();
            ByteBuf buffer = Unpooled.wrappedBuffer(key.getBytes(this.charset));
            this.isKey = false;
            if (this.currentBuffer == null) {
               this.currentBuffer = Unpooled.wrappedBuffer(buffer, Unpooled.wrappedBuffer("=".getBytes(this.charset)));
            } else {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, buffer, Unpooled.wrappedBuffer("=".getBytes(this.charset)));
            }

            size = sizeleft - (buffer.readableBytes() + 1);
            if (this.currentBuffer.readableBytes() >= 8096) {
               buffer = this.fillByteBuf();
               return new DefaultHttpContent(buffer);
            }
         }

         ByteBuf bufferx;
         try {
            bufferx = ((HttpData)this.currentData).getChunk(size);
         } catch (IOException var5) {
            throw new HttpPostRequestEncoder.ErrorDataEncoderException(var5);
         }

         ByteBuf delimiter = null;
         if (bufferx.readableBytes() < size) {
            this.isKey = true;
            this.currentData = null;
            delimiter = this.iterator.hasNext() ? Unpooled.wrappedBuffer("&".getBytes(this.charset)) : null;
         }

         if (bufferx.capacity() == 0) {
            this.isKey = true;
            this.currentData = null;
            if (this.currentBuffer == null) {
               if (delimiter == null) {
                  return null;
               }

               this.currentBuffer = delimiter;
            } else if (delimiter != null) {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, delimiter);
            }

            if (this.currentBuffer.readableBytes() >= 8096) {
               bufferx = this.fillByteBuf();
               return new DefaultHttpContent(bufferx);
            } else {
               return null;
            }
         } else {
            if (this.currentBuffer == null) {
               if (delimiter != null) {
                  this.currentBuffer = Unpooled.wrappedBuffer(bufferx, delimiter);
               } else {
                  this.currentBuffer = bufferx;
               }
            } else if (delimiter != null) {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, bufferx, delimiter);
            } else {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, bufferx);
            }

            return this.currentBuffer.readableBytes() >= 8096 ? new DefaultHttpContent(this.fillByteBuf()) : null;
         }
      }
   }

   @Override
   public void close() throws Exception {
   }

   @Deprecated
   public HttpContent readChunk(ChannelHandlerContext ctx) throws Exception {
      return this.readChunk(ctx.alloc());
   }

   public HttpContent readChunk(ByteBufAllocator allocator) throws Exception {
      if (this.isLastChunkSent) {
         return null;
      } else {
         HttpContent nextChunk = this.nextChunk();
         this.globalProgress = this.globalProgress + nextChunk.content().readableBytes();
         return nextChunk;
      }
   }

   private HttpContent nextChunk() throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.isLastChunk) {
         this.isLastChunkSent = true;
         return LastHttpContent.EMPTY_LAST_CONTENT;
      } else {
         int size = this.calculateRemainingSize();
         if (size <= 0) {
            ByteBuf buffer = this.fillByteBuf();
            return new DefaultHttpContent(buffer);
         } else {
            if (this.currentData != null) {
               HttpContent chunk;
               if (this.isMultipart) {
                  chunk = this.encodeNextChunkMultipart(size);
               } else {
                  chunk = this.encodeNextChunkUrlEncoded(size);
               }

               if (chunk != null) {
                  return chunk;
               }

               size = this.calculateRemainingSize();
            }

            if (!this.iterator.hasNext()) {
               return this.lastChunk();
            } else {
               while (size > 0 && this.iterator.hasNext()) {
                  this.currentData = this.iterator.next();
                  HttpContent chunkx;
                  if (this.isMultipart) {
                     chunkx = this.encodeNextChunkMultipart(size);
                  } else {
                     chunkx = this.encodeNextChunkUrlEncoded(size);
                  }

                  if (chunkx != null) {
                     return chunkx;
                  }

                  size = this.calculateRemainingSize();
               }

               return this.lastChunk();
            }
         }
      }
   }

   private int calculateRemainingSize() {
      int size = 8096;
      if (this.currentBuffer != null) {
         size -= this.currentBuffer.readableBytes();
      }

      return size;
   }

   private HttpContent lastChunk() {
      this.isLastChunk = true;
      if (this.currentBuffer == null) {
         this.isLastChunkSent = true;
         return LastHttpContent.EMPTY_LAST_CONTENT;
      } else {
         ByteBuf buffer = this.currentBuffer;
         this.currentBuffer = null;
         return new DefaultHttpContent(buffer);
      }
   }

   @Override
   public boolean isEndOfInput() throws Exception {
      return this.isLastChunkSent;
   }

   @Override
   public long length() {
      return this.isMultipart ? this.globalBodySize : this.globalBodySize - 1L;
   }

   @Override
   public long progress() {
      return this.globalProgress;
   }

   public static enum EncoderMode {
      RFC1738,
      RFC3986,
      HTML5;
   }

   public static class ErrorDataEncoderException extends Exception {
      private static final long serialVersionUID = 5020247425493164465L;

      public ErrorDataEncoderException() {
      }

      public ErrorDataEncoderException(String msg) {
         super(msg);
      }

      public ErrorDataEncoderException(Throwable cause) {
         super(cause);
      }

      public ErrorDataEncoderException(String msg, Throwable cause) {
         super(msg, cause);
      }
   }

   private static final class WrappedFullHttpRequest extends HttpPostRequestEncoder.WrappedHttpRequest implements FullHttpRequest {
      private final HttpContent content;

      private WrappedFullHttpRequest(HttpRequest request, HttpContent content) {
         super(request);
         this.content = content;
      }

      @Override
      public FullHttpRequest setProtocolVersion(HttpVersion version) {
         super.setProtocolVersion(version);
         return this;
      }

      @Override
      public FullHttpRequest setMethod(HttpMethod method) {
         super.setMethod(method);
         return this;
      }

      @Override
      public FullHttpRequest setUri(String uri) {
         super.setUri(uri);
         return this;
      }

      @Override
      public FullHttpRequest copy() {
         return this.replace(this.content().copy());
      }

      @Override
      public FullHttpRequest duplicate() {
         return this.replace(this.content().duplicate());
      }

      @Override
      public FullHttpRequest retainedDuplicate() {
         return this.replace(this.content().retainedDuplicate());
      }

      @Override
      public FullHttpRequest replace(ByteBuf content) {
         DefaultFullHttpRequest duplicate = new DefaultFullHttpRequest(this.protocolVersion(), this.method(), this.uri(), content);
         duplicate.headers().set(this.headers());
         duplicate.trailingHeaders().set(this.trailingHeaders());
         return duplicate;
      }

      @Override
      public FullHttpRequest retain(int increment) {
         this.content.retain(increment);
         return this;
      }

      @Override
      public FullHttpRequest retain() {
         this.content.retain();
         return this;
      }

      @Override
      public FullHttpRequest touch() {
         this.content.touch();
         return this;
      }

      @Override
      public FullHttpRequest touch(Object hint) {
         this.content.touch(hint);
         return this;
      }

      @Override
      public ByteBuf content() {
         return this.content.content();
      }

      @Override
      public HttpHeaders trailingHeaders() {
         return (HttpHeaders)(this.content instanceof LastHttpContent ? ((LastHttpContent)this.content).trailingHeaders() : EmptyHttpHeaders.INSTANCE);
      }

      @Override
      public int refCnt() {
         return this.content.refCnt();
      }

      @Override
      public boolean release() {
         return this.content.release();
      }

      @Override
      public boolean release(int decrement) {
         return this.content.release(decrement);
      }
   }

   private static class WrappedHttpRequest implements HttpRequest {
      private final HttpRequest request;

      WrappedHttpRequest(HttpRequest request) {
         this.request = request;
      }

      @Override
      public HttpRequest setProtocolVersion(HttpVersion version) {
         this.request.setProtocolVersion(version);
         return this;
      }

      @Override
      public HttpRequest setMethod(HttpMethod method) {
         this.request.setMethod(method);
         return this;
      }

      @Override
      public HttpRequest setUri(String uri) {
         this.request.setUri(uri);
         return this;
      }

      @Override
      public HttpMethod getMethod() {
         return this.request.method();
      }

      @Override
      public HttpMethod method() {
         return this.request.method();
      }

      @Override
      public String getUri() {
         return this.request.uri();
      }

      @Override
      public String uri() {
         return this.request.uri();
      }

      @Override
      public HttpVersion getProtocolVersion() {
         return this.request.protocolVersion();
      }

      @Override
      public HttpVersion protocolVersion() {
         return this.request.protocolVersion();
      }

      @Override
      public HttpHeaders headers() {
         return this.request.headers();
      }

      @Override
      public DecoderResult decoderResult() {
         return this.request.decoderResult();
      }

      @Deprecated
      @Override
      public DecoderResult getDecoderResult() {
         return this.request.getDecoderResult();
      }

      @Override
      public void setDecoderResult(DecoderResult result) {
         this.request.setDecoderResult(result);
      }
   }
}
