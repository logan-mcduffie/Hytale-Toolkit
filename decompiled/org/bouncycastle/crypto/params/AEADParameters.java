package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

public class AEADParameters implements CipherParameters {
   private byte[] associatedText;
   private byte[] nonce;
   private KeyParameter key;
   private int macSize;

   public AEADParameters(KeyParameter var1, int var2, byte[] var3) {
      this(var1, var2, var3, null);
   }

   public AEADParameters(KeyParameter var1, int var2, byte[] var3, byte[] var4) {
      this.key = var1;
      this.nonce = Arrays.clone(var3);
      this.macSize = var2;
      this.associatedText = Arrays.clone(var4);
   }

   public KeyParameter getKey() {
      return this.key;
   }

   public int getMacSize() {
      return this.macSize;
   }

   public byte[] getAssociatedText() {
      return Arrays.clone(this.associatedText);
   }

   public byte[] getNonce() {
      return Arrays.clone(this.nonce);
   }
}
