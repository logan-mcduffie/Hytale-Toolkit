package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.util.Arrays;

public class CMCEPrivateKeyParameters extends CMCEKeyParameters {
   private final byte[] privateKey;

   public byte[] getPrivateKey() {
      return Arrays.clone(this.privateKey);
   }

   public CMCEPrivateKeyParameters(CMCEParameters var1, byte[] var2) {
      super(true, var1);
      this.privateKey = Arrays.clone(var2);
   }

   public CMCEPrivateKeyParameters(CMCEParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      super(true, var1);
      int var7 = var2.length + var3.length + var4.length + var5.length + var6.length;
      this.privateKey = new byte[var7];
      int var8 = 0;
      System.arraycopy(var2, 0, this.privateKey, var8, var2.length);
      var8 += var2.length;
      System.arraycopy(var3, 0, this.privateKey, var8, var3.length);
      var8 += var3.length;
      System.arraycopy(var4, 0, this.privateKey, var8, var4.length);
      var8 += var4.length;
      System.arraycopy(var5, 0, this.privateKey, var8, var5.length);
      var8 += var5.length;
      System.arraycopy(var6, 0, this.privateKey, var8, var6.length);
   }

   public byte[] reconstructPublicKey() {
      CMCEEngine var1 = this.getParameters().getEngine();
      return var1.generate_public_key_from_private_key(this.privateKey);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.privateKey);
   }

   public byte[] getDelta() {
      return Arrays.copyOfRange(this.privateKey, 0, 32);
   }

   public byte[] getC() {
      return Arrays.copyOfRange(this.privateKey, 32, 40);
   }

   public byte[] getG() {
      return Arrays.copyOfRange(this.privateKey, 40, 40 + this.getParameters().getT() * 2);
   }

   public byte[] getAlpha() {
      return Arrays.copyOfRange(this.privateKey, 40 + this.getParameters().getT() * 2, this.privateKey.length - 32);
   }

   public byte[] getS() {
      return Arrays.copyOfRange(this.privateKey, this.privateKey.length - 32, this.privateKey.length);
   }
}
