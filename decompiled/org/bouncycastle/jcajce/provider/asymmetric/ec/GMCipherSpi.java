package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.Blake2sDigest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class GMCipherSpi extends CipherSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private SM2Engine engine;
   private int state = -1;
   private GMCipherSpi.ErasableOutputStream buffer = new GMCipherSpi.ErasableOutputStream();
   private AsymmetricKeyParameter key;
   private SecureRandom random;

   public GMCipherSpi(SM2Engine var1) {
      this.engine = var1;
   }

   @Override
   public int engineGetBlockSize() {
      return 0;
   }

   @Override
   public int engineGetKeySize(Key var1) {
      if (var1 instanceof ECKey) {
         return ((ECKey)var1).getParameters().getCurve().getFieldSize();
      } else {
         throw new IllegalArgumentException("not an EC key");
      }
   }

   @Override
   public byte[] engineGetIV() {
      return null;
   }

   @Override
   public AlgorithmParameters engineGetParameters() {
      return null;
   }

   @Override
   public void engineSetMode(String var1) throws NoSuchAlgorithmException {
      String var2 = Strings.toUpperCase(var1);
      if (!var2.equals("NONE")) {
         throw new IllegalArgumentException("can't support mode " + var1);
      }
   }

   @Override
   public int engineGetOutputSize(int var1) {
      if (this.state == 1 || this.state == 3) {
         return this.engine.getOutputSize(var1);
      } else if (this.state != 2 && this.state != 4) {
         throw new IllegalStateException("cipher not initialised");
      } else {
         return this.engine.getOutputSize(var1);
      }
   }

   @Override
   public void engineSetPadding(String var1) throws NoSuchPaddingException {
      String var2 = Strings.toUpperCase(var1);
      if (!var2.equals("NOPADDING")) {
         throw new NoSuchPaddingException("padding not available with IESCipher");
      }
   }

   @Override
   public void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      Object var5 = null;
      if (var3 != null) {
         throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + var3.getClass().getName());
      } else {
         this.engineInit(var1, var2, (AlgorithmParameterSpec)var5, var4);
      }
   }

   @Override
   public void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidAlgorithmParameterException, InvalidKeyException {
      if (var1 != 1 && var1 != 3) {
         if (var1 != 2 && var1 != 4) {
            throw new InvalidKeyException("must be passed EC key");
         }

         if (!(var2 instanceof PrivateKey)) {
            throw new InvalidKeyException("must be passed private EC key for decryption");
         }

         this.key = ECUtil.generatePrivateKeyParameter((PrivateKey)var2);
      } else {
         if (!(var2 instanceof PublicKey)) {
            throw new InvalidKeyException("must be passed public EC key for encryption");
         }

         this.key = ECUtils.generatePublicKeyParameter((PublicKey)var2);
      }

      if (var4 != null) {
         this.random = var4;
      } else {
         this.random = CryptoServicesRegistrar.getSecureRandom();
      }

      this.state = var1;
      this.buffer.reset();
   }

   @Override
   public void engineInit(int var1, Key var2, SecureRandom var3) throws InvalidKeyException {
      try {
         this.engineInit(var1, var2, (AlgorithmParameterSpec)null, var3);
      } catch (InvalidAlgorithmParameterException var5) {
         throw new IllegalArgumentException("cannot handle supplied parameter spec: " + var5.getMessage());
      }
   }

   @Override
   public byte[] engineUpdate(byte[] var1, int var2, int var3) {
      this.buffer.write(var1, var2, var3);
      return null;
   }

   @Override
   public int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      this.buffer.write(var1, var2, var3);
      return 0;
   }

   @Override
   public byte[] engineDoFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException {
      if (var3 != 0) {
         this.buffer.write(var1, var2, var3);
      }

      byte[] var4;
      try {
         if (this.state == 1 || this.state == 3) {
            try {
               this.engine.init(true, new ParametersWithRandom(this.key, this.random));
               return this.engine.processBlock(this.buffer.getBuf(), 0, this.buffer.size());
            } catch (Exception var10) {
               throw new BadBlockException("unable to process block", var10);
            }
         }

         if (this.state != 2 && this.state != 4) {
            throw new IllegalStateException("cipher not initialised");
         }

         try {
            this.engine.init(false, this.key);
            var4 = this.engine.processBlock(this.buffer.getBuf(), 0, this.buffer.size());
         } catch (Exception var9) {
            throw new BadBlockException("unable to process block", var9);
         }
      } finally {
         this.buffer.erase();
      }

      return var4;
   }

   @Override
   public int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
      byte[] var6 = this.engineDoFinal(var1, var2, var3);
      System.arraycopy(var6, 0, var4, var5, var6.length);
      return var6.length;
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

   public static class SM2 extends GMCipherSpi {
      public SM2() {
         super(new SM2Engine());
      }
   }

   public static class SM2withBlake2b extends GMCipherSpi {
      public SM2withBlake2b() {
         super(new SM2Engine(new Blake2bDigest(512)));
      }
   }

   public static class SM2withBlake2s extends GMCipherSpi {
      public SM2withBlake2s() {
         super(new SM2Engine(new Blake2sDigest(256)));
      }
   }

   public static class SM2withMD5 extends GMCipherSpi {
      public SM2withMD5() {
         super(new SM2Engine(new MD5Digest()));
      }
   }

   public static class SM2withRMD extends GMCipherSpi {
      public SM2withRMD() {
         super(new SM2Engine(new RIPEMD160Digest()));
      }
   }

   public static class SM2withSha1 extends GMCipherSpi {
      public SM2withSha1() {
         super(new SM2Engine(new SHA1Digest()));
      }
   }

   public static class SM2withSha224 extends GMCipherSpi {
      public SM2withSha224() {
         super(new SM2Engine(new SHA224Digest()));
      }
   }

   public static class SM2withSha256 extends GMCipherSpi {
      public SM2withSha256() {
         super(new SM2Engine(SHA256Digest.newInstance()));
      }
   }

   public static class SM2withSha384 extends GMCipherSpi {
      public SM2withSha384() {
         super(new SM2Engine(new SHA384Digest()));
      }
   }

   public static class SM2withSha512 extends GMCipherSpi {
      public SM2withSha512() {
         super(new SM2Engine(new SHA512Digest()));
      }
   }

   public static class SM2withWhirlpool extends GMCipherSpi {
      public SM2withWhirlpool() {
         super(new SM2Engine(new WhirlpoolDigest()));
      }
   }
}
