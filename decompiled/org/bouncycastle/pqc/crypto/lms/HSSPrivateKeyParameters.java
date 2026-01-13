package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.util.io.Streams;

public class HSSPrivateKeyParameters extends LMSKeyParameters implements LMSContextBasedSigner {
   private final int l;
   private final boolean isShard;
   private List<LMSPrivateKeyParameters> keys;
   private List<LMSSignature> sig;
   private final long indexLimit;
   private long index = 0L;
   private HSSPublicKeyParameters publicKey;

   public HSSPrivateKeyParameters(LMSPrivateKeyParameters var1, long var2, long var4) {
      super(true);
      this.l = 1;
      this.keys = Collections.singletonList(var1);
      this.sig = Collections.emptyList();
      this.index = var2;
      this.indexLimit = var4;
      this.isShard = false;
      this.resetKeyToIndex();
   }

   public HSSPrivateKeyParameters(int var1, List<LMSPrivateKeyParameters> var2, List<LMSSignature> var3, long var4, long var6) {
      super(true);
      this.l = var1;
      this.keys = Collections.unmodifiableList(var2);
      this.sig = Collections.unmodifiableList(var3);
      this.index = var4;
      this.indexLimit = var6;
      this.isShard = false;
      this.resetKeyToIndex();
   }

   private HSSPrivateKeyParameters(int var1, List<LMSPrivateKeyParameters> var2, List<LMSSignature> var3, long var4, long var6, boolean var8) {
      super(true);
      this.l = var1;
      this.keys = Collections.unmodifiableList(var2);
      this.sig = Collections.unmodifiableList(var3);
      this.index = var4;
      this.indexLimit = var6;
      this.isShard = var8;
   }

   public static HSSPrivateKeyParameters getInstance(byte[] var0, byte[] var1) throws IOException {
      HSSPrivateKeyParameters var2 = getInstance(var0);
      var2.publicKey = HSSPublicKeyParameters.getInstance(var1);
      return var2;
   }

   public static HSSPrivateKeyParameters getInstance(Object var0) throws IOException {
      if (var0 instanceof HSSPrivateKeyParameters) {
         return (HSSPrivateKeyParameters)var0;
      } else if (var0 instanceof DataInputStream) {
         if (((DataInputStream)var0).readInt() != 0) {
            throw new IllegalStateException("unknown version for hss private key");
         } else {
            int var15 = ((DataInputStream)var0).readInt();
            long var16 = ((DataInputStream)var0).readLong();
            long var17 = ((DataInputStream)var0).readLong();
            boolean var6 = ((DataInputStream)var0).readBoolean();
            ArrayList var7 = new ArrayList();
            ArrayList var8 = new ArrayList();

            for (int var9 = 0; var9 < var15; var9++) {
               var7.add(LMSPrivateKeyParameters.getInstance(var0));
            }

            for (int var18 = 0; var18 < var15 - 1; var18++) {
               var8.add(LMSSignature.getInstance(var0));
            }

            return new HSSPrivateKeyParameters(var15, var7, var8, var16, var17, var6);
         }
      } else if (var0 instanceof byte[]) {
         DataInputStream var1 = null;

         HSSPrivateKeyParameters var4;
         try {
            var1 = new DataInputStream(new ByteArrayInputStream((byte[])var0));

            try {
               return getInstance(var1);
            } catch (Exception var13) {
               LMSPrivateKeyParameters var3 = LMSPrivateKeyParameters.getInstance(var0);
               var4 = new HSSPrivateKeyParameters(var3, var3.getIndex(), var3.getIndexLimit());
            }
         } finally {
            if (var1 != null) {
               var1.close();
            }
         }

         return var4;
      } else if (var0 instanceof InputStream) {
         return getInstance(Streams.readAll((InputStream)var0));
      } else {
         throw new IllegalArgumentException("cannot parse " + var0);
      }
   }

   public int getL() {
      return this.l;
   }

   public synchronized long getIndex() {
      return this.index;
   }

   public synchronized LMSParameters[] getLMSParameters() {
      int var1 = this.keys.size();
      LMSParameters[] var2 = new LMSParameters[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         LMSPrivateKeyParameters var4 = this.keys.get(var3);
         var2[var3] = new LMSParameters(var4.getSigParameters(), var4.getOtsParameters());
      }

      return var2;
   }

   synchronized void incIndex() {
      this.index++;
   }

   private static HSSPrivateKeyParameters makeCopy(HSSPrivateKeyParameters var0) {
      try {
         return getInstance(var0.getEncoded());
      } catch (Exception var2) {
         throw new RuntimeException(var2.getMessage(), var2);
      }
   }

   protected void updateHierarchy(LMSPrivateKeyParameters[] var1, LMSSignature[] var2) {
      synchronized (this) {
         this.keys = Collections.unmodifiableList(Arrays.asList(var1));
         this.sig = Collections.unmodifiableList(Arrays.asList(var2));
      }
   }

   boolean isShard() {
      return this.isShard;
   }

   long getIndexLimit() {
      return this.indexLimit;
   }

   @Override
   public long getUsagesRemaining() {
      return this.getIndexLimit() - this.getIndex();
   }

   LMSPrivateKeyParameters getRootKey() {
      return this.keys.get(0);
   }

   public HSSPrivateKeyParameters extractKeyShard(int var1) {
      synchronized (this) {
         if (var1 < 0) {
            throw new IllegalArgumentException("usageCount cannot be negative");
         } else if (var1 > this.indexLimit - this.index) {
            throw new IllegalArgumentException("usageCount exceeds usages remaining in current leaf");
         } else {
            long var3 = this.index;
            long var5 = this.index + var1;
            this.index = var5;
            ArrayList var7 = new ArrayList<>(this.getKeys());
            ArrayList var8 = new ArrayList<>(this.getSig());
            HSSPrivateKeyParameters var9 = makeCopy(new HSSPrivateKeyParameters(this.l, var7, var8, var3, var5, true));
            this.resetKeyToIndex();
            return var9;
         }
      }
   }

   synchronized List<LMSPrivateKeyParameters> getKeys() {
      return this.keys;
   }

   synchronized List<LMSSignature> getSig() {
      return this.sig;
   }

   void resetKeyToIndex() {
      List var1 = this.getKeys();
      long[] var2 = new long[var1.size()];
      long var3 = this.getIndex();

      for (int var5 = var1.size() - 1; var5 >= 0; var5--) {
         LMSigParameters var6 = ((LMSPrivateKeyParameters)var1.get(var5)).getSigParameters();
         int var7 = (1 << var6.getH()) - 1;
         var2[var5] = var3 & var7;
         var3 >>>= var6.getH();
      }

      boolean var18 = false;
      LMSPrivateKeyParameters[] var19 = var1.toArray(new LMSPrivateKeyParameters[var1.size()]);
      LMSSignature[] var20 = this.sig.toArray(new LMSSignature[this.sig.size()]);
      LMSPrivateKeyParameters var8 = this.getRootKey();
      if (var19[0].getIndex() - 1 != var2[0]) {
         var19[0] = LMS.generateKeys(var8.getSigParameters(), var8.getOtsParameters(), (int)var2[0], var8.getI(), var8.getMasterSecret());
         var18 = true;
      }

      for (int var9 = 1; var9 < var2.length; var9++) {
         LMSPrivateKeyParameters var10 = var19[var9 - 1];
         int var11 = var10.getOtsParameters().getN();
         byte[] var12 = new byte[16];
         byte[] var13 = new byte[var11];
         SeedDerive var14 = new SeedDerive(var10.getI(), var10.getMasterSecret(), DigestUtil.getDigest(var10.getOtsParameters()));
         var14.setQ((int)var2[var9 - 1]);
         var14.setJ(-2);
         var14.deriveSeed(var13, true);
         byte[] var15 = new byte[var11];
         var14.deriveSeed(var15, false);
         System.arraycopy(var15, 0, var12, 0, var12.length);
         boolean var16 = var9 < var2.length - 1 ? var2[var9] == var19[var9].getIndex() - 1 : var2[var9] == var19[var9].getIndex();
         boolean var17 = org.bouncycastle.util.Arrays.areEqual(var12, var19[var9].getI())
            && org.bouncycastle.util.Arrays.areEqual(var13, var19[var9].getMasterSecret());
         if (!var17) {
            var19[var9] = LMS.generateKeys(
               ((LMSPrivateKeyParameters)var1.get(var9)).getSigParameters(),
               ((LMSPrivateKeyParameters)var1.get(var9)).getOtsParameters(),
               (int)var2[var9],
               var12,
               var13
            );
            var20[var9 - 1] = LMS.generateSign(var19[var9 - 1], var19[var9].getPublicKey().toByteArray());
            var18 = true;
         } else if (!var16) {
            var19[var9] = LMS.generateKeys(
               ((LMSPrivateKeyParameters)var1.get(var9)).getSigParameters(),
               ((LMSPrivateKeyParameters)var1.get(var9)).getOtsParameters(),
               (int)var2[var9],
               var12,
               var13
            );
            var18 = true;
         }
      }

      if (var18) {
         this.updateHierarchy(var19, var20);
      }
   }

   public synchronized HSSPublicKeyParameters getPublicKey() {
      return new HSSPublicKeyParameters(this.l, this.getRootKey().getPublicKey());
   }

   void replaceConsumedKey(int var1) {
      LMOtsPrivateKey var2 = this.keys.get(var1 - 1).getCurrentOTSKey();
      int var3 = var2.getParameter().getN();
      SeedDerive var4 = var2.getDerivationFunction();
      var4.setJ(-2);
      byte[] var5 = new byte[var3];
      var4.deriveSeed(var5, true);
      byte[] var6 = new byte[var3];
      var4.deriveSeed(var6, false);
      byte[] var7 = new byte[16];
      System.arraycopy(var6, 0, var7, 0, var7.length);
      ArrayList var8 = new ArrayList<>(this.keys);
      LMSPrivateKeyParameters var9 = this.keys.get(var1);
      var8.set(var1, LMS.generateKeys(var9.getSigParameters(), var9.getOtsParameters(), 0, var7, var5));
      ArrayList var10 = new ArrayList<>(this.sig);
      var10.set(var1 - 1, LMS.generateSign((LMSPrivateKeyParameters)var8.get(var1 - 1), ((LMSPrivateKeyParameters)var8.get(var1)).getPublicKey().toByteArray()));
      this.keys = Collections.unmodifiableList(var8);
      this.sig = Collections.unmodifiableList(var10);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         HSSPrivateKeyParameters var2 = (HSSPrivateKeyParameters)var1;
         if (this.l != var2.l) {
            return false;
         } else if (this.isShard != var2.isShard) {
            return false;
         } else if (this.indexLimit != var2.indexLimit) {
            return false;
         } else if (this.index != var2.index) {
            return false;
         } else {
            return !this.keys.equals(var2.keys) ? false : this.sig.equals(var2.sig);
         }
      } else {
         return false;
      }
   }

   @Override
   public synchronized byte[] getEncoded() throws IOException {
      Composer var1 = Composer.compose().u32str(0).u32str(this.l).u64str(this.index).u64str(this.indexLimit).bool(this.isShard);

      for (LMSPrivateKeyParameters var3 : this.keys) {
         var1.bytes(var3);
      }

      for (LMSSignature var5 : this.sig) {
         var1.bytes(var5);
      }

      return var1.build();
   }

   @Override
   public int hashCode() {
      int var1 = this.l;
      var1 = 31 * var1 + (this.isShard ? 1 : 0);
      var1 = 31 * var1 + this.keys.hashCode();
      var1 = 31 * var1 + this.sig.hashCode();
      var1 = 31 * var1 + (int)(this.indexLimit ^ this.indexLimit >>> 32);
      return 31 * var1 + (int)(this.index ^ this.index >>> 32);
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      return makeCopy(this);
   }

   @Override
   public LMSContext generateLMSContext() {
      int var3 = this.getL();
      LMSSignedPubKey[] var1;
      LMSPrivateKeyParameters var2;
      synchronized (this) {
         HSS.rangeTestKeys(this);
         List var5 = this.getKeys();
         List var6 = this.getSig();
         var2 = this.getKeys().get(var3 - 1);
         int var7 = 0;

         for (var1 = new LMSSignedPubKey[var3 - 1]; var7 < var3 - 1; var7++) {
            var1[var7] = new LMSSignedPubKey((LMSSignature)var6.get(var7), ((LMSPrivateKeyParameters)var5.get(var7 + 1)).getPublicKey());
         }

         this.incIndex();
      }

      return var2.generateLMSContext().withSignedPublicKeys(var1);
   }

   @Override
   public byte[] generateSignature(LMSContext var1) {
      try {
         return HSS.generateSignature(this.getL(), var1).getEncoded();
      } catch (IOException var3) {
         throw new IllegalStateException("unable to encode signature: " + var3.getMessage(), var3);
      }
   }
}
