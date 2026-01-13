package org.bouncycastle.crypto.constraints;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;

public class DefaultServiceProperties implements CryptoServiceProperties {
   private final String algorithm;
   private final int bitsOfSecurity;
   private final Object params;
   private final CryptoServicePurpose purpose;

   public DefaultServiceProperties(String var1, int var2) {
      this(var1, var2, null, CryptoServicePurpose.ANY);
   }

   public DefaultServiceProperties(String var1, int var2, Object var3) {
      this(var1, var2, var3, CryptoServicePurpose.ANY);
   }

   public DefaultServiceProperties(String var1, int var2, Object var3, CryptoServicePurpose var4) {
      this.algorithm = var1;
      this.bitsOfSecurity = var2;
      this.params = var3;
      if (var3 instanceof CryptoServicePurpose) {
         throw new IllegalArgumentException("params should not be CryptoServicePurpose");
      } else {
         this.purpose = var4;
      }
   }

   @Override
   public int bitsOfSecurity() {
      return this.bitsOfSecurity;
   }

   @Override
   public String getServiceName() {
      return this.algorithm;
   }

   @Override
   public CryptoServicePurpose getPurpose() {
      return this.purpose;
   }

   @Override
   public Object getParams() {
      return this.params;
   }
}
