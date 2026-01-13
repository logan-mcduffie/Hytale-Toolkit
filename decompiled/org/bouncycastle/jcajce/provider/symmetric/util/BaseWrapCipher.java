package org.bouncycastle.jcajce.provider.symmetric.util;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public abstract class BaseWrapCipher extends CipherSpi implements PBE {
   private Class[] availableSpecs = new Class[]{
      GOST28147WrapParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class
   };
   protected int pbeType = 2;
   protected int pbeHash = 1;
   protected int pbeKeySize;
   protected int pbeIvSize;
   protected AlgorithmParameters engineParams = null;
   protected Wrapper wrapEngine = null;
   private int ivSize;
   private byte[] iv;
   private BaseWrapCipher.ErasableOutputStream wrapStream = null;
   private boolean forWrapping;
   private final JcaJceHelper helper = new BCJcaJceHelper();

   protected BaseWrapCipher() {
   }

   protected BaseWrapCipher(Wrapper var1) {
      this(var1, 0);
   }

   protected BaseWrapCipher(int var1, Wrapper var2) {
      this(var1, var2, 0);
   }

   protected BaseWrapCipher(Wrapper var1, int var2) {
      this.wrapEngine = var1;
      this.ivSize = var2;
   }

   protected BaseWrapCipher(int var1, Wrapper var2, int var3) {
      this.pbeKeySize = var1;
      this.wrapEngine = var2;
      this.ivSize = var3;
   }

   @Override
   protected int engineGetBlockSize() {
      return 0;
   }

   @Override
   protected byte[] engineGetIV() {
      return Arrays.clone(this.iv);
   }

   @Override
   protected int engineGetKeySize(Key var1) {
      return var1.getEncoded().length * 8;
   }

   @Override
   protected int engineGetOutputSize(int var1) {
      return -1;
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null && this.iv != null) {
         String var1 = this.wrapEngine.getAlgorithmName();
         if (var1.indexOf(47) >= 0) {
            var1 = var1.substring(0, var1.indexOf(47));
         }

         try {
            this.engineParams = this.createParametersInstance(var1);
            this.engineParams.init(new IvParameterSpec(this.iv));
         } catch (Exception var3) {
            throw new RuntimeException(var3.toString());
         }
      }

      return this.engineParams;
   }

   protected final AlgorithmParameters createParametersInstance(String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      return this.helper.createAlgorithmParameters(var1);
   }

   @Override
   protected void engineSetMode(String var1) throws NoSuchAlgorithmException {
      throw new NoSuchAlgorithmException("can't support mode " + var1);
   }

   @Override
   protected void engineSetPadding(String var1) throws NoSuchPaddingException {
      throw new NoSuchPaddingException("Padding " + var1 + " unknown.");
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      Object var5;
      if (var2 instanceof BCPBEKey) {
         BCPBEKey var6 = (BCPBEKey)var2;
         if (var3 instanceof PBEParameterSpec) {
            var5 = PBE.Util.makePBEParameters(var6, var3, this.wrapEngine.getAlgorithmName());
         } else {
            if (var6.getParam() == null) {
               throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
            }

            var5 = var6.getParam();
         }
      } else {
         var5 = new KeyParameter(var2.getEncoded());
      }

      if (var3 instanceof IvParameterSpec) {
         IvParameterSpec var9 = (IvParameterSpec)var3;
         this.iv = var9.getIV();
         var5 = new ParametersWithIV((CipherParameters)var5, this.iv);
      }

      if (var3 instanceof GOST28147WrapParameterSpec) {
         GOST28147WrapParameterSpec var10 = (GOST28147WrapParameterSpec)var3;
         byte[] var7 = var10.getSBox();
         if (var7 != null) {
            var5 = new ParametersWithSBox((CipherParameters)var5, var7);
         }

         var5 = new ParametersWithUKM((CipherParameters)var5, var10.getUKM());
      }

      if (var5 instanceof KeyParameter && this.ivSize != 0 && (var1 == 3 || var1 == 1)) {
         this.iv = new byte[this.ivSize];
         var4.nextBytes(this.iv);
         var5 = new ParametersWithIV((CipherParameters)var5, this.iv);
      }

      if (var4 != null) {
         var5 = new ParametersWithRandom((CipherParameters)var5, var4);
      }

      try {
         switch (var1) {
            case 1:
               this.wrapEngine.init(true, (CipherParameters)var5);
               this.wrapStream = new BaseWrapCipher.ErasableOutputStream();
               this.forWrapping = true;
               break;
            case 2:
               this.wrapEngine.init(false, (CipherParameters)var5);
               this.wrapStream = new BaseWrapCipher.ErasableOutputStream();
               this.forWrapping = false;
               break;
            case 3:
               this.wrapEngine.init(true, (CipherParameters)var5);
               this.wrapStream = null;
               this.forWrapping = true;
               break;
            case 4:
               this.wrapEngine.init(false, (CipherParameters)var5);
               this.wrapStream = null;
               this.forWrapping = false;
               break;
            default:
               throw new InvalidParameterException("Unknown mode parameter passed to init.");
         }
      } catch (Exception var8) {
         throw new BaseWrapCipher.InvalidKeyOrParametersException(var8.getMessage(), var8);
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

      this.engineParams = var3;
      this.engineInit(var1, var2, var5, var4);
   }

   @Override
   protected void engineInit(int var1, Key var2, SecureRandom var3) throws InvalidKeyException {
      try {
         this.engineInit(var1, var2, (AlgorithmParameterSpec)null, var3);
      } catch (InvalidAlgorithmParameterException var5) {
         throw new BaseWrapCipher.InvalidKeyOrParametersException(var5.getMessage(), var5);
      }
   }

   @Override
   protected byte[] engineUpdate(byte[] var1, int var2, int var3) {
      if (this.wrapStream == null) {
         throw new IllegalStateException("not supported in a wrapping mode");
      } else {
         this.wrapStream.write(var1, var2, var3);
         return null;
      }
   }

   @Override
   protected int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException {
      if (this.wrapStream == null) {
         throw new IllegalStateException("not supported in a wrapping mode");
      } else {
         this.wrapStream.write(var1, var2, var3);
         return 0;
      }
   }

   @Override
   protected byte[] engineDoFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException {
      if (this.wrapStream == null) {
         throw new IllegalStateException("not supported in a wrapping mode");
      } else {
         if (var1 != null) {
            this.wrapStream.write(var1, var2, var3);
         }

         byte[] var4;
         try {
            if (!this.forWrapping) {
               try {
                  return this.wrapEngine.unwrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
               } catch (InvalidCipherTextException var10) {
                  throw new BadPaddingException(var10.getMessage());
               }
            }

            try {
               var4 = this.wrapEngine.wrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
            } catch (Exception var9) {
               throw new IllegalBlockSizeException(var9.getMessage());
            }
         } finally {
            this.wrapStream.erase();
         }

         return var4;
      }
   }

   @Override
   protected int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
      if (this.wrapStream == null) {
         throw new IllegalStateException("not supported in a wrapping mode");
      } else {
         this.wrapStream.write(var1, var2, var3);

         int var7;
         try {
            byte[] var6;
            if (this.forWrapping) {
               try {
                  var6 = this.wrapEngine.wrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
               } catch (Exception var13) {
                  throw new IllegalBlockSizeException(var13.getMessage());
               }
            } else {
               try {
                  var6 = this.wrapEngine.unwrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
               } catch (InvalidCipherTextException var12) {
                  throw new BadPaddingException(var12.getMessage());
               }
            }

            if (var5 + var6.length > var4.length) {
               throw new ShortBufferException("output buffer too short for input.");
            }

            System.arraycopy(var6, 0, var4, var5, var6.length);
            var7 = var6.length;
         } finally {
            this.wrapStream.erase();
         }

         return var7;
      }
   }

   @Override
   protected byte[] engineWrap(Key var1) throws IllegalBlockSizeException, InvalidKeyException {
      byte[] var2 = var1.getEncoded();
      if (var2 == null) {
         throw new InvalidKeyException("Cannot wrap key, null encoding.");
      } else {
         try {
            return this.wrapEngine == null ? this.engineDoFinal(var2, 0, var2.length) : this.wrapEngine.wrap(var2, 0, var2.length);
         } catch (BadPaddingException var4) {
            throw new IllegalBlockSizeException(var4.getMessage());
         }
      }
   }

   @Override
   protected Key engineUnwrap(byte[] var1, String var2, int var3) throws InvalidKeyException, NoSuchAlgorithmException {
      byte[] var4;
      try {
         if (this.wrapEngine == null) {
            var4 = this.engineDoFinal(var1, 0, var1.length);
         } else {
            var4 = this.wrapEngine.unwrap(var1, 0, var1.length);
         }
      } catch (InvalidCipherTextException var8) {
         throw new InvalidKeyException(var8.getMessage());
      } catch (BadPaddingException var9) {
         throw new InvalidKeyException(var9.getMessage());
      } catch (IllegalBlockSizeException var10) {
         throw new InvalidKeyException(var10.getMessage());
      }

      if (var3 == 3) {
         return new SecretKeySpec(var4, var2);
      } else if (var2.equals("") && var3 == 2) {
         try {
            PrivateKeyInfo var13 = PrivateKeyInfo.getInstance(var4);
            PrivateKey var6 = BouncyCastleProvider.getPrivateKey(var13);
            if (var6 != null) {
               return var6;
            } else {
               throw new InvalidKeyException("algorithm " + var13.getPrivateKeyAlgorithm().getAlgorithm() + " not supported");
            }
         } catch (Exception var7) {
            throw new InvalidKeyException("Invalid key encoding.");
         }
      } else {
         try {
            KeyFactory var5 = this.helper.createKeyFactory(var2);
            if (var3 == 1) {
               return var5.generatePublic(new X509EncodedKeySpec(var4));
            }

            if (var3 == 2) {
               return var5.generatePrivate(new PKCS8EncodedKeySpec(var4));
            }
         } catch (NoSuchProviderException var11) {
            throw new InvalidKeyException("Unknown key type " + var11.getMessage());
         } catch (InvalidKeySpecException var12) {
            throw new InvalidKeyException("Unknown key type " + var12.getMessage());
         }

         throw new InvalidKeyException("Unknown key type " + var3);
      }
   }

   protected static final class ErasableOutputStream extends ByteArrayOutputStream {
      public ErasableOutputStream() {
      }

      public byte[] getBuf() {
         return this.buf;
      }

      public void erase() {
         Arrays.fill(this.buf, (byte)0);
         this.reset();
      }
   }

   protected static class InvalidKeyOrParametersException extends InvalidKeyException {
      private final Throwable cause;

      InvalidKeyOrParametersException(String var1, Throwable var2) {
         super(var1);
         this.cause = var2;
      }

      @Override
      public Throwable getCause() {
         return this.cause;
      }
   }
}
