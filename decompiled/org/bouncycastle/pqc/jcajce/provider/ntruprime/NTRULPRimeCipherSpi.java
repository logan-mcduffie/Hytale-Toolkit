package org.bouncycastle.pqc.jcajce.provider.ntruprime;

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
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.KEMParameterSpec;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKEMExtractor;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKEMGenerator;
import org.bouncycastle.pqc.jcajce.provider.util.WrapUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

class NTRULPRimeCipherSpi extends CipherSpi {
   private final String algorithmName;
   private NTRULPRimeKEMGenerator kemGen;
   private KTSParameterSpec kemParameterSpec;
   private BCNTRULPRimePublicKey wrapKey;
   private BCNTRULPRimePrivateKey unwrapKey;
   private AlgorithmParameters engineParams;

   NTRULPRimeCipherSpi(String var1) throws NoSuchAlgorithmException {
      this.algorithmName = var1;
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
         if (!(var2 instanceof BCNTRULPRimePublicKey)) {
            throw new InvalidKeyException("Only a " + this.algorithmName + " public key can be used for wrapping");
         }

         this.wrapKey = (BCNTRULPRimePublicKey)var2;
         this.kemGen = new NTRULPRimeKEMGenerator(CryptoServicesRegistrar.getSecureRandom(var4));
      } else {
         if (var1 != 4) {
            throw new InvalidParameterException("Cipher only valid for wrapping/unwrapping");
         }

         if (!(var2 instanceof BCNTRULPRimePrivateKey)) {
            throw new InvalidKeyException("Only a " + this.algorithmName + " private key can be used for unwrapping");
         }

         this.unwrapKey = (BCNTRULPRimePrivateKey)var2;
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      AlgorithmParameterSpec var5 = null;
      if (var3 != null) {
         try {
            var5 = var3.getParameterSpec(KEMParameterSpec.class);
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
         try {
            SecretWithEncapsulation var3 = this.kemGen.generateEncapsulated(this.wrapKey.getKeyParams());
            Wrapper var4 = WrapUtil.getWrapper(this.kemParameterSpec.getKeyAlgorithmName());
            KeyParameter var5 = new KeyParameter(var3.getSecret(), 0, (this.kemParameterSpec.getKeySize() + 7) / 8);
            var4.init(true, var5);
            byte[] var6 = var3.getEncapsulation();
            var3.destroy();
            byte[] var7 = var1.getEncoded();
            byte[] var8 = Arrays.concatenate(var6, var4.wrap(var7, 0, var7.length));
            Arrays.clear(var7);
            return var8;
         } catch (IllegalArgumentException var9) {
            throw new IllegalBlockSizeException("unable to generate KTS secret: " + var9.getMessage());
         } catch (DestroyFailedException var10) {
            throw new IllegalBlockSizeException("unable to destroy interim values: " + var10.getMessage());
         }
      }
   }

   @Override
   protected Key engineUnwrap(byte[] var1, String var2, int var3) throws InvalidKeyException, NoSuchAlgorithmException {
      if (var3 != 3) {
         throw new InvalidKeyException("only SECRET_KEY supported");
      } else {
         try {
            NTRULPRimeKEMExtractor var4 = new NTRULPRimeKEMExtractor(this.unwrapKey.getKeyParams());
            byte[] var5 = var4.extractSecret(Arrays.copyOfRange(var1, 0, var4.getEncapsulationLength()));
            Wrapper var6 = WrapUtil.getWrapper(this.kemParameterSpec.getKeyAlgorithmName());
            KeyParameter var7 = new KeyParameter(var5, 0, (this.kemParameterSpec.getKeySize() + 7) / 8);
            Arrays.clear(var5);
            var6.init(false, var7);
            byte[] var8 = Arrays.copyOfRange(var1, var4.getEncapsulationLength(), var1.length);
            SecretKeySpec var9 = new SecretKeySpec(var6.unwrap(var8, 0, var8.length), var2);
            Arrays.clear(var7.getKey());
            return var9;
         } catch (IllegalArgumentException var10) {
            throw new NoSuchAlgorithmException("unable to extract KTS secret: " + var10.getMessage());
         } catch (InvalidCipherTextException var11) {
            throw new InvalidKeyException("unable to extract KTS secret: " + var11.getMessage());
         }
      }
   }

   public static class Base extends NTRULPRimeCipherSpi {
      public Base() throws NoSuchAlgorithmException {
         super("NTRULPRime");
      }
   }
}
