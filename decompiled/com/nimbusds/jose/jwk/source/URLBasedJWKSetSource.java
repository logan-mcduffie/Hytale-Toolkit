package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@ThreadSafe
public class URLBasedJWKSetSource<C extends SecurityContext> implements JWKSetSource<C> {
   private final URL url;
   private final ResourceRetriever resourceRetriever;

   public URLBasedJWKSetSource(URL url, ResourceRetriever resourceRetriever) {
      Objects.requireNonNull(url, "The URL must not be null");
      this.url = url;
      Objects.requireNonNull(resourceRetriever, "The resource retriever must not be null");
      this.resourceRetriever = resourceRetriever;
   }

   public URL getJWKSetURL() {
      return this.url;
   }

   public ResourceRetriever getResourceRetriever() {
      return this.resourceRetriever;
   }

   @Override
   public JWKSet getJWKSet(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
      Resource resource;
      try {
         resource = this.getResourceRetriever().retrieveResource(this.getJWKSetURL());
      } catch (IOException var8) {
         throw new JWKSetRetrievalException("Couldn't retrieve JWK set from URL: " + var8.getMessage(), var8);
      }

      try {
         return JWKSet.parse(resource.getContent());
      } catch (Exception var7) {
         throw new JWKSetParseException("Unable to parse JWK set", var7);
      }
   }

   @Override
   public void close() throws IOException {
   }
}
