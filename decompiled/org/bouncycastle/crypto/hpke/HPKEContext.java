package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.InvalidCipherTextException;

public class HPKEContext {
   protected final AEAD aead;
   protected final HKDF hkdf;
   protected final byte[] exporterSecret;
   protected final byte[] suiteId;

   HPKEContext(AEAD var1, HKDF var2, byte[] var3, byte[] var4) {
      this.aead = var1;
      this.hkdf = var2;
      this.exporterSecret = var3;
      this.suiteId = var4;
   }

   public byte[] export(byte[] var1, int var2) {
      return this.hkdf.LabeledExpand(this.exporterSecret, this.suiteId, "sec", var1, var2);
   }

   public byte[] seal(byte[] var1, byte[] var2) throws InvalidCipherTextException {
      return this.aead.seal(var1, var2);
   }

   public byte[] seal(byte[] var1, byte[] var2, int var3, int var4) throws InvalidCipherTextException {
      return this.aead.seal(var1, var2, var3, var4);
   }

   public byte[] open(byte[] var1, byte[] var2) throws InvalidCipherTextException {
      return this.aead.open(var1, var2);
   }

   public byte[] open(byte[] var1, byte[] var2, int var3, int var4) throws InvalidCipherTextException {
      return this.aead.open(var1, var2, var3, var4);
   }

   public byte[] extract(byte[] var1, byte[] var2) {
      return this.hkdf.Extract(var1, var2);
   }

   public byte[] expand(byte[] var1, byte[] var2, int var3) {
      return this.hkdf.Expand(var1, var2, var3);
   }
}
