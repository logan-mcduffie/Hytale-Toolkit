package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpContent;
import java.util.List;

public interface InterfaceHttpPostRequestDecoder {
   boolean isMultipart();

   void setDiscardThreshold(int var1);

   int getDiscardThreshold();

   List<InterfaceHttpData> getBodyHttpDatas();

   List<InterfaceHttpData> getBodyHttpDatas(String var1);

   InterfaceHttpData getBodyHttpData(String var1);

   InterfaceHttpPostRequestDecoder offer(HttpContent var1);

   boolean hasNext();

   InterfaceHttpData next();

   InterfaceHttpData currentPartialHttpData();

   void destroy();

   void cleanFiles();

   void removeHttpDataFromClean(InterfaceHttpData var1);
}
