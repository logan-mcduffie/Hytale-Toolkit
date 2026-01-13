package com.nimbusds.jose.util;

import java.util.List;
import java.util.Map;

public interface RestrictedResourceRetriever extends ResourceRetriever {
   int getConnectTimeout();

   void setConnectTimeout(int var1);

   int getReadTimeout();

   void setReadTimeout(int var1);

   int getSizeLimit();

   void setSizeLimit(int var1);

   Map<String, List<String>> getHeaders();

   void setHeaders(Map<String, List<String>> var1);
}
