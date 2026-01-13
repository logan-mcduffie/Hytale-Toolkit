package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultHttpDataFactory implements HttpDataFactory {
   public static final long MINSIZE = 16384L;
   public static final long MAXSIZE = -1L;
   private final boolean useDisk;
   private final boolean checkSize;
   private long minSize;
   private long maxSize = -1L;
   private Charset charset = HttpConstants.DEFAULT_CHARSET;
   private String baseDir;
   private boolean deleteOnExit;
   private final Map<HttpRequest, List<HttpData>> requestFileDeleteMap = Collections.synchronizedMap(new IdentityHashMap<>());

   public DefaultHttpDataFactory() {
      this.useDisk = false;
      this.checkSize = true;
      this.minSize = 16384L;
   }

   public DefaultHttpDataFactory(Charset charset) {
      this();
      this.charset = charset;
   }

   public DefaultHttpDataFactory(boolean useDisk) {
      this.useDisk = useDisk;
      this.checkSize = false;
   }

   public DefaultHttpDataFactory(boolean useDisk, Charset charset) {
      this(useDisk);
      this.charset = charset;
   }

   public DefaultHttpDataFactory(long minSize) {
      this.useDisk = false;
      this.checkSize = true;
      this.minSize = minSize;
   }

   public DefaultHttpDataFactory(long minSize, Charset charset) {
      this(minSize);
      this.charset = charset;
   }

   public void setBaseDir(String baseDir) {
      this.baseDir = baseDir;
   }

   public void setDeleteOnExit(boolean deleteOnExit) {
      this.deleteOnExit = deleteOnExit;
   }

   @Override
   public void setMaxLimit(long maxSize) {
      this.maxSize = maxSize;
   }

   private List<HttpData> getList(HttpRequest request) {
      List<HttpData> list = this.requestFileDeleteMap.get(request);
      if (list == null) {
         list = new ArrayList<>();
         this.requestFileDeleteMap.put(request, list);
      }

      return list;
   }

   @Override
   public Attribute createAttribute(HttpRequest request, String name) {
      if (this.useDisk) {
         Attribute attribute = new DiskAttribute(name, this.charset, this.baseDir, this.deleteOnExit);
         attribute.setMaxSize(this.maxSize);
         List<HttpData> list = this.getList(request);
         list.add(attribute);
         return attribute;
      } else if (this.checkSize) {
         Attribute attribute = new MixedAttribute(name, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
         attribute.setMaxSize(this.maxSize);
         List<HttpData> list = this.getList(request);
         list.add(attribute);
         return attribute;
      } else {
         MemoryAttribute attribute = new MemoryAttribute(name);
         attribute.setMaxSize(this.maxSize);
         return attribute;
      }
   }

   @Override
   public Attribute createAttribute(HttpRequest request, String name, long definedSize) {
      if (this.useDisk) {
         Attribute attribute = new DiskAttribute(name, definedSize, this.charset, this.baseDir, this.deleteOnExit);
         attribute.setMaxSize(this.maxSize);
         List<HttpData> list = this.getList(request);
         list.add(attribute);
         return attribute;
      } else if (this.checkSize) {
         Attribute attribute = new MixedAttribute(name, definedSize, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
         attribute.setMaxSize(this.maxSize);
         List<HttpData> list = this.getList(request);
         list.add(attribute);
         return attribute;
      } else {
         MemoryAttribute attribute = new MemoryAttribute(name, definedSize);
         attribute.setMaxSize(this.maxSize);
         return attribute;
      }
   }

   private static void checkHttpDataSize(HttpData data) {
      try {
         data.checkSize(data.length());
      } catch (IOException var2) {
         throw new IllegalArgumentException("Attribute bigger than maxSize allowed");
      }
   }

   @Override
   public Attribute createAttribute(HttpRequest request, String name, String value) {
      if (this.useDisk) {
         Attribute attribute;
         try {
            attribute = new DiskAttribute(name, value, this.charset, this.baseDir, this.deleteOnExit);
            attribute.setMaxSize(this.maxSize);
         } catch (IOException var6) {
            attribute = new MixedAttribute(name, value, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
            attribute.setMaxSize(this.maxSize);
         }

         checkHttpDataSize(attribute);
         List<HttpData> list = this.getList(request);
         list.add(attribute);
         return attribute;
      } else if (this.checkSize) {
         Attribute attribute = new MixedAttribute(name, value, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
         attribute.setMaxSize(this.maxSize);
         checkHttpDataSize(attribute);
         List<HttpData> list = this.getList(request);
         list.add(attribute);
         return attribute;
      } else {
         try {
            MemoryAttribute attribute = new MemoryAttribute(name, value, this.charset);
            attribute.setMaxSize(this.maxSize);
            checkHttpDataSize(attribute);
            return attribute;
         } catch (IOException var7) {
            throw new IllegalArgumentException(var7);
         }
      }
   }

   @Override
   public FileUpload createFileUpload(
      HttpRequest request, String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size
   ) {
      if (this.useDisk) {
         FileUpload fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size, this.baseDir, this.deleteOnExit);
         fileUpload.setMaxSize(this.maxSize);
         checkHttpDataSize(fileUpload);
         List<HttpData> list = this.getList(request);
         list.add(fileUpload);
         return fileUpload;
      } else if (this.checkSize) {
         FileUpload fileUpload = new MixedFileUpload(
            name, filename, contentType, contentTransferEncoding, charset, size, this.minSize, this.baseDir, this.deleteOnExit
         );
         fileUpload.setMaxSize(this.maxSize);
         checkHttpDataSize(fileUpload);
         List<HttpData> list = this.getList(request);
         list.add(fileUpload);
         return fileUpload;
      } else {
         MemoryFileUpload fileUpload = new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
         fileUpload.setMaxSize(this.maxSize);
         checkHttpDataSize(fileUpload);
         return fileUpload;
      }
   }

   @Override
   public void removeHttpDataFromClean(HttpRequest request, InterfaceHttpData data) {
      if (data instanceof HttpData) {
         List<HttpData> list = this.requestFileDeleteMap.get(request);
         if (list != null) {
            Iterator<HttpData> i = list.iterator();

            while (i.hasNext()) {
               HttpData n = i.next();
               if (n == data) {
                  i.remove();
                  if (list.isEmpty()) {
                     this.requestFileDeleteMap.remove(request);
                  }

                  return;
               }
            }
         }
      }
   }

   @Override
   public void cleanRequestHttpData(HttpRequest request) {
      List<HttpData> list = this.requestFileDeleteMap.remove(request);
      if (list != null) {
         for (HttpData data : list) {
            data.release();
         }
      }
   }

   @Override
   public void cleanAllHttpData() {
      Iterator<Entry<HttpRequest, List<HttpData>>> i = this.requestFileDeleteMap.entrySet().iterator();

      while (i.hasNext()) {
         Entry<HttpRequest, List<HttpData>> e = i.next();

         for (HttpData data : e.getValue()) {
            data.release();
         }

         i.remove();
      }
   }

   @Override
   public void cleanRequestHttpDatas(HttpRequest request) {
      this.cleanRequestHttpData(request);
   }

   @Override
   public void cleanAllHttpDatas() {
      this.cleanAllHttpData();
   }
}
