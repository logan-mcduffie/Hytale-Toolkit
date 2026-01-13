package org.bouncycastle.jcajce;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;

public class BCLoadStoreParameter implements LoadStoreParameter {
   private final InputStream in;
   private final OutputStream out;
   private final ProtectionParameter protectionParameter;

   public BCLoadStoreParameter(OutputStream var1, char[] var2) {
      this(var1, new PasswordProtection(var2));
   }

   public BCLoadStoreParameter(InputStream var1, char[] var2) {
      this(var1, new PasswordProtection(var2));
   }

   public BCLoadStoreParameter(InputStream var1, ProtectionParameter var2) {
      this(var1, null, var2);
   }

   public BCLoadStoreParameter(OutputStream var1, ProtectionParameter var2) {
      this(null, var1, var2);
   }

   BCLoadStoreParameter(InputStream var1, OutputStream var2, ProtectionParameter var3) {
      this.in = var1;
      this.out = var2;
      this.protectionParameter = var3;
   }

   @Override
   public ProtectionParameter getProtectionParameter() {
      return this.protectionParameter;
   }

   public OutputStream getOutputStream() {
      if (this.out == null) {
         throw new UnsupportedOperationException("parameter not configured for storage - no OutputStream");
      } else {
         return this.out;
      }
   }

   public InputStream getInputStream() {
      if (this.out != null) {
         throw new UnsupportedOperationException("parameter configured for storage OutputStream present");
      } else {
         return this.in;
      }
   }
}
