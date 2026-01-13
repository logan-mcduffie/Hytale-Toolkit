package org.bouncycastle.est;

import java.net.URL;
import org.bouncycastle.util.Arrays;

public class ESTRequestBuilder {
   private final String method;
   private URL url;
   private HttpUtil.Headers headers;
   ESTHijacker hijacker;
   ESTSourceConnectionListener listener;
   ESTClient client;
   private byte[] data;

   public ESTRequestBuilder(ESTRequest var1) {
      this.method = var1.method;
      this.url = var1.url;
      this.listener = var1.listener;
      this.data = var1.data;
      this.hijacker = var1.hijacker;
      this.headers = (HttpUtil.Headers)var1.headers.clone();
      this.client = var1.getClient();
   }

   public ESTRequestBuilder(String var1, URL var2) {
      this.method = var1;
      this.url = var2;
      this.headers = new HttpUtil.Headers();
   }

   public ESTRequestBuilder withConnectionListener(ESTSourceConnectionListener var1) {
      this.listener = var1;
      return this;
   }

   public ESTRequestBuilder withHijacker(ESTHijacker var1) {
      this.hijacker = var1;
      return this;
   }

   public ESTRequestBuilder withURL(URL var1) {
      this.url = var1;
      return this;
   }

   public ESTRequestBuilder withData(byte[] var1) {
      this.data = Arrays.clone(var1);
      return this;
   }

   public ESTRequestBuilder addHeader(String var1, String var2) {
      this.headers.add(var1, var2);
      return this;
   }

   public ESTRequestBuilder setHeader(String var1, String var2) {
      this.headers.set(var1, var2);
      return this;
   }

   public ESTRequestBuilder withClient(ESTClient var1) {
      this.client = var1;
      return this;
   }

   public ESTRequest build() {
      return new ESTRequest(this.method, this.url, this.data, this.hijacker, this.listener, this.headers, this.client);
   }
}
