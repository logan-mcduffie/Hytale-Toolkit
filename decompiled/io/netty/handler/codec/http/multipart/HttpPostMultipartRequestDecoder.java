package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostMultipartRequestDecoder implements InterfaceHttpPostRequestDecoder {
   private final HttpDataFactory factory;
   private final HttpRequest request;
   private final int maxFields;
   private final int maxBufferedBytes;
   private Charset charset;
   private boolean isLastChunk;
   private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<>();
   private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<>(CaseIgnoringComparator.INSTANCE);
   private ByteBuf undecodedChunk;
   private int bodyListHttpDataRank;
   private final String multipartDataBoundary;
   private String multipartMixedBoundary;
   private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
   private Map<CharSequence, Attribute> currentFieldAttributes;
   private FileUpload currentFileUpload;
   private Attribute currentAttribute;
   private boolean destroyed;
   private int discardThreshold = 10485760;
   private static final String FILENAME_ENCODED = HttpHeaderValues.FILENAME.toString() + '*';

   public HttpPostMultipartRequestDecoder(HttpRequest request) {
      this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request) {
      this(factory, request, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
      this(factory, request, charset, 128, 1024);
   }

   public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset, int maxFields, int maxBufferedBytes) {
      this.request = ObjectUtil.checkNotNull(request, "request");
      this.charset = ObjectUtil.checkNotNull(charset, "charset");
      this.factory = ObjectUtil.checkNotNull(factory, "factory");
      this.maxFields = maxFields;
      this.maxBufferedBytes = maxBufferedBytes;
      String contentTypeValue = this.request.headers().get(HttpHeaderNames.CONTENT_TYPE);
      if (contentTypeValue == null) {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("No '" + HttpHeaderNames.CONTENT_TYPE + "' header present.");
      } else {
         String[] dataBoundary = HttpPostRequestDecoder.getMultipartDataBoundary(contentTypeValue);
         if (dataBoundary != null) {
            this.multipartDataBoundary = dataBoundary[0];
            if (dataBoundary.length > 1 && dataBoundary[1] != null) {
               try {
                  this.charset = Charset.forName(dataBoundary[1]);
               } catch (IllegalCharsetNameException var10) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
               }
            }
         } else {
            this.multipartDataBoundary = null;
         }

         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;

         try {
            if (request instanceof HttpContent) {
               this.offer((HttpContent)request);
            } else {
               this.parseBody();
            }
         } catch (Throwable var9) {
            this.destroy();
            PlatformDependent.throwException(var9);
         }
      }
   }

   private void checkDestroyed() {
      if (this.destroyed) {
         throw new IllegalStateException(HttpPostMultipartRequestDecoder.class.getSimpleName() + " was destroyed already");
      }
   }

   @Override
   public boolean isMultipart() {
      this.checkDestroyed();
      return true;
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

   public HttpPostMultipartRequestDecoder offer(HttpContent content) {
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
      return (InterfaceHttpData)(this.currentFileUpload != null ? this.currentFileUpload : this.currentAttribute);
   }

   private void parseBody() {
      if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE && this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
         this.parseBodyMultipart();
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

   private void parseBodyMultipart() {
      if (this.undecodedChunk != null && this.undecodedChunk.readableBytes() != 0) {
         for (InterfaceHttpData data = this.decodeMultipart(this.currentStatus); data != null; data = this.decodeMultipart(this.currentStatus)) {
            this.addHttpData(data);
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE
               || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
               break;
            }
         }
      }
   }

   private InterfaceHttpData decodeMultipart(HttpPostRequestDecoder.MultiPartStatus state) {
      switch (state) {
         case NOTSTARTED:
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
         case PREAMBLE:
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
         case HEADERDELIMITER:
            return this.findMultipartDelimiter(
               this.multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE
            );
         case DISPOSITION:
            return this.findMultipartDisposition();
         case FIELD:
            Charset localCharset = null;
            Attribute charsetAttribute = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
            if (charsetAttribute != null) {
               try {
                  localCharset = Charset.forName(charsetAttribute.getValue());
               } catch (UnsupportedCharsetException | IOException var12) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
               }
            }

            Attribute nameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
            if (this.currentAttribute == null) {
               Attribute lengthAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);

               long size;
               try {
                  size = lengthAttribute != null ? Long.parseLong(lengthAttribute.getValue()) : 0L;
               } catch (IOException var10) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
               } catch (NumberFormatException var11) {
                  size = 0L;
               }

               try {
                  if (size > 0L) {
                     this.currentAttribute = this.factory.createAttribute(this.request, cleanString(nameAttribute.getValue()), size);
                  } else {
                     this.currentAttribute = this.factory.createAttribute(this.request, cleanString(nameAttribute.getValue()));
                  }
               } catch (IllegalArgumentException | IOException | NullPointerException var9) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
               }

               if (localCharset != null) {
                  this.currentAttribute.setCharset(localCharset);
               }
            }

            if (!loadDataMultipartOptimized(this.undecodedChunk, this.multipartDataBoundary, this.currentAttribute)) {
               return null;
            }

            Attribute finalAttribute = this.currentAttribute;
            this.currentAttribute = null;
            this.currentFieldAttributes = null;
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
            return finalAttribute;
         case FILEUPLOAD:
            return this.getFileUpload(this.multipartDataBoundary);
         case MIXEDDELIMITER:
            return this.findMultipartDelimiter(
               this.multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER
            );
         case MIXEDDISPOSITION:
            return this.findMultipartDisposition();
         case MIXEDFILEUPLOAD:
            return this.getFileUpload(this.multipartMixedBoundary);
         case PREEPILOGUE:
            return null;
         case EPILOGUE:
            return null;
         default:
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn't reach here.");
      }
   }

   private static void skipControlCharacters(ByteBuf undecodedChunk) {
      if (!undecodedChunk.hasArray()) {
         try {
            skipControlCharactersStandard(undecodedChunk);
         } catch (IndexOutOfBoundsException var3) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var3);
         }
      } else {
         HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);

         while (sao.pos < sao.limit) {
            char c = (char)(sao.bytes[sao.pos++] & 255);
            if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
               sao.setReadPosition(1);
               return;
            }
         }

         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
      }
   }

   private static void skipControlCharactersStandard(ByteBuf undecodedChunk) {
      char c;
      do {
         c = (char)undecodedChunk.readUnsignedByte();
      } while (Character.isISOControl(c) || Character.isWhitespace(c));

      undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
   }

   private InterfaceHttpData findMultipartDelimiter(
      String delimiter, HttpPostRequestDecoder.MultiPartStatus dispositionStatus, HttpPostRequestDecoder.MultiPartStatus closeDelimiterStatus
   ) {
      int readerIndex = this.undecodedChunk.readerIndex();

      try {
         skipControlCharacters(this.undecodedChunk);
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var8) {
         this.undecodedChunk.readerIndex(readerIndex);
         return null;
      }

      this.skipOneLine();

      String newline;
      try {
         newline = readDelimiterOptimized(this.undecodedChunk, delimiter, this.charset);
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var7) {
         this.undecodedChunk.readerIndex(readerIndex);
         return null;
      }

      if (newline.equals(delimiter)) {
         this.currentStatus = dispositionStatus;
         return this.decodeMultipart(dispositionStatus);
      } else if (newline.equals(delimiter + "--")) {
         this.currentStatus = closeDelimiterStatus;
         if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) {
            this.currentFieldAttributes = null;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
         } else {
            return null;
         }
      } else {
         this.undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
      }
   }

   private InterfaceHttpData findMultipartDisposition() {
      int readerIndex = this.undecodedChunk.readerIndex();
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
         this.currentFieldAttributes = new TreeMap<>(CaseIgnoringComparator.INSTANCE);
      }

      while (!this.skipOneLine()) {
         String newline;
         try {
            skipControlCharacters(this.undecodedChunk);
            newline = readLineOptimized(this.undecodedChunk, this.charset);
         } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var16) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
         }

         String[] contents = splitMultipartHeader(newline);
         if (!HttpHeaderNames.CONTENT_DISPOSITION.contentEqualsIgnoreCase(contents[0])) {
            if (HttpHeaderNames.CONTENT_TRANSFER_ENCODING.contentEqualsIgnoreCase(contents[0])) {
               Attribute attribute;
               try {
                  attribute = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_TRANSFER_ENCODING.toString(), cleanString(contents[1]));
               } catch (IllegalArgumentException | NullPointerException var14) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
               }

               this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_TRANSFER_ENCODING, attribute);
            } else if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(contents[0])) {
               Attribute attribute;
               try {
                  attribute = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_LENGTH.toString(), cleanString(contents[1]));
               } catch (IllegalArgumentException | NullPointerException var13) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var13);
               }

               this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_LENGTH, attribute);
            } else if (HttpHeaderNames.CONTENT_TYPE.contentEqualsIgnoreCase(contents[0])) {
               if (HttpHeaderValues.MULTIPART_MIXED.contentEqualsIgnoreCase(contents[1])) {
                  if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                     String values = StringUtil.substringAfter(contents[2], '=');
                     this.multipartMixedBoundary = "--" + values;
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                     return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
                  }

                  throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
               }

               for (int i = 1; i < contents.length; i++) {
                  String charsetHeader = HttpHeaderValues.CHARSET.toString();
                  if (contents[i].regionMatches(true, 0, charsetHeader, 0, charsetHeader.length())) {
                     String values = StringUtil.substringAfter(contents[i], '=');

                     Attribute attribute;
                     try {
                        attribute = this.factory.createAttribute(this.request, charsetHeader, cleanString(values));
                     } catch (IllegalArgumentException | NullPointerException var12) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
                     }

                     this.currentFieldAttributes.put(HttpHeaderValues.CHARSET, attribute);
                  } else if (contents[i].contains("=")) {
                     String name = StringUtil.substringBefore(contents[i], '=');
                     String values = StringUtil.substringAfter(contents[i], '=');

                     Attribute attribute;
                     try {
                        attribute = this.factory.createAttribute(this.request, cleanString(name), values);
                     } catch (IllegalArgumentException | NullPointerException var11) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
                     }

                     this.currentFieldAttributes.put(name, attribute);
                  } else {
                     Attribute attribute;
                     try {
                        attribute = this.factory.createAttribute(this.request, cleanString(contents[0]), contents[i]);
                     } catch (IllegalArgumentException | NullPointerException var10) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
                     }

                     this.currentFieldAttributes.put(attribute.getName(), attribute);
                  }
               }
            }
         } else {
            boolean checkSecondArg;
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
               checkSecondArg = HttpHeaderValues.FORM_DATA.contentEqualsIgnoreCase(contents[1]);
            } else {
               checkSecondArg = HttpHeaderValues.ATTACHMENT.contentEqualsIgnoreCase(contents[1]) || HttpHeaderValues.FILE.contentEqualsIgnoreCase(contents[1]);
            }

            if (checkSecondArg) {
               for (int ix = 2; ix < contents.length; ix++) {
                  String[] values = contents[ix].split("=", 2);

                  Attribute attribute;
                  try {
                     attribute = this.getContentDispositionAttribute(values);
                  } catch (IllegalArgumentException | NullPointerException var15) {
                     throw new HttpPostRequestDecoder.ErrorDataDecoderException(var15);
                  }

                  this.currentFieldAttributes.put(attribute.getName(), attribute);
               }
            }
         }
      }

      Attribute filenameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
         if (filenameAttribute != null) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
         } else {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
         }
      } else if (filenameAttribute != null) {
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
         return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
      } else {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
      }
   }

   private Attribute getContentDispositionAttribute(String... values) {
      String name = cleanString(values[0]);
      String value = values[1];
      if (HttpHeaderValues.FILENAME.contentEquals(name)) {
         int last = value.length() - 1;
         if (last > 0 && value.charAt(0) == '"' && value.charAt(last) == '"') {
            value = value.substring(1, last);
         }
      } else if (FILENAME_ENCODED.equals(name)) {
         try {
            name = HttpHeaderValues.FILENAME.toString();
            String[] split = cleanString(value).split("'", 3);
            value = QueryStringDecoder.decodeComponent(split[2], Charset.forName(split[0]));
         } catch (UnsupportedCharsetException | ArrayIndexOutOfBoundsException var5) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var5);
         }
      } else {
         value = cleanString(value);
      }

      return this.factory.createAttribute(this.request, name, value);
   }

   protected InterfaceHttpData getFileUpload(String delimiter) {
      Attribute encoding = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
      Charset localCharset = this.charset;
      HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
      if (encoding != null) {
         String code;
         try {
            code = encoding.getValue().toLowerCase();
         } catch (IOException var17) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var17);
         }

         if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
            localCharset = CharsetUtil.US_ASCII;
         } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
            localCharset = CharsetUtil.ISO_8859_1;
            mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
         } else {
            if (!code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + code);
            }

            mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
         }
      }

      Attribute charsetAttribute = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
      if (charsetAttribute != null) {
         try {
            localCharset = Charset.forName(charsetAttribute.getValue());
         } catch (UnsupportedCharsetException | IOException var16) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var16);
         }
      }

      if (this.currentFileUpload == null) {
         Attribute filenameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
         Attribute nameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
         Attribute contentTypeAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TYPE);
         Attribute lengthAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);

         long size;
         try {
            size = lengthAttribute != null ? Long.parseLong(lengthAttribute.getValue()) : 0L;
         } catch (IOException var14) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
         } catch (NumberFormatException var15) {
            size = 0L;
         }

         try {
            String contentType;
            if (contentTypeAttribute != null) {
               contentType = contentTypeAttribute.getValue();
            } else {
               contentType = "application/octet-stream";
            }

            this.currentFileUpload = this.factory
               .createFileUpload(
                  this.request,
                  cleanString(nameAttribute.getValue()),
                  cleanString(filenameAttribute.getValue()),
                  contentType,
                  mechanism.value(),
                  localCharset,
                  size
               );
         } catch (IllegalArgumentException | IOException | NullPointerException var13) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var13);
         }
      }

      if (!loadDataMultipartOptimized(this.undecodedChunk, delimiter, this.currentFileUpload)) {
         return null;
      } else if (this.currentFileUpload.isCompleted()) {
         if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
            this.currentFieldAttributes = null;
         } else {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
            this.cleanMixedAttributes();
         }

         FileUpload fileUpload = this.currentFileUpload;
         this.currentFileUpload = null;
         return fileUpload;
      } else {
         return null;
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

   private void cleanMixedAttributes() {
      this.currentFieldAttributes.remove(HttpHeaderValues.CHARSET);
      this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_LENGTH);
      this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
      this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TYPE);
      this.currentFieldAttributes.remove(HttpHeaderValues.FILENAME);
   }

   private static String readLineOptimized(ByteBuf undecodedChunk, Charset charset) {
      int readerIndex = undecodedChunk.readerIndex();
      ByteBuf line = null;

      try {
         if (undecodedChunk.isReadable()) {
            int posLfOrCrLf = HttpPostBodyUtil.findLineBreak(undecodedChunk, undecodedChunk.readerIndex());
            if (posLfOrCrLf <= 0) {
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            String var6;
            try {
               line = undecodedChunk.alloc().heapBuffer(posLfOrCrLf);
               line.writeBytes(undecodedChunk, posLfOrCrLf);
               byte nextByte = undecodedChunk.readByte();
               if (nextByte == 13) {
                  undecodedChunk.readByte();
               }

               var6 = line.toString(charset);
            } finally {
               line.release();
            }

            return var6;
         }
      } catch (IndexOutOfBoundsException var11) {
         undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var11);
      }

      undecodedChunk.readerIndex(readerIndex);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private static String readDelimiterOptimized(ByteBuf undecodedChunk, String delimiter, Charset charset) {
      int readerIndex = undecodedChunk.readerIndex();
      byte[] bdelimiter = delimiter.getBytes(charset);
      int delimiterLength = bdelimiter.length;

      try {
         int delimiterPos = HttpPostBodyUtil.findDelimiter(undecodedChunk, readerIndex, bdelimiter, false);
         if (delimiterPos < 0) {
            undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
         }

         StringBuilder sb = new StringBuilder(delimiter);
         undecodedChunk.readerIndex(readerIndex + delimiterPos + delimiterLength);
         if (undecodedChunk.isReadable()) {
            byte nextByte = undecodedChunk.readByte();
            if (nextByte == 13) {
               nextByte = undecodedChunk.readByte();
               if (nextByte == 10) {
                  return sb.toString();
               }

               undecodedChunk.readerIndex(readerIndex);
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            if (nextByte == 10) {
               return sb.toString();
            }

            if (nextByte == 45) {
               sb.append('-');
               nextByte = undecodedChunk.readByte();
               if (nextByte == 45) {
                  sb.append('-');
                  if (undecodedChunk.isReadable()) {
                     nextByte = undecodedChunk.readByte();
                     if (nextByte == 13) {
                        nextByte = undecodedChunk.readByte();
                        if (nextByte == 10) {
                           return sb.toString();
                        }

                        undecodedChunk.readerIndex(readerIndex);
                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                     }

                     if (nextByte == 10) {
                        return sb.toString();
                     }

                     undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
                     return sb.toString();
                  }

                  return sb.toString();
               }
            }
         }
      } catch (IndexOutOfBoundsException var9) {
         undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var9);
      }

      undecodedChunk.readerIndex(readerIndex);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private static void rewriteCurrentBuffer(ByteBuf buffer, int lengthToSkip) {
      if (lengthToSkip != 0) {
         int readerIndex = buffer.readerIndex();
         int readableBytes = buffer.readableBytes();
         if (readableBytes == lengthToSkip) {
            buffer.readerIndex(readerIndex);
            buffer.writerIndex(readerIndex);
         } else {
            buffer.setBytes(readerIndex, buffer, readerIndex + lengthToSkip, readableBytes - lengthToSkip);
            buffer.readerIndex(readerIndex);
            buffer.writerIndex(readerIndex + readableBytes - lengthToSkip);
         }
      }
   }

   private static boolean loadDataMultipartOptimized(ByteBuf undecodedChunk, String delimiter, HttpData httpData) {
      if (!undecodedChunk.isReadable()) {
         return false;
      } else {
         int startReaderIndex = undecodedChunk.readerIndex();
         byte[] bdelimiter = delimiter.getBytes(httpData.getCharset());
         int posDelimiter = HttpPostBodyUtil.findDelimiter(undecodedChunk, startReaderIndex, bdelimiter, true);
         if (posDelimiter < 0) {
            int readableBytes = undecodedChunk.readableBytes();
            int lastPosition = readableBytes - bdelimiter.length - 1;
            if (lastPosition < 0) {
               lastPosition = 0;
            }

            posDelimiter = HttpPostBodyUtil.findLastLineBreak(undecodedChunk, startReaderIndex + lastPosition);
            if (posDelimiter < 0
               && httpData.definedLength() == httpData.length() + readableBytes - 1L
               && undecodedChunk.getByte(readableBytes + startReaderIndex - 1) == 13) {
               lastPosition = 0;
               posDelimiter = readableBytes - 1;
            }

            if (posDelimiter < 0) {
               ByteBuf content = undecodedChunk.copy();

               try {
                  httpData.addContent(content, false);
               } catch (IOException var10) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
               }

               undecodedChunk.readerIndex(startReaderIndex);
               undecodedChunk.writerIndex(startReaderIndex);
               return false;
            } else {
               posDelimiter += lastPosition;
               if (posDelimiter == 0) {
                  return false;
               } else {
                  ByteBuf content = undecodedChunk.copy(startReaderIndex, posDelimiter);

                  try {
                     httpData.addContent(content, false);
                  } catch (IOException var11) {
                     throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
                  }

                  rewriteCurrentBuffer(undecodedChunk, posDelimiter);
                  return false;
               }
            }
         } else {
            ByteBuf content = undecodedChunk.copy(startReaderIndex, posDelimiter);

            try {
               httpData.addContent(content, true);
            } catch (IOException var12) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
            }

            rewriteCurrentBuffer(undecodedChunk, posDelimiter);
            return true;
         }
      }
   }

   private static String cleanString(String field) {
      int size = field.length();
      StringBuilder sb = new StringBuilder(size);

      for (int i = 0; i < size; i++) {
         char nextChar = field.charAt(i);
         switch (nextChar) {
            case '\t':
            case ',':
            case ':':
            case ';':
            case '=':
               sb.append(' ');
            case '"':
               break;
            default:
               sb.append(nextChar);
         }
      }

      return sb.toString().trim();
   }

   private boolean skipOneLine() {
      if (!this.undecodedChunk.isReadable()) {
         return false;
      } else {
         byte nextByte = this.undecodedChunk.readByte();
         if (nextByte == 13) {
            if (!this.undecodedChunk.isReadable()) {
               this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
               return false;
            } else {
               nextByte = this.undecodedChunk.readByte();
               if (nextByte == 10) {
                  return true;
               } else {
                  this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
                  return false;
               }
            }
         } else if (nextByte == 10) {
            return true;
         } else {
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
            return false;
         }
      }
   }

   private static String[] splitMultipartHeader(String sb) {
      ArrayList<String> headers = new ArrayList<>(1);
      int nameStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);

      int nameEnd;
      for (nameEnd = nameStart; nameEnd < sb.length(); nameEnd++) {
         char ch = sb.charAt(nameEnd);
         if (ch == ':' || Character.isWhitespace(ch)) {
            break;
         }
      }

      int colonEnd;
      for (colonEnd = nameEnd; colonEnd < sb.length(); colonEnd++) {
         if (sb.charAt(colonEnd) == ':') {
            colonEnd++;
            break;
         }
      }

      int valueStart = HttpPostBodyUtil.findNonWhitespace(sb, colonEnd);
      int valueEnd = HttpPostBodyUtil.findEndOfString(sb);
      headers.add(sb.substring(nameStart, nameEnd));
      String svalue = valueStart >= valueEnd ? "" : sb.substring(valueStart, valueEnd);
      String[] values;
      if (svalue.indexOf(59) >= 0) {
         values = splitMultipartHeaderValues(svalue);
      } else {
         values = svalue.split(",");
      }

      for (String value : values) {
         headers.add(value.trim());
      }

      String[] array = new String[headers.size()];

      for (int i = 0; i < headers.size(); i++) {
         array[i] = headers.get(i);
      }

      return array;
   }

   private static String[] splitMultipartHeaderValues(String svalue) {
      List<String> values = InternalThreadLocalMap.get().arrayList(1);
      boolean inQuote = false;
      boolean escapeNext = false;
      int start = 0;

      for (int i = 0; i < svalue.length(); i++) {
         char c = svalue.charAt(i);
         if (inQuote) {
            if (escapeNext) {
               escapeNext = false;
            } else if (c == '\\') {
               escapeNext = true;
            } else if (c == '"') {
               inQuote = false;
            }
         } else if (c == '"') {
            inQuote = true;
         } else if (c == ';') {
            values.add(svalue.substring(start, i));
            start = i + 1;
         }
      }

      values.add(svalue.substring(start));
      return values.toArray(EmptyArrays.EMPTY_STRINGS);
   }

   int getCurrentAllocatedCapacity() {
      return this.undecodedChunk.capacity();
   }
}
