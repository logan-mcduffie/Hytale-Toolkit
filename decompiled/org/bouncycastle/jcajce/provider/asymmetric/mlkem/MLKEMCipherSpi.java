package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.jcajce.provider.util.WrapUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

class MLKEMCipherSpi extends CipherSpi {
   private final String algorithmName;
   private MLKEMGenerator kemGen;
   private KTSParameterSpec kemParameterSpec;
   private BCMLKEMPublicKey wrapKey;
   private BCMLKEMPrivateKey unwrapKey;
   private AlgorithmParameters engineParams;
   private MLKEMParameters mlkemParamters;

   MLKEMCipherSpi(String var1) {
      this.algorithmName = var1;
      this.mlkemParamters = null;
   }

   MLKEMCipherSpi(MLKEMParameters var1) {
      this.mlkemParamters = var1;
      this.algorithmName = var1.getName();
   }

   @Override
   protected void engineSetMode(String var1) throws NoSuchAlgorithmException {
      throw new NoSuchAlgorithmException("Cannot support mode " + var1);
   }

   @Override
   protected void engineSetPadding(String var1) throws NoSuchPaddingException {
      throw new NoSuchPaddingException("Padding " + var1 + " unknown");
   }

   @Override
   protected int engineGetKeySize(Key var1) {
      return 2048;
   }

   @Override
   protected int engineGetBlockSize() {
      return 0;
   }

   @Override
   protected int engineGetOutputSize(int var1) {
      return -1;
   }

   @Override
   protected byte[] engineGetIV() {
      return null;
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null) {
         try {
            this.engineParams = AlgorithmParameters.getInstance(this.algorithmName, "BCPQC");
            this.engineParams.init(this.kemParameterSpec);
         } catch (Exception var2) {
            throw Exceptions.illegalStateException(var2.toString(), var2);
         }
      }

      return this.engineParams;
   }

   @Override
   protected void engineInit(int var1, Key var2, SecureRandom var3) throws InvalidKeyException {
      try {
         this.engineInit(var1, var2, (AlgorithmParameterSpec)null, var3);
      } catch (InvalidAlgorithmParameterException var5) {
         throw Exceptions.illegalArgumentException(var5.getMessage(), var5);
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (var3 == null) {
         this.kemParameterSpec = new KTSParameterSpec.Builder("AES-KWP", 256).build();
      } else {
         if (!(var3 instanceof KTSParameterSpec)) {
            throw new InvalidAlgorithmParameterException(this.algorithmName + " can only accept KTSParameterSpec");
         }

         this.kemParameterSpec = (KTSParameterSpec)var3;
      }

      if (var1 == 3) {
         if (!(var2 instanceof BCMLKEMPublicKey)) {
            throw new InvalidKeyException("Only a " + this.algorithmName + " public key can be used for wrapping");
         }

         this.wrapKey = (BCMLKEMPublicKey)var2;
         this.kemGen = new MLKEMGenerator(CryptoServicesRegistrar.getSecureRandom(var4));
      } else {
         if (var1 != 4) {
            throw new InvalidParameterException("Cipher only valid for wrapping/unwrapping");
         }

         if (!(var2 instanceof BCMLKEMPrivateKey)) {
            throw new InvalidKeyException("Only a " + this.algorithmName + " private key can be used for unwrapping");
         }

         this.unwrapKey = (BCMLKEMPrivateKey)var2;
      }

      if (this.mlkemParamters != null) {
         String var5 = MLKEMParameterSpec.fromName(this.mlkemParamters.getName()).getName();
         if (!var5.equals(var2.getAlgorithm())) {
            throw new InvalidKeyException("cipher locked to " + var5);
         }
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      AlgorithmParameterSpec var5 = null;
      if (var3 != null) {
         try {
            var5 = var3.getParameterSpec(KTSParameterSpec.class);
         } catch (Exception var7) {
            throw new InvalidAlgorithmParameterException("can't handle parameter " + var3.toString());
         }
      }

      this.engineInit(var1, var2, var5, var4);
   }

   @Override
   protected byte[] engineUpdate(byte[] var1, int var2, int var3) {
      throw new IllegalStateException("Not supported in a wrapping mode");
   }

   @Override
   protected int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException {
      throw new IllegalStateException("Not supported in a wrapping mode");
   }

   @Override
   protected byte[] engineDoFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException {
      throw new IllegalStateException("Not supported in a wrapping mode");
   }

   @Override
   protected int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
      throw new IllegalStateException("Not supported in a wrapping mode");
   }

   @Override
   protected byte[] engineWrap(Key var1) throws IllegalBlockSizeException, InvalidKeyException {
      byte[] var2 = var1.getEncoded();
      if (var2 == null) {
         throw new InvalidKeyException("Cannot wrap key, null encoding.");
      } else {
         SecretWithEncapsulation var3 = null;

         byte[] var8;
         try {
            var3 = this.kemGen.generateEncapsulated(this.wrapKey.getKeyParams());
            Wrapper var4 = WrapUtil.getKeyWrapper(this.kemParameterSpec, var3.getSecret());
            byte[] var5 = var3.getEncapsulation();
            byte[] var6 = var1.getEncoded();
            byte[] var7 = Arrays.concatenate(var5, var4.wrap(var6, 0, var6.length));
            Arrays.clear(var6);
            var8 = var7;
         } catch (IllegalArgumentException var16) {
            throw new IllegalBlockSizeException("unable to generate KTS secret: " + var16.getMessage());
         } finally {
            try {
               if (var3 != null) {
                  var3.destroy();
               }
            } catch (DestroyFailedException var17) {
               throw new IllegalBlockSizeException("unable to destroy interim values: " + var17.getMessage());
            }
         }

         return var8;
      }
   }

   @Override
   protected Key engineUnwrap(byte[] var1, String var2, int var3) throws InvalidKeyException, NoSuchAlgorithmException {
      if (var3 != 3) {
         throw new InvalidKeyException("only SECRET_KEY supported");
      } else {
         byte[] var4 = null;

         SecretKeySpec var9;
         try {
            MLKEMExtractor var5 = new MLKEMExtractor(this.unwrapKey.getKeyParams());
            var4 = var5.extractSecret(Arrays.copyOfRange(var1, 0, var5.getEncapsulationLength()));
            Wrapper var6 = WrapUtil.getKeyUnwrapper(this.kemParameterSpec, var4);
            byte[] var7 = Arrays.copyOfRange(var1, var5.getEncapsulationLength(), var1.length);
            SecretKeySpec var8 = new SecretKeySpec(var6.unwrap(var7, 0, var7.length), var2);
            var9 = var8;
         } catch (IllegalArgumentException var14) {
            throw new NoSuchAlgorithmException("unable to extract KTS secret: " + var14.getMessage());
         } catch (InvalidCipherTextException var15) {
            throw new InvalidKeyException("unable to extract KTS secret: " + var15.getMessage());
         } finally {
            if (var4 != null) {
               Arrays.clear(var4);
            }
         }

         return var9;
      }
   }

   public static class Base extends MLKEMCipherSpi {
      public Base() throws NoSuchAlgorithmException {
         super("MLKEM");
      }
   }

   public static class MLKEM1024 extends MLKEMCipherSpi {
      public MLKEM1024() {
         super(MLKEMParameters.ml_kem_1024);
      }
   }

   public static class MLKEM512 extends MLKEMCipherSpi {
      public MLKEM512() {
         super(MLKEMParameters.ml_kem_512);
      }
   }

   public static class MLKEM768 extends MLKEMCipherSpi {
      public MLKEM768() {
         super(MLKEMParameters.ml_kem_768);
      }
   }
}
