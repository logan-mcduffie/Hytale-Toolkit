package org.bouncycastle.jcajce;

import java.io.OutputStream;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import org.bouncycastle.crypto.util.PBKDFConfig;

/** @deprecated */
public class BCFKSStoreParameter implements LoadStoreParameter {
   private final ProtectionParameter protectionParameter;
   private final PBKDFConfig storeConfig;
   private OutputStream out;

   public BCFKSStoreParameter(OutputStream var1, PBKDFConfig var2, char[] var3) {
      this(var1, var2, new PasswordProtection(var3));
   }

   public BCFKSStoreParameter(OutputStream var1, PBKDFConfig var2, ProtectionParameter var3) {
      this.out = var1;
      this.storeConfig = var2;
      this.protectionParameter = var3;
   }

   @Override
   public ProtectionParameter getProtectionParameter() {
      return this.protectionParameter;
   }

   public OutputStream getOutputStream() {
      return this.out;
   }

   public PBKDFConfig getStorePBKDFConfig() {
      return this.storeConfig;
   }
}
