package org.bouncycastle.jcajce.provider.config;

import java.io.OutputStream;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;

/** @deprecated */
public class PKCS12StoreParameter extends org.bouncycastle.jcajce.PKCS12StoreParameter {
   public PKCS12StoreParameter(OutputStream var1, char[] var2) {
      super(var1, var2, false);
   }

   public PKCS12StoreParameter(OutputStream var1, ProtectionParameter var2) {
      super(var1, var2, false);
   }

   public PKCS12StoreParameter(OutputStream var1, char[] var2, boolean var3) {
      super(var1, new PasswordProtection(var2), var3);
   }

   public PKCS12StoreParameter(OutputStream var1, char[] var2, boolean var3, boolean var4) {
      super(var1, new PasswordProtection(var2), var3, var4);
   }

   public PKCS12StoreParameter(OutputStream var1, ProtectionParameter var2, boolean var3) {
      super(var1, var2, var3);
   }
}
