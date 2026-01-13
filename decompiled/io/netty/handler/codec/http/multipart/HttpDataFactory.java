package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpRequest;
import java.nio.charset.Charset;

public interface HttpDataFactory {
   void setMaxLimit(long var1);

   Attribute createAttribute(HttpRequest var1, String var2);

   Attribute createAttribute(HttpRequest var1, String var2, long var3);

   Attribute createAttribute(HttpRequest var1, String var2, String var3);

   FileUpload createFileUpload(HttpRequest var1, String var2, String var3, String var4, String var5, Charset var6, long var7);

   void removeHttpDataFromClean(HttpRequest var1, InterfaceHttpData var2);

   void cleanRequestHttpData(HttpRequest var1);

   void cleanAllHttpData();

   @Deprecated
   void cleanRequestHttpDatas(HttpRequest var1);

   @Deprecated
   void cleanAllHttpDatas();
}
