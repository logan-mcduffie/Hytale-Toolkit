package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.jcajce.spec.SkeinParameterSpec;

public class BaseMac extends MacSpi implements PBE {
   private Mac macEngine;
   private int scheme = 2;
   private int pbeHash = 1;
   private int keySize = 160;

   protected BaseMac(Mac var1) {
      this.macEngine = var1;
   }

   protected BaseMac(int var1, Mac var2) {
      this.keySize = var1;
      this.macEngine = var2;
   }

   protected BaseMac(Mac var1, int var2, int var3, int var4) {
      this.macEngine = var1;
      this.scheme = var2;
      this.pbeHash = var3;
      this.keySize = var4;
   }

   @Override
   protected void engineInit(Key var1, AlgorithmParameterSpec var2) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (var1 == null) {
         throw new InvalidKeyException("key is null");
      } else {
         Object var3;
         if (var1 instanceof PKCS12Key) {
            SecretKey var4;
            try {
               var4 = (SecretKey)var1;
            } catch (Exception var10) {
               throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }

            PBEParameterSpec var5;
            try {
               var5 = (PBEParameterSpec)var2;
            } catch (Exception var9) {
               throw new InvalidAlgorithmParameterException("PKCS12 requires a PBEParameterSpec");
            }

            if (var4 instanceof PBEKey && var5 == null) {
               var5 = new PBEParameterSpec(((PBEKey)var4).getSalt(), ((PBEKey)var4).getIterationCount());
            }

            byte var6 = 1;
            short var7 = 160;
            if (this.macEngine.getAlgorithmName().startsWith("GOST")) {
               var6 = 6;
               var7 = 256;
            } else if (this.macEngine instanceof HMac && !this.macEngine.getAlgorithmName().startsWith("SHA-1")) {
               if (this.macEngine.getAlgorithmName().startsWith("SHA-224")) {
                  var6 = 7;
                  var7 = 224;
               } else if (this.macEngine.getAlgorithmName().startsWith("SHA-256")) {
                  var6 = 4;
                  var7 = 256;
               } else if (this.macEngine.getAlgorithmName().startsWith("SHA-384")) {
                  var6 = 8;
                  var7 = 384;
               } else if (this.macEngine.getAlgorithmName().startsWith("SHA-512")) {
                  var6 = 9;
                  var7 = 512;
               } else {
                  if (!this.macEngine.getAlgorithmName().startsWith("RIPEMD160")) {
                     throw new InvalidAlgorithmParameterException("no PKCS12 mapping for HMAC: " + this.macEngine.getAlgorithmName());
                  }

                  var6 = 2;
                  var7 = 160;
               }
            }

            var3 = PBE.Util.makePBEMacParameters(var4, 2, var6, var7, var5);
         } else if (var1 instanceof BCPBEKey) {
            BCPBEKey var11 = (BCPBEKey)var1;
            if (var11.getParam() != null) {
               var3 = var11.getParam();
            } else {
               if (!(var2 instanceof PBEParameterSpec)) {
                  throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
               }

               var3 = PBE.Util.makePBEMacParameters(var11, var2);
            }
         } else {
            if (var2 instanceof PBEParameterSpec) {
               throw new InvalidAlgorithmParameterException("inappropriate parameter type: " + var2.getClass().getName());
            }

            var3 = new KeyParameter(var1.getEncoded());
         }

         KeyParameter var12;
         if (var3 instanceof ParametersWithIV) {
            var12 = (KeyParameter)((ParametersWithIV)var3).getParameters();
         } else {
            var12 = (KeyParameter)var3;
         }

         if (var2 instanceof AEADParameterSpec) {
            AEADParameterSpec var13 = (AEADParameterSpec)var2;
            var3 = new AEADParameters(var12, var13.getMacSizeInBits(), var13.getNonce(), var13.getAssociatedData());
         } else if (var2 instanceof IvParameterSpec) {
            var3 = new ParametersWithIV(var12, ((IvParameterSpec)var2).getIV());
         } else if (var2 instanceof RC2ParameterSpec) {
            var3 = new ParametersWithIV(new RC2Parameters(var12.getKey(), ((RC2ParameterSpec)var2).getEffectiveKeyBits()), ((RC2ParameterSpec)var2).getIV());
         } else if (var2 instanceof SkeinParameterSpec) {
            var3 = new SkeinParameters.Builder(copyMap(((SkeinParameterSpec)var2).getParameters())).setKey(var12.getKey()).build();
         } else if (var2 == null) {
            var3 = new KeyParameter(var1.getEncoded());
         } else if (GcmSpecUtil.isGcmSpec(var2)) {
            var3 = GcmSpecUtil.extractAeadParameters(var12, var2);
         } else if (!(var2 instanceof PBEParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter type: " + var2.getClass().getName());
         }

         try {
            this.macEngine.init((CipherParameters)var3);
         } catch (Exception var8) {
            throw new InvalidAlgorithmParameterException("cannot initialize MAC: " + var8.getMessage());
         }
      }
   }

   @Override
   protected int engineGetMacLength() {
      return this.macEngine.getMacSize();
   }

   @Override
   protected void engineReset() {
      this.macEngine.reset();
   }

   @Override
   protected void engineUpdate(byte var1) {
      this.macEngine.update(var1);
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) {
      this.macEngine.update(var1, var2, var3);
   }

   @Override
   protected byte[] engineDoFinal() {
      byte[] var1 = new byte[this.engineGetMacLength()];
      this.macEngine.doFinal(var1, 0);
      return var1;
   }

   private static Hashtable copyMap(Map var0) {
      Hashtable var1 = new Hashtable();

      for (Object var3 : var0.keySet()) {
         var1.put(var3, var0.get(var3));
      }

      return var1;
   }
}
