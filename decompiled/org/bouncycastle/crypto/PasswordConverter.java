package org.bouncycastle.crypto;

public enum PasswordConverter implements CharToByteConverter {
   ASCII {
      @Override
      public String getType() {
         return "ASCII";
      }

      @Override
      public byte[] convert(char[] var1) {
         return PBEParametersGenerator.PKCS5PasswordToBytes(var1);
      }
   },
   UTF8 {
      @Override
      public String getType() {
         return "UTF8";
      }

      @Override
      public byte[] convert(char[] var1) {
         return PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(var1);
      }
   },
   PKCS12 {
      @Override
      public String getType() {
         return "PKCS12";
      }

      @Override
      public byte[] convert(char[] var1) {
         return PBEParametersGenerator.PKCS12PasswordToBytes(var1);
      }
   };

   private PasswordConverter() {
   }
}
