package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public abstract class BaseCipherSpi extends CipherSpi {
   private Class[] availableSpecs = new Class[]{IvParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class};
   private final JcaJceHelper helper = new BCJcaJceHelper();
   protected AlgorithmParameters engineParams = null;
   protected Wrapper wrapEngine = null;
   private int ivSize;
   private byte[] iv;

   protected BaseCipherSpi() {
   }

   @Override
   protected int engineGetBlockSize() {
      return 0;
   }

   @Override
   protected byte[] engineGetIV() {
      return null;
   }

   @Override
   protected int engineGetKeySize(Key var1) {
      return var1.getEncoded().length;
   }

   @Override
   protected int engineGetOutputSize(int var1) {
      return -1;
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      return null;
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
   protected Key engineUnwrap(byte[] var1, String var2, int var3) throws InvalidKeyException {
      byte[] var4;
      try {
         if (this.wrapEngine == null) {
            var4 = this.engineDoFinal(var1, 0, var1.length);
         } else {
            var4 = this.wrapEngine.unwrap(var1, 0, var1.length);
         }
      } catch (InvalidCipherTextException var8) {
         throw new InvalidKeyException(var8.getMessage());
      } catch (final BadPaddingException var9) {
         throw new InvalidKeyException("unable to unwrap") {
            @Override
            public synchronized Throwable getCause() {
               return var9;
            }
         };
      } catch (IllegalBlockSizeException var10) {
         throw new InvalidKeyException(var10.getMessage());
      }

      if (var3 == 3) {
         return new SecretKeySpec(var4, var2);
      } else if (var2.equals("") && var3 == 2) {
         try {
            PrivateKeyInfo var14 = PrivateKeyInfo.getInstance(var4);
            PrivateKey var6 = BouncyCastleProvider.getPrivateKey(var14);
            if (var6 != null) {
               return var6;
            } else {
               throw new InvalidKeyException("algorithm " + var14.getPrivateKeyAlgorithm().getAlgorithm() + " not supported");
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
         } catch (NoSuchAlgorithmException var11) {
            throw new InvalidKeyException("Unknown key type " + var11.getMessage());
         } catch (InvalidKeySpecException var12) {
            throw new InvalidKeyException("Unknown key type " + var12.getMessage());
         } catch (NoSuchProviderException var13) {
            throw new InvalidKeyException("Unknown key type " + var13.getMessage());
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
}
