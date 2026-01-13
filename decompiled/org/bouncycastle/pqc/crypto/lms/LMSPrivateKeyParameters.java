package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.ExhaustedPrivateKeyException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public class LMSPrivateKeyParameters extends LMSKeyParameters implements LMSContextBasedSigner {
   private static LMSPrivateKeyParameters.CacheKey T1 = new LMSPrivateKeyParameters.CacheKey(1);
   private static LMSPrivateKeyParameters.CacheKey[] internedKeys = new LMSPrivateKeyParameters.CacheKey[129];
   private final byte[] I;
   private final LMSigParameters parameters;
   private final LMOtsParameters otsParameters;
   private final int maxQ;
   private final byte[] masterSecret;
   private final Map<LMSPrivateKeyParameters.CacheKey, byte[]> tCache;
   private final int maxCacheR;
   private final Digest tDigest;
   private int q;
   private LMSPublicKeyParameters publicKey;

   public LMSPrivateKeyParameters(LMSigParameters var1, LMOtsParameters var2, int var3, byte[] var4, int var5, byte[] var6) {
      super(true);
      this.parameters = var1;
      this.otsParameters = var2;
      this.q = var3;
      this.I = Arrays.clone(var4);
      this.maxQ = var5;
      this.masterSecret = Arrays.clone(var6);
      this.maxCacheR = 1 << this.parameters.getH() + 1;
      this.tCache = new WeakHashMap<>();
      this.tDigest = DigestUtil.getDigest(var1);
   }

   private LMSPrivateKeyParameters(LMSPrivateKeyParameters var1, int var2, int var3) {
      super(true);
      this.parameters = var1.parameters;
      this.otsParameters = var1.otsParameters;
      this.q = var2;
      this.I = var1.I;
      this.maxQ = var3;
      this.masterSecret = var1.masterSecret;
      this.maxCacheR = 1 << this.parameters.getH();
      this.tCache = var1.tCache;
      this.tDigest = DigestUtil.getDigest(this.parameters);
      this.publicKey = var1.publicKey;
   }

   public static LMSPrivateKeyParameters getInstance(byte[] var0, byte[] var1) throws IOException {
      LMSPrivateKeyParameters var2 = getInstance(var0);
      var2.publicKey = LMSPublicKeyParameters.getInstance(var1);
      return var2;
   }

   public static LMSPrivateKeyParameters getInstance(Object var0) throws IOException {
      if (var0 instanceof LMSPrivateKeyParameters) {
         return (LMSPrivateKeyParameters)var0;
      } else if (var0 instanceof DataInputStream) {
         DataInputStream var12 = (DataInputStream)var0;
         if (var12.readInt() != 0) {
            throw new IllegalStateException("expected version 0 lms private key");
         } else {
            LMSigParameters var13 = LMSigParameters.getParametersForType(var12.readInt());
            LMOtsParameters var3 = LMOtsParameters.getParametersForType(var12.readInt());
            byte[] var4 = new byte[16];
            var12.readFully(var4);
            int var5 = var12.readInt();
            int var6 = var12.readInt();
            int var7 = var12.readInt();
            if (var7 < 0) {
               throw new IllegalStateException("secret length less than zero");
            } else if (var7 > var12.available()) {
               throw new IOException("secret length exceeded " + var12.available());
            } else {
               byte[] var8 = new byte[var7];
               var12.readFully(var8);
               return new LMSPrivateKeyParameters(var13, var3, var5, var4, var6, var8);
            }
         }
      } else if (var0 instanceof byte[]) {
         DataInputStream var1 = null;

         LMSPrivateKeyParameters var2;
         try {
            var1 = new DataInputStream(new ByteArrayInputStream((byte[])var0));
            var2 = getInstance(var1);
         } finally {
            if (var1 != null) {
               var1.close();
            }
         }

         return var2;
      } else if (var0 instanceof InputStream) {
         return getInstance(Streams.readAll((InputStream)var0));
      } else {
         throw new IllegalArgumentException("cannot parse " + var0);
      }
   }

   LMOtsPrivateKey getCurrentOTSKey() {
      synchronized (this) {
         if (this.q >= this.maxQ) {
            throw new ExhaustedPrivateKeyException("ots private keys expired");
         } else {
            return new LMOtsPrivateKey(this.otsParameters, this.I, this.q, this.masterSecret);
         }
      }
   }

   public synchronized int getIndex() {
      return this.q;
   }

   synchronized void incIndex() {
      this.q++;
   }

   @Override
   public LMSContext generateLMSContext() {
      LMSigParameters var1 = this.getSigParameters();
      int var2 = var1.getH();
      int var3 = this.getIndex();
      LMOtsPrivateKey var4 = this.getNextOtsPrivateKey();
      int var5 = 0;
      int var6 = (1 << var2) + var3;

      byte[][] var7;
      for (var7 = new byte[var2][]; var5 < var2; var5++) {
         int var8 = var6 / (1 << var5) ^ 1;
         var7[var5] = this.findT(var8);
      }

      return var4.getSignatureContext(this.getSigParameters(), var7);
   }

   @Override
   public byte[] generateSignature(LMSContext var1) {
      try {
         return LMS.generateSign(var1).getEncoded();
      } catch (IOException var3) {
         throw new IllegalStateException("unable to encode signature: " + var3.getMessage(), var3);
      }
   }

   LMOtsPrivateKey getNextOtsPrivateKey() {
      synchronized (this) {
         if (this.q >= this.maxQ) {
            throw new ExhaustedPrivateKeyException("ots private key exhausted");
         } else {
            LMOtsPrivateKey var2 = new LMOtsPrivateKey(this.otsParameters, this.I, this.q, this.masterSecret);
            this.incIndex();
            return var2;
         }
      }
   }

   public LMSPrivateKeyParameters extractKeyShard(int var1) {
      synchronized (this) {
         if (var1 < 0) {
            throw new IllegalArgumentException("usageCount cannot be negative");
         } else if (var1 > this.maxQ - this.q) {
            throw new IllegalArgumentException("usageCount exceeds usages remaining");
         } else {
            int var3 = this.q;
            int var4 = this.q + var1;
            this.q = var4;
            return new LMSPrivateKeyParameters(this, var3, var4);
         }
      }
   }

   public LMSigParameters getSigParameters() {
      return this.parameters;
   }

   public LMOtsParameters getOtsParameters() {
      return this.otsParameters;
   }

   public byte[] getI() {
      return Arrays.clone(this.I);
   }

   public byte[] getMasterSecret() {
      return Arrays.clone(this.masterSecret);
   }

   public int getIndexLimit() {
      return this.maxQ;
   }

   @Override
   public long getUsagesRemaining() {
      return this.getIndexLimit() - this.getIndex();
   }

   public LMSPublicKeyParameters getPublicKey() {
      synchronized (this) {
         if (this.publicKey == null) {
            this.publicKey = new LMSPublicKeyParameters(this.parameters, this.otsParameters, this.findT(T1), this.I);
         }

         return this.publicKey;
      }
   }

   byte[] findT(int var1) {
      return var1 < this.maxCacheR
         ? this.findT(var1 < internedKeys.length ? internedKeys[var1] : new LMSPrivateKeyParameters.CacheKey(var1))
         : this.calcT(var1);
   }

   private byte[] findT(LMSPrivateKeyParameters.CacheKey var1) {
      synchronized (this.tCache) {
         byte[] var3 = this.tCache.get(var1);
         if (var3 != null) {
            return var3;
         } else {
            var3 = this.calcT(var1.index);
            this.tCache.put(var1, var3);
            return var3;
         }
      }
   }

   private byte[] calcT(int var1) {
      int var2 = this.getSigParameters().getH();
      int var3 = 1 << var2;
      if (var1 >= var3) {
         LmsUtils.byteArray(this.getI(), this.tDigest);
         LmsUtils.u32str(var1, this.tDigest);
         LmsUtils.u16str((short)-32126, this.tDigest);
         byte[] var8 = LM_OTS.lms_ots_generatePublicKey(this.getOtsParameters(), this.getI(), var1 - var3, this.getMasterSecret());
         LmsUtils.byteArray(var8, this.tDigest);
         byte[] var7 = new byte[this.tDigest.getDigestSize()];
         this.tDigest.doFinal(var7, 0);
         return var7;
      } else {
         byte[] var5 = this.findT(2 * var1);
         byte[] var6 = this.findT(2 * var1 + 1);
         LmsUtils.byteArray(this.getI(), this.tDigest);
         LmsUtils.u32str(var1, this.tDigest);
         LmsUtils.u16str((short)-31869, this.tDigest);
         LmsUtils.byteArray(var5, this.tDigest);
         LmsUtils.byteArray(var6, this.tDigest);
         byte[] var4 = new byte[this.tDigest.getDigestSize()];
         this.tDigest.doFinal(var4, 0);
         return var4;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         LMSPrivateKeyParameters var2 = (LMSPrivateKeyParameters)var1;
         if (this.q != var2.q) {
            return false;
         } else if (this.maxQ != var2.maxQ) {
            return false;
         } else if (!Arrays.areEqual(this.I, var2.I)) {
            return false;
         } else if (this.parameters != null ? this.parameters.equals(var2.parameters) : var2.parameters == null) {
            return (this.otsParameters != null ? this.otsParameters.equals(var2.otsParameters) : var2.otsParameters == null)
               ? Arrays.areEqual(this.masterSecret, var2.masterSecret)
               : false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.q;
      var1 = 31 * var1 + Arrays.hashCode(this.I);
      var1 = 31 * var1 + (this.parameters != null ? this.parameters.hashCode() : 0);
      var1 = 31 * var1 + (this.otsParameters != null ? this.otsParameters.hashCode() : 0);
      var1 = 31 * var1 + this.maxQ;
      return 31 * var1 + Arrays.hashCode(this.masterSecret);
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return Composer.compose()
         .u32str(0)
         .u32str(this.parameters.getType())
         .u32str(this.otsParameters.getType())
         .bytes(this.I)
         .u32str(this.q)
         .u32str(this.maxQ)
         .u32str(this.masterSecret.length)
         .bytes(this.masterSecret)
         .build();
   }

   static {
      internedKeys[1] = T1;

      for (int var0 = 2; var0 < internedKeys.length; var0++) {
         internedKeys[var0] = new LMSPrivateKeyParameters.CacheKey(var0);
      }
   }

   private static class CacheKey {
      private final int index;

      CacheKey(int var1) {
         this.index = var1;
      }

      @Override
      public int hashCode() {
         return this.index;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof LMSPrivateKeyParameters.CacheKey ? ((LMSPrivateKeyParameters.CacheKey)var1).index == this.index : false;
      }
   }
}
