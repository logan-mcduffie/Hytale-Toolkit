package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.CharToByteConverter;

public class PBKDF1Key implements PBKDFKey {
   private final char[] password;
   private final CharToByteConverter converter;

   public PBKDF1Key(char[] var1, CharToByteConverter var2) {
      this.password = new char[var1.length];
      this.converter = var2;
      System.arraycopy(var1, 0, this.password, 0, var1.length);
   }

   public char[] getPassword() {
      return this.password;
   }

   @Override
   public String getAlgorithm() {
      return "PBKDF1";
   }

   @Override
   public String getFormat() {
      return this.converter.getType();
   }

   @Override
   public byte[] getEncoded() {
      return this.converter.convert(this.password);
   }
}
