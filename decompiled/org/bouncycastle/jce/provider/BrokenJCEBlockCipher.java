package org.bouncycastle.jce.provider;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.CTSBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.params.RC5Parameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.util.Strings;

public class BrokenJCEBlockCipher implements BrokenPBE {
   private Class[] availableSpecs = new Class[]{IvParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class};
   private BufferedBlockCipher cipher;
   private ParametersWithIV ivParam;
   private int pbeType = 2;
   private int pbeHash = 1;
   private int pbeKeySize;
   private int pbeIvSize;
   private int ivLength = 0;
   private AlgorithmParameters engineParams = null;

   protected BrokenJCEBlockCipher(BlockCipher var1) {
      this.cipher = new PaddedBufferedBlockCipher(var1);
   }

   protected BrokenJCEBlockCipher(BlockCipher var1, int var2, int var3, int var4, int var5) {
      this.cipher = new PaddedBufferedBlockCipher(var1);
      this.pbeType = var2;
      this.pbeHash = var3;
      this.pbeKeySize = var4;
      this.pbeIvSize = var5;
   }

   protected int engineGetBlockSize() {
      return this.cipher.getBlockSize();
   }

   protected byte[] engineGetIV() {
      return this.ivParam != null ? this.ivParam.getIV() : null;
   }

   protected int engineGetKeySize(Key var1) {
      return var1.getEncoded().length;
   }

   protected int engineGetOutputSize(int var1) {
      return this.cipher.getOutputSize(var1);
   }

   protected AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null && this.ivParam != null) {
         String var1 = this.cipher.getUnderlyingCipher().getAlgorithmName();
         if (var1.indexOf(47) >= 0) {
            var1 = var1.substring(0, var1.indexOf(47));
         }

         try {
            this.engineParams = AlgorithmParameters.getInstance(var1, "BC");
            this.engineParams.init(this.ivParam.getIV());
         } catch (Exception var3) {
            throw new RuntimeException(var3.toString());
         }
      }

      return this.engineParams;
   }

   protected void engineSetMode(String var1) {
      String var2 = Strings.toUpperCase(var1);
      if (var2.equals("ECB")) {
         this.ivLength = 0;
         this.cipher = new PaddedBufferedBlockCipher(this.cipher.getUnderlyingCipher());
      } else if (var2.equals("CBC")) {
         this.ivLength = this.cipher.getUnderlyingCipher().getBlockSize();
         this.cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(this.cipher.getUnderlyingCipher()));
      } else if (var2.startsWith("OFB")) {
         this.ivLength = this.cipher.getUnderlyingCipher().getBlockSize();
         if (var2.length() != 3) {
            int var3 = Integer.parseInt(var2.substring(3));
            this.cipher = new PaddedBufferedBlockCipher(new OFBBlockCipher(this.cipher.getUnderlyingCipher(), var3));
         } else {
            this.cipher = new PaddedBufferedBlockCipher(new OFBBlockCipher(this.cipher.getUnderlyingCipher(), 8 * this.cipher.getBlockSize()));
         }
      } else {
         if (!var2.startsWith("CFB")) {
            throw new IllegalArgumentException("can't support mode " + var1);
         }

         this.ivLength = this.cipher.getUnderlyingCipher().getBlockSize();
         if (var2.length() != 3) {
            int var4 = Integer.parseInt(var2.substring(3));
            this.cipher = new PaddedBufferedBlockCipher(new CFBBlockCipher(this.cipher.getUnderlyingCipher(), var4));
         } else {
            this.cipher = new PaddedBufferedBlockCipher(new CFBBlockCipher(this.cipher.getUnderlyingCipher(), 8 * this.cipher.getBlockSize()));
         }
      }
   }

   protected void engineSetPadding(String var1) throws NoSuchPaddingException {
      String var2 = Strings.toUpperCase(var1);
      if (var2.equals("NOPADDING")) {
         this.cipher = new BufferedBlockCipher(this.cipher.getUnderlyingCipher());
      } else if (!var2.equals("PKCS5PADDING") && !var2.equals("PKCS7PADDING") && !var2.equals("ISO10126PADDING")) {
         if (!var2.equals("WITHCTS")) {
            throw new NoSuchPaddingException("Padding " + var1 + " unknown.");
         }

         this.cipher = new CTSBlockCipher(this.cipher.getUnderlyingCipher());
      } else {
         this.cipher = new PaddedBufferedBlockCipher(this.cipher.getUnderlyingCipher());
      }
   }

   protected void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      Object var5;
      if (var2 instanceof BCPBEKey) {
         var5 = BrokenPBE.Util.makePBEParameters(
            (BCPBEKey)var2, var3, this.pbeType, this.pbeHash, this.cipher.getUnderlyingCipher().getAlgorithmName(), this.pbeKeySize, this.pbeIvSize
         );
         if (this.pbeIvSize != 0) {
            this.ivParam = (ParametersWithIV)var5;
         }
      } else if (var3 == null) {
         var5 = new KeyParameter(var2.getEncoded());
      } else if (var3 instanceof IvParameterSpec) {
         if (this.ivLength != 0) {
            var5 = new ParametersWithIV(new KeyParameter(var2.getEncoded()), ((IvParameterSpec)var3).getIV());
            this.ivParam = (ParametersWithIV)var5;
         } else {
            var5 = new KeyParameter(var2.getEncoded());
         }
      } else if (var3 instanceof RC2ParameterSpec) {
         RC2ParameterSpec var6 = (RC2ParameterSpec)var3;
         var5 = new RC2Parameters(var2.getEncoded(), ((RC2ParameterSpec)var3).getEffectiveKeyBits());
         if (var6.getIV() != null && this.ivLength != 0) {
            var5 = new ParametersWithIV((CipherParameters)var5, var6.getIV());
            this.ivParam = (ParametersWithIV)var5;
         }
      } else {
         if (!(var3 instanceof RC5ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter type.");
         }

         RC5ParameterSpec var7 = (RC5ParameterSpec)var3;
         var5 = new RC5Parameters(var2.getEncoded(), ((RC5ParameterSpec)var3).getRounds());
         if (var7.getWordSize() != 32) {
            throw new IllegalArgumentException("can only accept RC5 word size 32 (at the moment...)");
         }

         if (var7.getIV() != null && this.ivLength != 0) {
            var5 = new ParametersWithIV((CipherParameters)var5, var7.getIV());
            this.ivParam = (ParametersWithIV)var5;
         }
      }

      if (this.ivLength != 0 && !(var5 instanceof ParametersWithIV)) {
         if (var4 == null) {
            var4 = CryptoServicesRegistrar.getSecureRandom();
         }

         if (var1 != 1 && var1 != 3) {
            throw new InvalidAlgorithmParameterException("no IV set when one expected");
         }

         byte[] var8 = new byte[this.ivLength];
         var4.nextBytes(var8);
         var5 = new ParametersWithIV((CipherParameters)var5, var8);
         this.ivParam = (ParametersWithIV)var5;
      }

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
            throw new IllegalArgumentException("unknown opmode: " + var1);
      }
   }

   protected void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      AlgorithmParameterSpec var5 = null;
      if (var3 != null) {
         for (int var6 = 0; var6 != this.availableSpecs.length; var6++) {
            try {
               var5 = var3.getParameterSpec(this.availableSpecs[var6]);
               break;
            } catch (Exception var8) {
            }
         }

         if (var5 == null) {
            throw new InvalidAlgorithmParameterException("can't handle parameter " + var3.toString());
         }
      }

      this.engineParams = var3;
      this.engineInit(var1, var2, var5, var4);
   }

   protected void engineInit(int var1, Key var2, SecureRandom var3) throws InvalidKeyException {
      try {
         this.engineInit(var1, var2, (AlgorithmParameterSpec)null, var3);
      } catch (InvalidAlgorithmParameterException var5) {
         throw new IllegalArgumentException(var5.getMessage());
      }
   }

   protected byte[] engineUpdate(byte[] var1, int var2, int var3) {
      int var4 = this.cipher.getUpdateOutputSize(var3);
      if (var4 > 0) {
         byte[] var5 = new byte[var4];
         this.cipher.processBytes(var1, var2, var3, var5, 0);
         return var5;
      } else {
         this.cipher.processBytes(var1, var2, var3, null, 0);
         return null;
      }
   }

   protected int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      return this.cipher.processBytes(var1, var2, var3, var4, var5);
   }

   protected byte[] engineDoFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException {
      int var4 = 0;
      byte[] var5 = new byte[this.engineGetOutputSize(var3)];
      if (var3 != 0) {
         var4 = this.cipher.processBytes(var1, var2, var3, var5, 0);
      }

      try {
         var4 += this.cipher.doFinal(var5, var4);
      } catch (DataLengthException var7) {
         throw new IllegalBlockSizeException(var7.getMessage());
      } catch (InvalidCipherTextException var8) {
         throw new BadPaddingException(var8.getMessage());
      }

      byte[] var6 = new byte[var4];
      System.arraycopy(var5, 0, var6, 0, var4);
      return var6;
   }

   protected int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalBlockSizeException, BadPaddingException {
      int var6 = 0;
      if (var3 != 0) {
         var6 = this.cipher.processBytes(var1, var2, var3, var4, var5);
      }

      try {
         return var6 + this.cipher.doFinal(var4, var5 + var6);
      } catch (DataLengthException var8) {
         throw new IllegalBlockSizeException(var8.getMessage());
      } catch (InvalidCipherTextException var9) {
         throw new BadPaddingException(var9.getMessage());
      }
   }

   protected byte[] engineWrap(Key var1) throws IllegalBlockSizeException, InvalidKeyException {
      byte[] var2 = var1.getEncoded();
      if (var2 == null) {
         throw new InvalidKeyException("Cannot wrap key, null encoding.");
      } else {
         try {
            return this.engineDoFinal(var2, 0, var2.length);
         } catch (BadPaddingException var4) {
            throw new IllegalBlockSizeException(var4.getMessage());
         }
      }
   }

   protected Key engineUnwrap(byte[] var1, String var2, int var3) throws InvalidKeyException {
      Object var4 = null;

      try {
         var4 = this.engineDoFinal(var1, 0, var1.length);
      } catch (BadPaddingException var6) {
         throw new InvalidKeyException(var6.getMessage());
      } catch (IllegalBlockSizeException var7) {
         throw new InvalidKeyException(var7.getMessage());
      }

      if (var3 == 3) {
         return new SecretKeySpec((byte[])var4, var2);
      } else {
         try {
            KeyFactory var5 = KeyFactory.getInstance(var2, "BC");
            if (var3 == 1) {
               return var5.generatePublic(new X509EncodedKeySpec((byte[])var4));
            }

            if (var3 == 2) {
               return var5.generatePrivate(new PKCS8EncodedKeySpec((byte[])var4));
            }
         } catch (NoSuchProviderException var8) {
            throw new InvalidKeyException("Unknown key type " + var8.getMessage());
         } catch (NoSuchAlgorithmException var9) {
            throw new InvalidKeyException("Unknown key type " + var9.getMessage());
         } catch (InvalidKeySpecException var10) {
            throw new InvalidKeyException("Unknown key type " + var10.getMessage());
         }

         throw new InvalidKeyException("Unknown key type " + var3);
      }
   }

   public static class BrokePBEWithMD5AndDES extends BrokenJCEBlockCipher {
      public BrokePBEWithMD5AndDES() {
         super(new CBCBlockCipher(new DESEngine()), 0, 0, 64, 64);
      }
   }

   public static class BrokePBEWithSHA1AndDES extends BrokenJCEBlockCipher {
      public BrokePBEWithSHA1AndDES() {
         super(new CBCBlockCipher(new DESEngine()), 0, 1, 64, 64);
      }
   }

   public static class BrokePBEWithSHAAndDES2Key extends BrokenJCEBlockCipher {
      public BrokePBEWithSHAAndDES2Key() {
         super(new CBCBlockCipher(new DESedeEngine()), 2, 1, 128, 64);
      }
   }

   public static class BrokePBEWithSHAAndDES3Key extends BrokenJCEBlockCipher {
      public BrokePBEWithSHAAndDES3Key() {
         super(new CBCBlockCipher(new DESedeEngine()), 2, 1, 192, 64);
      }
   }

   public static class OldPBEWithSHAAndDES3Key extends BrokenJCEBlockCipher {
      public OldPBEWithSHAAndDES3Key() {
         super(new CBCBlockCipher(new DESedeEngine()), 3, 1, 192, 64);
      }
   }

   public static class OldPBEWithSHAAndTwofish extends BrokenJCEBlockCipher {
      public OldPBEWithSHAAndTwofish() {
         super(new CBCBlockCipher(new TwofishEngine()), 3, 1, 256, 128);
      }
   }
}
