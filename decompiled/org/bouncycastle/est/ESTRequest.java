package org.bouncycastle.est;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

public class ESTRequest {
   final String method;
   final URL url;
   HttpUtil.Headers headers = new HttpUtil.Headers();
   final byte[] data;
   final ESTHijacker hijacker;
   final ESTClient estClient;
   final ESTSourceConnectionListener listener;

   ESTRequest(String var1, URL var2, byte[] var3, ESTHijacker var4, ESTSourceConnectionListener var5, HttpUtil.Headers var6, ESTClient var7) {
      this.method = var1;
      this.url = var2;
      this.data = var3;
      this.hijacker = var4;
      this.listener = var5;
      this.headers = var6;
      this.estClient = var7;
   }

   public String getMethod() {
      return this.method;
   }

   public URL getURL() {
      return this.url;
   }

   public Map<String, String[]> getHeaders() {
      return (Map<String, String[]>)this.headers.clone();
   }

   public ESTHijacker getHijacker() {
      return this.hijacker;
   }

   public ESTClient getClient() {
      return this.estClient;
   }

   public ESTSourceConnectionListener getListener() {
      return this.listener;
   }

   public void writeData(OutputStream var1) throws IOException {
      if (this.data != null) {
         var1.write(this.data);
      }
   }
}
