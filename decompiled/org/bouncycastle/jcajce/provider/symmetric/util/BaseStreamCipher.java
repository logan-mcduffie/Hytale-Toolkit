package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;

public class BaseStreamCipher extends BaseWrapCipher implements PBE {
   private Class[] availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
   private StreamCipher cipher;
   private int keySizeInBits;
   private int digest;
   private ParametersWithIV ivParam;
   private int ivLength = 0;
   private PBEParameterSpec pbeSpec = null;
   private String pbeAlgorithm = null;

   protected BaseStreamCipher(StreamCipher var1, int var2) {
      this(var1, var2, -1, -1);
   }

   protected BaseStreamCipher(StreamCipher var1, int var2, int var3) {
      this(var1, var2, var3, -1);
   }

   protected BaseStreamCipher(StreamCipher var1, int var2, int var3, int var4) {
      this.cipher = var1;
      this.ivLength = var2;
      this.keySizeInBits = var3;
      this.digest = var4;
   }

   @Override
   protected int engineGetBlockSize() {
      return 0;
   }

   @Override
   protected byte[] engineGetIV() {
      return this.ivParam != null ? this.ivParam.getIV() : null;
   }

   @Override
   protected int engineGetKeySize(Key var1) {
      return var1.getEncoded().length * 8;
   }

   @Override
   protected int engineGetOutputSize(int var1) {
      return var1;
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null) {
         if (this.pbeSpec != null) {
            try {
               AlgorithmParameters var5 = this.createParametersInstance(this.pbeAlgorithm);
               var5.init(this.pbeSpec);
               return var5;
            } catch (Exception var3) {
               return null;
            }
         }

         if (this.ivParam != null) {
            String var1 = this.cipher.getAlgorithmName();
            if (var1.indexOf(47) >= 0) {
               var1 = var1.substring(0, var1.indexOf(47));
            }

            if (var1.startsWith("ChaCha7539")) {
               var1 = "ChaCha7539";
            } else if (var1.startsWith("Grain")) {
               var1 = "Grainv1";
            } else if (var1.startsWith("HC")) {
               int var2 = var1.indexOf(45);
               var1 = var1.substring(0, var2) + var1.substring(var2 + 1);
            }

            try {
               this.engineParams = this.createParametersInstance(var1);
               this.engineParams.init(new IvParameterSpec(this.ivParam.getIV()));
            } catch (Exception var4) {
               throw new RuntimeException(var4.toString());
            }
         }
      }

      return this.engineParams;
   }

   @Override
   protected void engineSetMode(String var1) throws NoSuchAlgorithmException {
      if (!var1.equalsIgnoreCase("ECB") && !var1.equals("NONE")) {
         throw new NoSuchAlgorithmException("can't support mode " + var1);
      }
   }

   @Override
   protected void engineSetPadding(String var1) throws NoSuchPaddingException {
      if (!var1.equalsIgnoreCase("NoPadding")) {
         throw new NoSuchPaddingException("Padding " + var1 + " unknown.");
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      this.pbeSpec = null;
      this.pbeAlgorithm = null;
      this.engineParams = null;
      if (!(var2 instanceof SecretKey)) {
         throw new InvalidKeyException("Key for algorithm " + var2.getAlgorithm() + " not suitable for symmetric enryption.");
      } else {
         Object var5;
         if (var2 instanceof PKCS12Key) {
            PKCS12Key var6 = (PKCS12Key)var2;
            this.pbeSpec = (PBEParameterSpec)var3;
            if (var6 instanceof PKCS12KeyWithParameters && this.pbeSpec == null) {
               this.pbeSpec = new PBEParameterSpec(((PKCS12KeyWithParameters)var6).getSalt(), ((PKCS12KeyWithParameters)var6).getIterationCount());
            }

            var5 = PBE.Util.makePBEParameters(
               var6.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName()
            );
         } else if (var2 instanceof BCPBEKey) {
            BCPBEKey var9 = (BCPBEKey)var2;
            if (var9.getOID() != null) {
               this.pbeAlgorithm = var9.getOID().getId();
            } else {
               this.pbeAlgorithm = var9.getAlgorithm();
            }

            if (var9.getParam() != null) {
               var5 = var9.getParam();
               this.pbeSpec = new PBEParameterSpec(var9.getSalt(), var9.getIterationCount());
            } else {
               if (!(var3 instanceof PBEParameterSpec)) {
                  throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
               }

               var5 = PBE.Util.makePBEParameters(var9, var3, this.cipher.getAlgorithmName());
               this.pbeSpec = (PBEParameterSpec)var3;
            }

            if (var9.getIvSize() != 0) {
               this.ivParam = (ParametersWithIV)var5;
            }
         } else if (var3 == null) {
            if (this.digest > 0) {
               throw new InvalidKeyException("Algorithm requires a PBE key");
            }

            var5 = new KeyParameter(var2.getEncoded());
         } else {
            if (!(var3 instanceof IvParameterSpec)) {
               throw new InvalidAlgorithmParameterException("unknown parameter type.");
            }

            var5 = new ParametersWithIV(new KeyParameter(var2.getEncoded()), ((IvParameterSpec)var3).getIV());
            this.ivParam = (ParametersWithIV)var5;
         }

         if (this.ivLength != 0 && !(var5 instanceof ParametersWithIV)) {
            SecureRandom var10 = var4;
            if (var4 == null) {
               var10 = CryptoServicesRegistrar.getSecureRandom();
            }

            if (var1 != 1 && var1 != 3) {
               throw new InvalidAlgorithmParameterException("no IV set when one expected");
            }

            byte[] var7 = new byte[this.ivLength];
            var10.nextBytes(var7);
            var5 = new ParametersWithIV((CipherParameters)var5, var7);
            this.ivParam = (ParametersWithIV)var5;
         }

         try {
            switch (var1) {
               case 1:
               case 3:
                  this.cipher.init(true, (CipherParameters)var5);
                  break;
               case 2:
               case 4:
                  this.cipher.init(false, (CipherParameters)var5);
                  break;
               default:
                  throw new InvalidParameterException("unknown opmode " + var1 + " passed");
            }
         } catch (Exception var8) {
            throw new InvalidKeyException(var8.getMessage());
         }
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      AlgorithmParameterSpec var5 = null;
      if (var3 != null) {
         var5 = SpecUtil.extractSpec(var3, this.availableSpecs);
         if (var5 == null) {
            throw new InvalidAlgorithmParameterException("can't handle parameter " + var3.toString());
         }
      }

      this.engineInit(var1, var2, var5, var4);
      this.engineParams = var3;
   }

   @Override
   protected void engineInit(int var1, Key var2, SecureRandom var3) throws InvalidKeyException {
      try {
         this.engineInit(var1, var2, (AlgorithmParameterSpec)null, var3);
      } catch (InvalidAlgorithmParameterException var5) {
         throw new InvalidKeyException(var5.getMessage());
      }
   }

   @Override
   protected byte[] engineUpdate(byte[] var1, int var2, int var3) {
      byte[] var4 = new byte[var3];
      this.cipher.processBytes(var1, var2, var3, var4, 0);
      return var4;
   }

   @Override
   protected int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException {
      if (var5 + var3 > var4.length) {
         throw new ShortBufferException("output buffer too short for input.");
      } else {
         try {
            this.cipher.processBytes(var1, var2, var3, var4, var5);
            return var3;
         } catch (DataLengthException var7) {
            throw new IllegalStateException(var7.getMessage());
         }
      }
   }

   @Override
   protected byte[] engineDoFinal(byte[] var1, int var2, int var3) {
      if (var3 != 0) {
         byte[] var4 = this.engineUpdate(var1, var2, var3);
         this.cipher.reset();
         return var4;
      } else {
         this.cipher.reset();
         return new byte[0];
      }
   }

   @Override
   protected int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException {
      if (var5 + var3 > var4.length) {
         throw new ShortBufferException("output buffer too short for input.");
      } else {
         if (var3 != 0) {
            this.cipher.processBytes(var1, var2, var3, var4, var5);
         }

         this.cipher.reset();
         return var3;
      }
   }
}
