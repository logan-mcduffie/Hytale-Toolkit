package org.bouncycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Constructor;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.fpe.FPEEngine;
import org.bouncycastle.crypto.fpe.FPEFF1Engine;
import org.bouncycastle.crypto.fpe.FPEFF3_1Engine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.CTSBlockCipher;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.GCMSIVBlockCipher;
import org.bouncycastle.crypto.modes.GOFBBlockCipher;
import org.bouncycastle.crypto.modes.KCCMBlockCipher;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.modes.OCBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.OpenPGPCFBBlockCipher;
import org.bouncycastle.crypto.modes.PGPCFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO10126d2Padding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.TBCPadding;
import org.bouncycastle.crypto.paddings.X923Padding;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.params.RC5Parameters;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.jcajce.PBKDF1KeyWithParameters;
import org.bouncycastle.jcajce.PBKDF2Key;
import org.bouncycastle.jcajce.PBKDF2KeyWithParameters;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.jcajce.spec.FPEParameterSpec;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.spec.RepeatedSecretKeySpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BaseBlockCipher extends BaseWrapCipher implements PBE {
   private static final int BUF_SIZE = 512;
   private static final Class[] availableSpecs = new Class[]{
      RC2ParameterSpec.class, RC5ParameterSpec.class, GcmSpecUtil.gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class
   };
   private BlockCipher baseEngine;
   private BlockCipherProvider engineProvider;
   private BaseBlockCipher.GenericBlockCipher cipher;
   private ParametersWithIV ivParam;
   private AEADParameters aeadParams;
   private int keySizeInBits;
   private int scheme = -1;
   private int digest;
   private int ivLength = 0;
   private boolean padded;
   private boolean fixedIv = true;
   private PBEParameterSpec pbeSpec = null;
   private String pbeAlgorithm = null;
   private String modeName = null;

   protected BaseBlockCipher(BlockCipher var1) {
      this.baseEngine = var1;
      this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(var1);
   }

   protected BaseBlockCipher(BlockCipher var1, int var2, int var3, int var4, int var5) {
      this.baseEngine = var1;
      this.scheme = var2;
      this.digest = var3;
      this.keySizeInBits = var4;
      this.ivLength = var5;
      this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(var1);
   }

   protected BaseBlockCipher(BlockCipherProvider var1) {
      this.baseEngine = var1.get();
      this.engineProvider = var1;
      this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(var1.get());
   }

   protected BaseBlockCipher(int var1, BlockCipherProvider var2) {
      this.baseEngine = var2.get();
      this.engineProvider = var2;
      this.keySizeInBits = var1;
      this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(var2.get());
   }

   protected BaseBlockCipher(AEADBlockCipher var1) {
      this(0, var1);
   }

   protected BaseBlockCipher(int var1, AEADBlockCipher var2) {
      this.keySizeInBits = var1;
      this.baseEngine = var2.getUnderlyingCipher();
      if (var2.getAlgorithmName().indexOf("GCM") >= 0) {
         this.ivLength = 12;
      } else {
         this.ivLength = this.baseEngine.getBlockSize();
      }

      this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(var2);
   }

   protected BaseBlockCipher(AEADCipher var1, boolean var2, int var3) {
      this.baseEngine = null;
      this.fixedIv = var2;
      this.ivLength = var3;
      this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(var1);
   }

   protected BaseBlockCipher(AEADBlockCipher var1, boolean var2, int var3) {
      this(0, var1, var2, var3);
   }

   protected BaseBlockCipher(int var1, AEADBlockCipher var2, boolean var3, int var4) {
      this.keySizeInBits = var1;
      this.baseEngine = var2.getUnderlyingCipher();
      this.fixedIv = var3;
      this.ivLength = var4;
      this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(var2);
   }

   protected BaseBlockCipher(BlockCipher var1, int var2) {
      this(var1, true, var2);
   }

   protected BaseBlockCipher(int var1, BlockCipher var2, int var3) {
      this.keySizeInBits = var1;
      this.baseEngine = var2;
      this.fixedIv = true;
      this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(var2);
      this.ivLength = var3 / 8;
   }

   protected BaseBlockCipher(BlockCipher var1, boolean var2, int var3) {
      this.baseEngine = var1;
      this.fixedIv = var2;
      this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(var1);
      this.ivLength = var3 / 8;
   }

   protected BaseBlockCipher(BufferedBlockCipher var1, int var2) {
      this(var1, true, var2);
   }

   protected BaseBlockCipher(int var1, BufferedBlockCipher var2, int var3) {
      this.keySizeInBits = var1;
      this.baseEngine = var2.getUnderlyingCipher();
      this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(var2);
      this.fixedIv = true;
      this.ivLength = var3 / 8;
   }

   protected BaseBlockCipher(BufferedBlockCipher var1, boolean var2, int var3) {
      this.baseEngine = var1.getUnderlyingCipher();
      this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(var1);
      this.fixedIv = var2;
      this.ivLength = var3 / 8;
   }

   @Override
   protected int engineGetBlockSize() {
      return this.baseEngine == null ? -1 : this.baseEngine.getBlockSize();
   }

   @Override
   protected byte[] engineGetIV() {
      if (this.aeadParams != null) {
         return this.aeadParams.getNonce();
      } else {
         return this.ivParam != null ? this.ivParam.getIV() : null;
      }
   }

   @Override
   protected int engineGetKeySize(Key var1) {
      return var1.getEncoded().length * 8;
   }

   @Override
   protected int engineGetOutputSize(int var1) {
      return this.cipher.getOutputSize(var1);
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      if (this.engineParams != null) {
         return this.engineParams;
      } else if (this.pbeSpec != null) {
         try {
            this.engineParams = this.createParametersInstance(this.pbeAlgorithm);
            this.engineParams.init(this.pbeSpec);
            return this.engineParams;
         } catch (Exception var6) {
            var6.printStackTrace();
            return null;
         }
      } else if (this.aeadParams != null) {
         if (this.baseEngine == null) {
            try {
               this.engineParams = this.createParametersInstance(PKCSObjectIdentifiers.id_alg_AEADChaCha20Poly1305.getId());
               this.engineParams.init(new DEROctetString(this.aeadParams.getNonce()).getEncoded());
               return this.engineParams;
            } catch (Exception var5) {
               throw new RuntimeException(var5.toString());
            }
         } else {
            try {
               this.engineParams = this.createParametersInstance("GCM");
               this.engineParams.init(new GCMParameters(this.aeadParams.getNonce(), this.aeadParams.getMacSize() / 8).getEncoded());
               return this.engineParams;
            } catch (Exception var4) {
               throw new RuntimeException(var4.toString());
            }
         }
      } else {
         if (this.ivParam != null) {
            String var1 = this.cipher.getUnderlyingCipher().getAlgorithmName();
            if (var1.indexOf(47) >= 0) {
               var1 = var1.substring(0, var1.indexOf(47));
            }

            try {
               this.engineParams = this.createParametersInstance(var1);
               this.engineParams.init(new IvParameterSpec(this.ivParam.getIV()));
            } catch (Exception var3) {
               throw new RuntimeException(var3.toString());
            }
         }

         return this.engineParams;
      }
   }

   @Override
   protected void engineSetMode(String var1) throws NoSuchAlgorithmException {
      if (this.baseEngine == null) {
         throw new NoSuchAlgorithmException("no mode supported for this algorithm");
      } else {
         this.modeName = Strings.toUpperCase(var1);
         if (this.modeName.equals("ECB")) {
            this.ivLength = 0;
            this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(this.baseEngine);
         } else if (this.modeName.equals("CBC")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(CBCBlockCipher.newInstance(this.baseEngine));
         } else if (this.modeName.startsWith("OFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
               int var2 = Integer.parseInt(this.modeName.substring(3));
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new OFBBlockCipher(this.baseEngine, var2));
            } else {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new OFBBlockCipher(this.baseEngine, 8 * this.baseEngine.getBlockSize()));
            }
         } else if (this.modeName.startsWith("CFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
               int var3 = Integer.parseInt(this.modeName.substring(3));
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(CFBBlockCipher.newInstance(this.baseEngine, var3));
            } else {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(CFBBlockCipher.newInstance(this.baseEngine, 8 * this.baseEngine.getBlockSize()));
            }
         } else if (this.modeName.startsWith("PGPCFB")) {
            boolean var4 = this.modeName.equals("PGPCFBWITHIV");
            if (!var4 && this.modeName.length() != 6) {
               throw new NoSuchAlgorithmException("no mode support for " + this.modeName);
            }

            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new PGPCFBBlockCipher(this.baseEngine, var4));
         } else if (this.modeName.equals("OPENPGPCFB")) {
            this.ivLength = 0;
            this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new OpenPGPCFBBlockCipher(this.baseEngine));
         } else if (this.modeName.equals("FF1")) {
            this.ivLength = 0;
            this.cipher = new BaseBlockCipher.BufferedFPEBlockCipher(new FPEFF1Engine(this.baseEngine));
         } else if (this.modeName.equals("FF3-1")) {
            this.ivLength = 0;
            this.cipher = new BaseBlockCipher.BufferedFPEBlockCipher(new FPEFF3_1Engine(this.baseEngine));
         } else if (this.modeName.equals("SIC")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.ivLength < 16) {
               throw new IllegalArgumentException(
                  "Warning: SIC-Mode can become a twotime-pad if the blocksize of the cipher is too small. Use a cipher with a block size of at least 128 bits (e.g. AES)"
               );
            }

            this.fixedIv = false;
            this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(SICBlockCipher.newInstance(this.baseEngine)));
         } else if (this.modeName.equals("CTR")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.fixedIv = false;
            if (this.baseEngine instanceof DSTU7624Engine) {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(new KCTRBlockCipher(this.baseEngine)));
            } else {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(SICBlockCipher.newInstance(this.baseEngine)));
            }
         } else if (this.modeName.equals("GOFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(new GOFBBlockCipher(this.baseEngine)));
         } else if (this.modeName.equals("GCFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(new GCFBBlockCipher(this.baseEngine)));
         } else if (this.modeName.equals("CTS")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new CTSBlockCipher(CBCBlockCipher.newInstance(this.baseEngine)));
         } else if (this.modeName.equals("CCM")) {
            this.ivLength = 12;
            if (this.baseEngine instanceof DSTU7624Engine) {
               this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(new KCCMBlockCipher(this.baseEngine));
            } else {
               this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(CCMBlockCipher.newInstance(this.baseEngine));
            }
         } else if (this.modeName.equals("OCB")) {
            if (this.engineProvider == null) {
               throw new NoSuchAlgorithmException("can't support mode " + var1);
            }

            this.ivLength = 15;
            this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(new OCBBlockCipher(this.baseEngine, this.engineProvider.get()));
         } else if (this.modeName.equals("EAX")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(new EAXBlockCipher(this.baseEngine));
         } else if (this.modeName.equals("GCM-SIV")) {
            this.ivLength = 12;
            this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(new GCMSIVBlockCipher(this.baseEngine));
         } else {
            if (!this.modeName.equals("GCM")) {
               throw new NoSuchAlgorithmException("can't support mode " + var1);
            }

            if (this.baseEngine instanceof DSTU7624Engine) {
               this.ivLength = this.baseEngine.getBlockSize();
               this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(new KGCMBlockCipher(this.baseEngine));
            } else {
               this.ivLength = 12;
               this.cipher = new BaseBlockCipher.AEADGenericBlockCipher(GCMBlockCipher.newInstance(this.baseEngine));
            }
         }
      }
   }

   @Override
   protected void engineSetPadding(String var1) throws NoSuchPaddingException {
      if (this.baseEngine == null) {
         throw new NoSuchPaddingException("no padding supported for this algorithm");
      } else {
         String var2 = Strings.toUpperCase(var1);
         if (var2.equals("NOPADDING")) {
            if (this.cipher.wrapOnNoPadding()) {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(this.cipher.getUnderlyingCipher()));
            }
         } else if (!var2.equals("WITHCTS") && !var2.equals("CTSPADDING") && !var2.equals("CS3PADDING")) {
            this.padded = true;
            if (this.isAEADModeName(this.modeName)) {
               throw new NoSuchPaddingException("Only NoPadding can be used with AEAD modes.");
            }

            if (var2.equals("PKCS5PADDING") || var2.equals("PKCS7PADDING")) {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher());
            } else if (var2.equals("ZEROBYTEPADDING")) {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ZeroBytePadding());
            } else if (var2.equals("ISO10126PADDING") || var2.equals("ISO10126-2PADDING")) {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO10126d2Padding());
            } else if (var2.equals("X9.23PADDING") || var2.equals("X923PADDING")) {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new X923Padding());
            } else if (!var2.equals("ISO7816-4PADDING") && !var2.equals("ISO9797-1PADDING")) {
               if (!var2.equals("TBCPADDING")) {
                  throw new NoSuchPaddingException("Padding " + var1 + " unknown.");
               }

               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new TBCPadding());
            } else {
               this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO7816d4Padding());
            }
         } else {
            this.cipher = new BaseBlockCipher.BufferedGenericBlockCipher(new CTSBlockCipher(this.cipher.getUnderlyingCipher()));
         }
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      this.pbeSpec = null;
      this.pbeAlgorithm = null;
      this.engineParams = null;
      this.aeadParams = null;
      if (!(var2 instanceof SecretKey)) {
         throw new InvalidKeyException("Key for algorithm " + (var2 != null ? var2.getAlgorithm() : null) + " not suitable for symmetric enryption.");
      } else if (var3 == null && this.baseEngine != null && this.baseEngine.getAlgorithmName().startsWith("RC5-64")) {
         throw new InvalidAlgorithmParameterException("RC5 requires an RC5ParametersSpec to be passed in.");
      } else {
         Object var5;
         if (this.scheme == 2 || var2 instanceof PKCS12Key) {
            SecretKey var15;
            try {
               var15 = (SecretKey)var2;
            } catch (Exception var11) {
               throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }

            if (var3 instanceof PBEParameterSpec) {
               this.pbeSpec = (PBEParameterSpec)var3;
            }

            if (var15 instanceof PBEKey && this.pbeSpec == null) {
               PBEKey var7 = (PBEKey)var15;
               if (var7.getSalt() == null) {
                  throw new InvalidAlgorithmParameterException("PBEKey requires parameters to specify salt");
               }

               this.pbeSpec = new PBEParameterSpec(var7.getSalt(), var7.getIterationCount());
            }

            if (this.pbeSpec == null && !(var15 instanceof PBEKey)) {
               throw new InvalidKeyException("Algorithm requires a PBE key");
            }

            this.pbeAlgorithm = "PKCS12PBE";
            if (var2 instanceof BCPBEKey) {
               CipherParameters var17 = ((BCPBEKey)var2).getParam();
               if (var17 instanceof ParametersWithIV) {
                  var5 = var17;
               } else {
                  if (var17 != null) {
                     throw new InvalidKeyException("Algorithm requires a PBE key suitable for PKCS12");
                  }

                  var5 = PBE.Util.makePBEParameters(
                     var15.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName()
                  );
               }
            } else {
               var5 = PBE.Util.makePBEParameters(
                  var15.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName()
               );
            }

            if (var5 instanceof ParametersWithIV) {
               this.ivParam = (ParametersWithIV)var5;
            }
         } else if (var2 instanceof PBKDF1Key) {
            PBKDF1Key var6 = (PBKDF1Key)var2;
            if (var3 instanceof PBEParameterSpec) {
               this.pbeSpec = (PBEParameterSpec)var3;
            }

            if (var6 instanceof PBKDF1KeyWithParameters && this.pbeSpec == null) {
               this.pbeSpec = new PBEParameterSpec(((PBKDF1KeyWithParameters)var6).getSalt(), ((PBKDF1KeyWithParameters)var6).getIterationCount());
            }

            var5 = PBE.Util.makePBEParameters(
               var6.getEncoded(), 0, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName()
            );
            if (var5 instanceof ParametersWithIV) {
               this.ivParam = (ParametersWithIV)var5;
            }
         } else if (var2 instanceof PBKDF2Key) {
            PBKDF2Key var12 = (PBKDF2Key)var2;
            if (var3 instanceof PBEParameterSpec) {
               this.pbeSpec = (PBEParameterSpec)var3;
            }

            if (var12 instanceof PBKDF2KeyWithParameters && this.pbeSpec == null) {
               this.pbeSpec = new PBEParameterSpec(((PBKDF2KeyWithParameters)var12).getSalt(), ((PBKDF2KeyWithParameters)var12).getIterationCount());
            }

            var5 = PBE.Util.makePBEParameters(var12.getEncoded(), 1, 9, this.keySizeInBits, 0, this.pbeSpec, this.cipher.getAlgorithmName());
            if (var5 instanceof ParametersWithIV) {
               this.ivParam = (ParametersWithIV)var5;
            }
         } else if (var2 instanceof BCPBEKey) {
            BCPBEKey var13 = (BCPBEKey)var2;
            if (var13.getOID() != null) {
               this.pbeAlgorithm = var13.getOID().getId();
            } else {
               this.pbeAlgorithm = var13.getAlgorithm();
            }

            if (var13.getParam() != null) {
               var5 = this.adjustParameters(var3, var13.getParam());
            } else {
               if (!(var3 instanceof PBEParameterSpec)) {
                  throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
               }

               this.pbeSpec = (PBEParameterSpec)var3;
               var5 = PBE.Util.makePBEParameters(var13, var3, this.cipher.getUnderlyingCipher().getAlgorithmName());
            }

            if (var5 instanceof ParametersWithIV) {
               this.ivParam = (ParametersWithIV)var5;
            }
         } else if (var2 instanceof PBEKey) {
            PBEKey var14 = (PBEKey)var2;
            this.pbeSpec = (PBEParameterSpec)var3;
            if (var14 instanceof PKCS12KeyWithParameters && this.pbeSpec == null) {
               this.pbeSpec = new PBEParameterSpec(var14.getSalt(), var14.getIterationCount());
            }

            var5 = PBE.Util.makePBEParameters(
               var14.getEncoded(), this.scheme, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName()
            );
            if (var5 instanceof ParametersWithIV) {
               this.ivParam = (ParametersWithIV)var5;
            }
         } else if (!(var2 instanceof RepeatedSecretKeySpec)) {
            if (this.scheme == 0 || this.scheme == 4 || this.scheme == 1 || this.scheme == 5) {
               throw new InvalidKeyException("Algorithm requires a PBE key");
            }

            var5 = new KeyParameter(var2.getEncoded());
         } else {
            var5 = null;
         }

         AlgorithmParameterSpec var16 = var3;
         if (var3 instanceof PBEParameterSpec) {
            var16 = ((PBEParameterSpec)var3).getParameterSpec();
            if (var16 instanceof IvParameterSpec && ((IvParameterSpec)var16).getIV().length == 0) {
               var16 = var3;
            }
         }

         if (var16 instanceof AEADParameterSpec) {
            if (!this.isAEADModeName(this.modeName) && !(this.cipher instanceof BaseBlockCipher.AEADGenericBlockCipher)) {
               throw new InvalidAlgorithmParameterException("AEADParameterSpec can only be used with AEAD modes.");
            }

            AEADParameterSpec var18 = (AEADParameterSpec)var16;
            KeyParameter var8;
            if (var5 instanceof ParametersWithIV) {
               var8 = (KeyParameter)((ParametersWithIV)var5).getParameters();
            } else {
               var8 = (KeyParameter)var5;
            }

            var5 = this.aeadParams = new AEADParameters(var8, var18.getMacSizeInBits(), var18.getNonce(), var18.getAssociatedData());
         } else if (var16 instanceof IvParameterSpec) {
            if (this.ivLength != 0) {
               IvParameterSpec var19 = (IvParameterSpec)var16;
               if (var19.getIV().length != this.ivLength && !(this.cipher instanceof BaseBlockCipher.AEADGenericBlockCipher) && this.fixedIv) {
                  throw new InvalidAlgorithmParameterException("IV must be " + this.ivLength + " bytes long.");
               }

               if (var5 instanceof ParametersWithIV) {
                  var5 = new ParametersWithIV(((ParametersWithIV)var5).getParameters(), var19.getIV());
               } else {
                  var5 = new ParametersWithIV((CipherParameters)var5, var19.getIV());
               }

               this.ivParam = (ParametersWithIV)var5;
            } else if (this.modeName != null && this.modeName.equals("ECB")) {
               throw new InvalidAlgorithmParameterException("ECB mode does not use an IV");
            }
         } else if (var16 instanceof GOST28147ParameterSpec) {
            GOST28147ParameterSpec var20 = (GOST28147ParameterSpec)var16;
            var5 = new ParametersWithSBox(new KeyParameter(var2.getEncoded()), ((GOST28147ParameterSpec)var16).getSBox());
            if (var20.getIV() != null && this.ivLength != 0) {
               if (var5 instanceof ParametersWithIV) {
                  var5 = new ParametersWithIV(((ParametersWithIV)var5).getParameters(), var20.getIV());
               } else {
                  var5 = new ParametersWithIV((CipherParameters)var5, var20.getIV());
               }

               this.ivParam = (ParametersWithIV)var5;
            }
         } else if (var16 instanceof RC2ParameterSpec) {
            RC2ParameterSpec var21 = (RC2ParameterSpec)var16;
            var5 = new RC2Parameters(var2.getEncoded(), ((RC2ParameterSpec)var16).getEffectiveKeyBits());
            if (var21.getIV() != null && this.ivLength != 0) {
               if (var5 instanceof ParametersWithIV) {
                  var5 = new ParametersWithIV(((ParametersWithIV)var5).getParameters(), var21.getIV());
               } else {
                  var5 = new ParametersWithIV((CipherParameters)var5, var21.getIV());
               }

               this.ivParam = (ParametersWithIV)var5;
            }
         } else if (var16 instanceof RC5ParameterSpec) {
            RC5ParameterSpec var22 = (RC5ParameterSpec)var16;
            var5 = new RC5Parameters(var2.getEncoded(), ((RC5ParameterSpec)var16).getRounds());
            if (!this.baseEngine.getAlgorithmName().startsWith("RC5")) {
               throw new InvalidAlgorithmParameterException("RC5 parameters passed to a cipher that is not RC5.");
            }

            if (this.baseEngine.getAlgorithmName().equals("RC5-32")) {
               if (var22.getWordSize() != 32) {
                  throw new InvalidAlgorithmParameterException("RC5 already set up for a word size of 32 not " + var22.getWordSize() + ".");
               }
            } else if (this.baseEngine.getAlgorithmName().equals("RC5-64") && var22.getWordSize() != 64) {
               throw new InvalidAlgorithmParameterException("RC5 already set up for a word size of 64 not " + var22.getWordSize() + ".");
            }

            if (var22.getIV() != null && this.ivLength != 0) {
               if (var5 instanceof ParametersWithIV) {
                  var5 = new ParametersWithIV(((ParametersWithIV)var5).getParameters(), var22.getIV());
               } else {
                  var5 = new ParametersWithIV((CipherParameters)var5, var22.getIV());
               }

               this.ivParam = (ParametersWithIV)var5;
            }
         } else if (var16 instanceof FPEParameterSpec) {
            FPEParameterSpec var23 = (FPEParameterSpec)var16;
            var5 = new FPEParameters((KeyParameter)var5, var23.getRadixConverter(), var23.getTweak(), var23.isUsingInverseFunction());
         } else if (GcmSpecUtil.isGcmSpec(var16)) {
            if (!this.isAEADModeName(this.modeName) && !(this.cipher instanceof BaseBlockCipher.AEADGenericBlockCipher)) {
               throw new InvalidAlgorithmParameterException("GCMParameterSpec can only be used with AEAD modes.");
            }

            KeyParameter var24;
            if (var5 instanceof ParametersWithIV) {
               var24 = (KeyParameter)((ParametersWithIV)var5).getParameters();
            } else {
               var24 = (KeyParameter)var5;
            }

            var5 = this.aeadParams = GcmSpecUtil.extractAeadParameters(var24, var16);
         } else if (var16 != null && !(var16 instanceof PBEParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter type.");
         }

         if (this.ivLength != 0 && !(var5 instanceof ParametersWithIV) && !(var5 instanceof AEADParameters)) {
            SecureRandom var25 = var4;
            if (var4 == null) {
               var25 = CryptoServicesRegistrar.getSecureRandom();
            }

            if (var1 != 1 && var1 != 3) {
               if (this.cipher.getUnderlyingCipher().getAlgorithmName().indexOf("PGPCFB") < 0) {
                  throw new InvalidAlgorithmParameterException("no IV set when one expected");
               }
            } else {
               byte[] var27 = new byte[this.ivLength];
               var25.nextBytes(var27);
               var5 = new ParametersWithIV((CipherParameters)var5, var27);
               this.ivParam = (ParametersWithIV)var5;
            }
         }

         if (var4 != null && this.padded) {
            var5 = new ParametersWithRandom((CipherParameters)var5, var4);
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

            if (this.cipher instanceof BaseBlockCipher.AEADGenericBlockCipher && this.aeadParams == null) {
               AEADCipher var26 = ((BaseBlockCipher.AEADGenericBlockCipher)this.cipher).cipher;
               this.aeadParams = new AEADParameters((KeyParameter)this.ivParam.getParameters(), var26.getMac().length * 8, this.ivParam.getIV());
            }
         } catch (IllegalArgumentException var9) {
            throw new InvalidAlgorithmParameterException(var9.getMessage(), var9);
         } catch (Exception var10) {
            throw new BaseWrapCipher.InvalidKeyOrParametersException(var10.getMessage(), var10);
         }
      }
   }

   private CipherParameters adjustParameters(AlgorithmParameterSpec var1, CipherParameters var2) {
      if (var2 instanceof ParametersWithIV) {
         CipherParameters var3 = ((ParametersWithIV)var2).getParameters();
         if (var1 instanceof IvParameterSpec) {
            IvParameterSpec var4 = (IvParameterSpec)var1;
            this.ivParam = new ParametersWithIV(var3, var4.getIV());
            var2 = this.ivParam;
         } else if (var1 instanceof GOST28147ParameterSpec) {
            GOST28147ParameterSpec var5 = (GOST28147ParameterSpec)var1;
            var2 = new ParametersWithSBox((CipherParameters)var2, var5.getSBox());
            if (var5.getIV() != null && this.ivLength != 0) {
               this.ivParam = new ParametersWithIV(var3, var5.getIV());
               var2 = this.ivParam;
            }
         }
      } else if (var1 instanceof IvParameterSpec) {
         IvParameterSpec var6 = (IvParameterSpec)var1;
         this.ivParam = new ParametersWithIV((CipherParameters)var2, var6.getIV());
         var2 = this.ivParam;
      } else if (var1 instanceof GOST28147ParameterSpec) {
         GOST28147ParameterSpec var7 = (GOST28147ParameterSpec)var1;
         var2 = new ParametersWithSBox((CipherParameters)var2, var7.getSBox());
         if (var7.getIV() != null && this.ivLength != 0) {
            var2 = new ParametersWithIV((CipherParameters)var2, var7.getIV());
         }
      }

      return (CipherParameters)var2;
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      AlgorithmParameterSpec var5 = null;
      if (var3 != null) {
         var5 = SpecUtil.extractSpec(var3, availableSpecs);
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
   protected void engineUpdateAAD(byte[] var1, int var2, int var3) {
      this.cipher.updateAAD(var1, var2, var3);
   }

   @Override
   protected void engineUpdateAAD(ByteBuffer var1) {
      int var2 = var1.remaining();
      if (var2 >= 1) {
         if (var1.hasArray()) {
            this.engineUpdateAAD(var1.array(), var1.arrayOffset() + var1.position(), var2);
            ((Buffer)var1).position(var1.limit());
         } else if (var2 <= 512) {
            byte[] var3 = new byte[var2];
            var1.get(var3);
            this.engineUpdateAAD(var3, 0, var3.length);
            Arrays.fill(var3, (byte)0);
         } else {
            byte[] var5 = new byte[512];

            do {
               int var4 = Math.min(var5.length, var2);
               var1.get(var5, 0, var4);
               this.engineUpdateAAD(var5, 0, var4);
               var2 -= var4;
            } while (var2 > 0);

            Arrays.fill(var5, (byte)0);
         }
      }
   }

   @Override
   protected byte[] engineUpdate(byte[] var1, int var2, int var3) {
      int var4 = this.cipher.getUpdateOutputSize(var3);
      if (var4 > 0) {
         byte[] var5 = new byte[var4];
         int var6 = this.cipher.processBytes(var1, var2, var3, var5, 0);
         if (var6 == 0) {
            return null;
         } else if (var6 != var5.length) {
            byte[] var7 = new byte[var6];
            System.arraycopy(var5, 0, var7, 0, var6);
            return var7;
         } else {
            return var5;
         }
      } else {
         this.cipher.processBytes(var1, var2, var3, null, 0);
         return null;
      }
   }

   @Override
   protected int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException {
      if (var5 + this.cipher.getUpdateOutputSize(var3) > var4.length) {
         throw new ShortBufferException("output buffer too short for input.");
      } else {
         try {
            return this.cipher.processBytes(var1, var2, var3, var4, var5);
         } catch (DataLengthException var7) {
            throw new IllegalStateException(var7.toString());
         }
      }
   }

   @Override
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
      }

      if (var4 == var5.length) {
         return var5;
      } else if (var4 > var5.length) {
         throw new IllegalBlockSizeException("internal buffer overflow");
      } else {
         byte[] var6 = new byte[var4];
         System.arraycopy(var5, 0, var6, 0, var4);
         return var6;
      }
   }

   @Override
   protected int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
      int var6 = 0;
      if (var5 + this.engineGetOutputSize(var3) > var4.length) {
         throw new ShortBufferException("output buffer too short for input.");
      } else {
         try {
            if (var3 != 0) {
               var6 = this.cipher.processBytes(var1, var2, var3, var4, var5);
            }

            return var6 + this.cipher.doFinal(var4, var5 + var6);
         } catch (OutputLengthException var8) {
            throw new IllegalBlockSizeException(var8.getMessage());
         } catch (DataLengthException var9) {
            throw new IllegalBlockSizeException(var9.getMessage());
         }
      }
   }

   private boolean isAEADModeName(String var1) {
      return "CCM".equals(var1) || "EAX".equals(var1) || "GCM".equals(var1) || "GCM-SIV".equals(var1) || "OCB".equals(var1);
   }

   private static class AEADGenericBlockCipher implements BaseBlockCipher.GenericBlockCipher {
      private static final Constructor aeadBadTagConstructor;
      private AEADCipher cipher;

      private static Constructor findExceptionConstructor(Class var0) {
         try {
            return var0.getConstructor(String.class);
         } catch (Exception var2) {
            return null;
         }
      }

      AEADGenericBlockCipher(AEADCipher var1) {
         this.cipher = var1;
      }

      @Override
      public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
         this.cipher.init(var1, var2);
      }

      @Override
      public String getAlgorithmName() {
         return this.cipher instanceof AEADBlockCipher
            ? ((AEADBlockCipher)this.cipher).getUnderlyingCipher().getAlgorithmName()
            : this.cipher.getAlgorithmName();
      }

      @Override
      public boolean wrapOnNoPadding() {
         return false;
      }

      @Override
      public BlockCipher getUnderlyingCipher() {
         return this.cipher instanceof AEADBlockCipher ? ((AEADBlockCipher)this.cipher).getUnderlyingCipher() : null;
      }

      @Override
      public int getOutputSize(int var1) {
         return this.cipher.getOutputSize(var1);
      }

      @Override
      public int getUpdateOutputSize(int var1) {
         return this.cipher.getUpdateOutputSize(var1);
      }

      @Override
      public void updateAAD(byte[] var1, int var2, int var3) {
         this.cipher.processAADBytes(var1, var2, var3);
      }

      @Override
      public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException {
         return this.cipher.processByte(var1, var2, var3);
      }

      @Override
      public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
         return this.cipher.processBytes(var1, var2, var3, var4, var5);
      }

      @Override
      public int doFinal(byte[] var1, int var2) throws IllegalStateException, BadPaddingException {
         try {
            return this.cipher.doFinal(var1, var2);
         } catch (InvalidCipherTextException var7) {
            InvalidCipherTextException var3 = var7;
            if (aeadBadTagConstructor != null) {
               BadPaddingException var4 = null;

               try {
                  var4 = (BadPaddingException)aeadBadTagConstructor.newInstance(var3.getMessage());
               } catch (Exception var6) {
               }

               if (var4 != null) {
                  throw var4;
               }
            }

            throw new BadPaddingException(var7.getMessage());
         }
      }

      static {
         Class var0 = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.AEADBadTagException");
         if (var0 != null) {
            aeadBadTagConstructor = findExceptionConstructor(var0);
         } else {
            aeadBadTagConstructor = null;
         }
      }
   }

   private static class BufferedFPEBlockCipher implements BaseBlockCipher.GenericBlockCipher {
      private FPEEngine cipher;
      private BaseWrapCipher.ErasableOutputStream eOut = new BaseWrapCipher.ErasableOutputStream();

      BufferedFPEBlockCipher(FPEEngine var1) {
         this.cipher = var1;
      }

      @Override
      public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
         this.cipher.init(var1, var2);
      }

      @Override
      public boolean wrapOnNoPadding() {
         return false;
      }

      @Override
      public String getAlgorithmName() {
         return this.cipher.getAlgorithmName();
      }

      @Override
      public BlockCipher getUnderlyingCipher() {
         throw new IllegalStateException("not applicable for FPE");
      }

      @Override
      public int getOutputSize(int var1) {
         return this.eOut.size() + var1;
      }

      @Override
      public int getUpdateOutputSize(int var1) {
         return 0;
      }

      @Override
      public void updateAAD(byte[] var1, int var2, int var3) {
         throw new UnsupportedOperationException("AAD is not supported in the current mode.");
      }

      @Override
      public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException {
         this.eOut.write(var1);
         return 0;
      }

      @Override
      public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
         this.eOut.write(var1, var2, var3);
         return 0;
      }

      @Override
      public int doFinal(byte[] var1, int var2) throws IllegalStateException, BadPaddingException {
         int var3;
         try {
            var3 = this.cipher.processBlock(this.eOut.getBuf(), 0, this.eOut.size(), var1, var2);
         } finally {
            this.eOut.erase();
         }

         return var3;
      }
   }

   private static class BufferedGenericBlockCipher implements BaseBlockCipher.GenericBlockCipher {
      private BufferedBlockCipher cipher;

      BufferedGenericBlockCipher(BufferedBlockCipher var1) {
         this.cipher = var1;
      }

      BufferedGenericBlockCipher(BlockCipher var1) {
         this(var1, new PKCS7Padding());
      }

      BufferedGenericBlockCipher(BlockCipher var1, BlockCipherPadding var2) {
         this.cipher = new PaddedBufferedBlockCipher(var1, var2);
      }

      @Override
      public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
         this.cipher.init(var1, var2);
      }

      @Override
      public boolean wrapOnNoPadding() {
         return !(this.cipher instanceof CTSBlockCipher);
      }

      @Override
      public String getAlgorithmName() {
         return this.cipher.getUnderlyingCipher().getAlgorithmName();
      }

      @Override
      public BlockCipher getUnderlyingCipher() {
         return this.cipher.getUnderlyingCipher();
      }

      @Override
      public int getOutputSize(int var1) {
         return this.cipher.getOutputSize(var1);
      }

      @Override
      public int getUpdateOutputSize(int var1) {
         return this.cipher.getUpdateOutputSize(var1);
      }

      @Override
      public void updateAAD(byte[] var1, int var2, int var3) {
         throw new UnsupportedOperationException("AAD is not supported in the current mode.");
      }

      @Override
      public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException {
         return this.cipher.processByte(var1, var2, var3);
      }

      @Override
      public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
         return this.cipher.processBytes(var1, var2, var3, var4, var5);
      }

      @Override
      public int doFinal(byte[] var1, int var2) throws IllegalStateException, BadPaddingException {
         try {
            return this.cipher.doFinal(var1, var2);
         } catch (InvalidCipherTextException var4) {
            throw new BadPaddingException(var4.getMessage());
         }
      }
   }

   private interface GenericBlockCipher {
      void init(boolean var1, CipherParameters var2) throws IllegalArgumentException;

      boolean wrapOnNoPadding();

      String getAlgorithmName();

      BlockCipher getUnderlyingCipher();

      int getOutputSize(int var1);

      int getUpdateOutputSize(int var1);

      void updateAAD(byte[] var1, int var2, int var3);

      int processByte(byte var1, byte[] var2, int var3) throws DataLengthException;

      int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException;

      int doFinal(byte[] var1, int var2) throws IllegalStateException, BadPaddingException;
   }
}
