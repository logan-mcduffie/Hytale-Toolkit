package org.bouncycastle.jcajce;

import java.io.OutputStream;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;

public class PKCS12StoreParameter implements LoadStoreParameter {
   private final OutputStream out;
   private final ProtectionParameter protectionParameter;
   private final boolean forDEREncoding;
   private final boolean overwriteFriendlyName;

   public PKCS12StoreParameter(OutputStream var1, char[] var2) {
      this(var1, var2, false);
   }

   public PKCS12StoreParameter(OutputStream var1, ProtectionParameter var2) {
      this(var1, var2, false, true);
   }

   public PKCS12StoreParameter(OutputStream var1, char[] var2, boolean var3) {
      this(var1, new PasswordProtection(var2), var3, true);
   }

   public PKCS12StoreParameter(OutputStream var1, ProtectionParameter var2, boolean var3) {
      this(var1, var2, var3, true);
   }

   public PKCS12StoreParameter(OutputStream var1, char[] var2, boolean var3, boolean var4) {
      this(var1, new PasswordProtection(var2), var3, var4);
   }

   public PKCS12StoreParameter(OutputStream var1, ProtectionParameter var2, boolean var3, boolean var4) {
      this.out = var1;
      this.protectionParameter = var2;
      this.forDEREncoding = var3;
      this.overwriteFriendlyName = var4;
   }

   public OutputStream getOutputStream() {
      return this.out;
   }

   @Override
   public ProtectionParameter getProtectionParameter() {
      return this.protectionParameter;
   }

   public boolean isForDEREncoding() {
      return this.forDEREncoding;
   }

   public boolean isOverwriteFriendlyName() {
      return this.overwriteFriendlyName;
   }
}
