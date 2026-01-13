package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.Digest;

class Utils {
   static CryptoServiceProperties getDefaultProperties(Digest var0, CryptoServicePurpose var1) {
      return new Utils.DefaultProperties(var0.getDigestSize() * 4, var0.getAlgorithmName(), var1);
   }

   static CryptoServiceProperties getDefaultProperties(Digest var0, int var1, CryptoServicePurpose var2) {
      return new Utils.DefaultPropertiesWithPRF(var0.getDigestSize() * 4, var1, var0.getAlgorithmName(), var2);
   }

   private static class DefaultProperties implements CryptoServiceProperties {
      private final int bitsOfSecurity;
      private final String algorithmName;
      private final CryptoServicePurpose purpose;

      public DefaultProperties(int var1, String var2, CryptoServicePurpose var3) {
         this.bitsOfSecurity = var1;
         this.algorithmName = var2;
         this.purpose = var3;
      }

      @Override
      public int bitsOfSecurity() {
         return this.bitsOfSecurity;
      }

      @Override
      public String getServiceName() {
         return this.algorithmName;
      }

      @Override
      public CryptoServicePurpose getPurpose() {
         return this.purpose;
      }

      @Override
      public Object getParams() {
         return null;
      }
   }

   private static class DefaultPropertiesWithPRF implements CryptoServiceProperties {
      private final int bitsOfSecurity;
      private final int prfBitsOfSecurity;
      private final String algorithmName;
      private final CryptoServicePurpose purpose;

      public DefaultPropertiesWithPRF(int var1, int var2, String var3, CryptoServicePurpose var4) {
         this.bitsOfSecurity = var1;
         this.prfBitsOfSecurity = var2;
         this.algorithmName = var3;
         this.purpose = var4;
      }

      @Override
      public int bitsOfSecurity() {
         return this.purpose == CryptoServicePurpose.PRF ? this.prfBitsOfSecurity : this.bitsOfSecurity;
      }

      @Override
      public String getServiceName() {
         return this.algorithmName;
      }

      @Override
      public CryptoServicePurpose getPurpose() {
         return this.purpose;
      }

      @Override
      public Object getParams() {
         return null;
      }
   }
}
