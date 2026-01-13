package org.bouncycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.util.Arrays;

final class WOTSPlus {
   private final WOTSPlusParameters params;
   private final KeyedHashFunctions khf;
   private byte[] secretKeySeed;
   private byte[] publicSeed;

   WOTSPlus(WOTSPlusParameters var1) {
      if (var1 == null) {
         throw new NullPointerException("params == null");
      } else {
         this.params = var1;
         int var2 = var1.getTreeDigestSize();
         this.khf = new KeyedHashFunctions(var1.getTreeDigest(), var2);
         this.secretKeySeed = new byte[var2];
         this.publicSeed = new byte[var2];
      }
   }

   void importKeys(byte[] var1, byte[] var2) {
      if (var1 == null) {
         throw new NullPointerException("secretKeySeed == null");
      } else if (var1.length != this.params.getTreeDigestSize()) {
         throw new IllegalArgumentException("size of secretKeySeed needs to be equal to size of digest");
      } else if (var2 == null) {
         throw new NullPointerException("publicSeed == null");
      } else if (var2.length != this.params.getTreeDigestSize()) {
         throw new IllegalArgumentException("size of publicSeed needs to be equal to size of digest");
      } else {
         this.secretKeySeed = var1;
         this.publicSeed = var2;
      }
   }

   WOTSPlusSignature sign(byte[] var1, OTSHashAddress var2) {
      if (var1 == null) {
         throw new NullPointerException("messageDigest == null");
      } else if (var1.length != this.params.getTreeDigestSize()) {
         throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
      } else if (var2 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else {
         List var3 = this.convertToBaseW(var1, this.params.getWinternitzParameter(), this.params.getLen1());
         int var4 = 0;

         for (int var5 = 0; var5 < this.params.getLen1(); var5++) {
            var4 += this.params.getWinternitzParameter() - 1 - var3.get(var5);
         }

         var4 <<= 8 - this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) % 8;
         int var10 = (int)Math.ceil(this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) / 8.0);
         List var6 = this.convertToBaseW(XMSSUtil.toBytesBigEndian(var4, var10), this.params.getWinternitzParameter(), this.params.getLen2());
         var3.addAll(var6);
         byte[][] var7 = new byte[this.params.getLen()][];

         for (int var8 = 0; var8 < this.params.getLen(); var8++) {
            var2 = (OTSHashAddress)new OTSHashAddress.Builder()
               .withLayerAddress(var2.getLayerAddress())
               .withTreeAddress(var2.getTreeAddress())
               .withOTSAddress(var2.getOTSAddress())
               .withChainAddress(var8)
               .withHashAddress(var2.getHashAddress())
               .withKeyAndMask(var2.getKeyAndMask())
               .build();
            var7[var8] = this.chain(this.expandSecretKeySeed(var8), 0, (Integer)var3.get(var8), var2);
         }

         return new WOTSPlusSignature(this.params, var7);
      }
   }

   WOTSPlusPublicKeyParameters getPublicKeyFromSignature(byte[] var1, WOTSPlusSignature var2, OTSHashAddress var3) {
      if (var1 == null) {
         throw new NullPointerException("messageDigest == null");
      } else if (var1.length != this.params.getTreeDigestSize()) {
         throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
      } else if (var2 == null) {
         throw new NullPointerException("signature == null");
      } else if (var3 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else {
         List var4 = this.convertToBaseW(var1, this.params.getWinternitzParameter(), this.params.getLen1());
         int var5 = 0;

         for (int var6 = 0; var6 < this.params.getLen1(); var6++) {
            var5 += this.params.getWinternitzParameter() - 1 - var4.get(var6);
         }

         var5 <<= 8 - this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) % 8;
         int var11 = (int)Math.ceil(this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) / 8.0);
         List var7 = this.convertToBaseW(XMSSUtil.toBytesBigEndian(var5, var11), this.params.getWinternitzParameter(), this.params.getLen2());
         var4.addAll(var7);
         byte[][] var8 = new byte[this.params.getLen()][];

         for (int var9 = 0; var9 < this.params.getLen(); var9++) {
            var3 = (OTSHashAddress)new OTSHashAddress.Builder()
               .withLayerAddress(var3.getLayerAddress())
               .withTreeAddress(var3.getTreeAddress())
               .withOTSAddress(var3.getOTSAddress())
               .withChainAddress(var9)
               .withHashAddress(var3.getHashAddress())
               .withKeyAndMask(var3.getKeyAndMask())
               .build();
            var8[var9] = this.chain(var2.toByteArray()[var9], (Integer)var4.get(var9), this.params.getWinternitzParameter() - 1 - (Integer)var4.get(var9), var3);
         }

         return new WOTSPlusPublicKeyParameters(this.params, var8);
      }
   }

   private byte[] chain(byte[] var1, int var2, int var3, OTSHashAddress var4) {
      int var5 = this.params.getTreeDigestSize();
      if (var1 == null) {
         throw new NullPointerException("startHash == null");
      } else if (var1.length != var5) {
         throw new IllegalArgumentException("startHash needs to be " + var5 + "bytes");
      } else if (var4 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else if (var4.toByteArray() == null) {
         throw new NullPointerException("otsHashAddress byte array == null");
      } else if (var2 + var3 > this.params.getWinternitzParameter() - 1) {
         throw new IllegalArgumentException("max chain length must not be greater than w");
      } else if (var3 == 0) {
         return var1;
      } else {
         byte[] var6 = this.chain(var1, var2, var3 - 1, var4);
         var4 = (OTSHashAddress)new OTSHashAddress.Builder()
            .withLayerAddress(var4.getLayerAddress())
            .withTreeAddress(var4.getTreeAddress())
            .withOTSAddress(var4.getOTSAddress())
            .withChainAddress(var4.getChainAddress())
            .withHashAddress(var2 + var3 - 1)
            .withKeyAndMask(0)
            .build();
         byte[] var7 = this.khf.PRF(this.publicSeed, var4.toByteArray());
         var4 = (OTSHashAddress)new OTSHashAddress.Builder()
            .withLayerAddress(var4.getLayerAddress())
            .withTreeAddress(var4.getTreeAddress())
            .withOTSAddress(var4.getOTSAddress())
            .withChainAddress(var4.getChainAddress())
            .withHashAddress(var4.getHashAddress())
            .withKeyAndMask(1)
            .build();
         byte[] var8 = this.khf.PRF(this.publicSeed, var4.toByteArray());
         byte[] var9 = new byte[var5];

         for (int var10 = 0; var10 < var5; var10++) {
            var9[var10] = (byte)(var6[var10] ^ var8[var10]);
         }

         return this.khf.F(var7, var9);
      }
   }

   private List<Integer> convertToBaseW(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("msg == null");
      } else if (var2 != 4 && var2 != 16) {
         throw new IllegalArgumentException("w needs to be 4 or 16");
      } else {
         int var4 = XMSSUtil.log2(var2);
         if (var3 > 8 * var1.length / var4) {
            throw new IllegalArgumentException("outLength too big");
         } else {
            ArrayList var5 = new ArrayList();

            for (int var6 = 0; var6 < var1.length; var6++) {
               for (int var7 = 8 - var4; var7 >= 0; var7 -= var4) {
                  var5.add(var1[var6] >> var7 & var2 - 1);
                  if (var5.size() == var3) {
                     return var5;
                  }
               }
            }

            return var5;
         }
      }
   }

   protected byte[] getWOTSPlusSecretKey(byte[] var1, OTSHashAddress var2) {
      var2 = (OTSHashAddress)new OTSHashAddress.Builder()
         .withLayerAddress(var2.getLayerAddress())
         .withTreeAddress(var2.getTreeAddress())
         .withOTSAddress(var2.getOTSAddress())
         .build();
      return this.khf.PRF(var1, var2.toByteArray());
   }

   private byte[] expandSecretKeySeed(int var1) {
      if (var1 >= 0 && var1 < this.params.getLen()) {
         return this.khf.PRF(this.secretKeySeed, XMSSUtil.toBytesBigEndian(var1, 32));
      } else {
         throw new IllegalArgumentException("index out of bounds");
      }
   }

   protected WOTSPlusParameters getParams() {
      return this.params;
   }

   protected KeyedHashFunctions getKhf() {
      return this.khf;
   }

   protected byte[] getSecretKeySeed() {
      return Arrays.clone(this.secretKeySeed);
   }

   protected byte[] getPublicSeed() {
      return Arrays.clone(this.publicSeed);
   }

   protected WOTSPlusPrivateKeyParameters getPrivateKey() {
      byte[][] var1 = new byte[this.params.getLen()][];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = this.expandSecretKeySeed(var2);
      }

      return new WOTSPlusPrivateKeyParameters(this.params, var1);
   }

   WOTSPlusPublicKeyParameters getPublicKey(OTSHashAddress var1) {
      if (var1 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else {
         byte[][] var2 = new byte[this.params.getLen()][];

         for (int var3 = 0; var3 < this.params.getLen(); var3++) {
            var1 = (OTSHashAddress)new OTSHashAddress.Builder()
               .withLayerAddress(var1.getLayerAddress())
               .withTreeAddress(var1.getTreeAddress())
               .withOTSAddress(var1.getOTSAddress())
               .withChainAddress(var3)
               .withHashAddress(var1.getHashAddress())
               .withKeyAndMask(var1.getKeyAndMask())
               .build();
            var2[var3] = this.chain(this.expandSecretKeySeed(var3), 0, this.params.getWinternitzParameter() - 1, var1);
         }

         return new WOTSPlusPublicKeyParameters(this.params, var2);
      }
   }
}
