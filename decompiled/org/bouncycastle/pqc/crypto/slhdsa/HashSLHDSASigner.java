package org.bouncycastle.pqc.crypto.slhdsa;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.DigestUtils;
import org.bouncycastle.util.Arrays;

public class HashSLHDSASigner implements Signer {
   private byte[] msgPrefix;
   private SLHDSAPublicKeyParameters pubKey;
   private SLHDSAPrivateKeyParameters privKey;
   private SecureRandom random;
   private Digest digest;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      ParametersWithContext var3 = null;
      if (var2 instanceof ParametersWithContext) {
         var3 = (ParametersWithContext)var2;
         var2 = ((ParametersWithContext)var2).getParameters();
         if (var3.getContextLength() > 255) {
            throw new IllegalArgumentException("context too long");
         }
      }

      SLHDSAParameters var4;
      if (var1) {
         this.pubKey = null;
         if (var2 instanceof ParametersWithRandom) {
            this.privKey = (SLHDSAPrivateKeyParameters)((ParametersWithRandom)var2).getParameters();
            this.random = ((ParametersWithRandom)var2).getRandom();
         } else {
            this.privKey = (SLHDSAPrivateKeyParameters)var2;
            this.random = null;
         }

         var4 = this.privKey.getParameters();
      } else {
         this.pubKey = (SLHDSAPublicKeyParameters)var2;
         this.privKey = null;
         this.random = null;
         var4 = this.pubKey.getParameters();
      }

      this.initDigest(var4, var3);
   }

   private void initDigest(SLHDSAParameters var1, ParametersWithContext var2) {
      this.digest = createDigest(var1);
      ASN1ObjectIdentifier var3 = DigestUtils.getDigestOid(this.digest.getAlgorithmName());

      byte[] var4;
      try {
         var4 = var3.getEncoded("DER");
      } catch (IOException var6) {
         throw new IllegalStateException("oid encoding failed: " + var6.getMessage());
      }

      int var5 = var2 == null ? 0 : var2.getContextLength();
      this.msgPrefix = new byte[2 + var5 + var4.length];
      this.msgPrefix[0] = 1;
      this.msgPrefix[1] = (byte)var5;
      if (var2 != null) {
         var2.copyContextTo(this.msgPrefix, 2, var5);
      }

      System.arraycopy(var4, 0, this.msgPrefix, 2 + var5, var4.length);
   }

   @Override
   public void update(byte var1) {
      this.digest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.digest.update(var1, var2, var3);
   }

   @Override
   public byte[] generateSignature() throws CryptoException, DataLengthException {
      SLHDSAEngine var1 = this.privKey.getParameters().getEngine();
      var1.init(this.privKey.pk.seed);
      byte[] var2 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var2, 0);
      byte[] var3 = new byte[var1.N];
      if (this.random != null) {
         this.random.nextBytes(var3);
      } else {
         System.arraycopy(this.privKey.pk.seed, 0, var3, 0, var3.length);
      }

      return internalGenerateSignature(this.privKey, this.msgPrefix, var2, var3);
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      byte[] var2 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var2, 0);
      return internalVerifySignature(this.pubKey, this.msgPrefix, var2, var1);
   }

   @Override
   public void reset() {
      this.digest.reset();
   }

   protected byte[] internalGenerateSignature(byte[] var1, byte[] var2) {
      return internalGenerateSignature(this.privKey, null, var1, var2);
   }

   private static byte[] internalGenerateSignature(SLHDSAPrivateKeyParameters var0, byte[] var1, byte[] var2, byte[] var3) {
      SLHDSAEngine var4 = var0.getParameters().getEngine();
      var4.init(var0.pk.seed);
      Fors var5 = new Fors(var4);
      byte[] var6 = var4.PRF_msg(var0.sk.prf, var3, var1, var2);
      IndexedDigest var7 = var4.H_msg(var6, var0.pk.seed, var0.pk.root, var1, var2);
      byte[] var8 = var7.digest;
      long var9 = var7.idx_tree;
      int var11 = var7.idx_leaf;
      ADRS var12 = new ADRS();
      var12.setTypeAndClear(3);
      var12.setTreeAddress(var9);
      var12.setKeyPairAddress(var11);
      SIG_FORS[] var13 = var5.sign(var8, var0.sk.seed, var0.pk.seed, var12);
      var12 = new ADRS();
      var12.setTypeAndClear(3);
      var12.setTreeAddress(var9);
      var12.setKeyPairAddress(var11);
      byte[] var14 = var5.pkFromSig(var13, var8, var0.pk.seed, var12);
      ADRS var15 = new ADRS();
      var15.setTypeAndClear(2);
      HT var16 = new HT(var4, var0.getSeed(), var0.getPublicSeed());
      byte[] var17 = var16.sign(var14, var9, var11);
      byte[][] var18 = new byte[var13.length + 2][];
      var18[0] = var6;

      for (int var19 = 0; var19 != var13.length; var19++) {
         var18[1 + var19] = Arrays.concatenate(var13[var19].sk, Arrays.concatenate(var13[var19].authPath));
      }

      var18[var18.length - 1] = var17;
      return Arrays.concatenate(var18);
   }

   protected boolean internalVerifySignature(byte[] var1, byte[] var2) {
      return internalVerifySignature(this.pubKey, null, var1, var2);
   }

   private static boolean internalVerifySignature(SLHDSAPublicKeyParameters var0, byte[] var1, byte[] var2, byte[] var3) {
      SLHDSAEngine var4 = var0.getParameters().getEngine();
      var4.init(var0.getSeed());
      ADRS var5 = new ADRS();
      if ((1 + var4.K * (1 + var4.A) + var4.H + var4.D * var4.WOTS_LEN) * var4.N != var3.length) {
         return false;
      } else {
         SIG var6 = new SIG(var4.N, var4.K, var4.A, var4.D, var4.H_PRIME, var4.WOTS_LEN, var3);
         byte[] var7 = var6.getR();
         SIG_FORS[] var8 = var6.getSIG_FORS();
         SIG_XMSS[] var9 = var6.getSIG_HT();
         IndexedDigest var10 = var4.H_msg(var7, var0.getSeed(), var0.getRoot(), var1, var2);
         byte[] var11 = var10.digest;
         long var12 = var10.idx_tree;
         int var14 = var10.idx_leaf;
         var5.setTypeAndClear(3);
         var5.setLayerAddress(0);
         var5.setTreeAddress(var12);
         var5.setKeyPairAddress(var14);
         byte[] var15 = new Fors(var4).pkFromSig(var8, var11, var0.getSeed(), var5);
         var5.setTypeAndClear(2);
         var5.setLayerAddress(0);
         var5.setTreeAddress(var12);
         var5.setKeyPairAddress(var14);
         HT var16 = new HT(var4, null, var0.getSeed());
         return var16.verify(var15, var9, var0.getSeed(), var12, var14, var0.getRoot());
      }
   }

   private static Digest createDigest(SLHDSAParameters var0) {
      switch (var0.getType()) {
         case 0:
            String var1 = var0.getName();
            if (var1.startsWith("sha2")) {
               if (SLHDSAParameters.sha2_128f != var0 && SLHDSAParameters.sha2_128s != var0) {
                  return new SHA512Digest();
               }

               return SHA256Digest.newInstance();
            } else {
               if (SLHDSAParameters.shake_128f != var0 && SLHDSAParameters.shake_128s != var0) {
                  return new SHAKEDigest(256);
               }

               return new SHAKEDigest(128);
            }
         case 1:
            return SHA256Digest.newInstance();
         case 2:
            return new SHA512Digest();
         case 3:
            return new SHAKEDigest(128);
         case 4:
            return new SHAKEDigest(256);
         default:
            throw new IllegalArgumentException("unknown parameters type");
      }
   }
}
