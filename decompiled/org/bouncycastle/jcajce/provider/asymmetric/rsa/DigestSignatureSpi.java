package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;
import org.bouncycastle.util.Arrays;

public class DigestSignatureSpi extends SignatureSpi {
   private Digest digest;
   private AsymmetricBlockCipher cipher;
   private AlgorithmIdentifier algId;

   protected DigestSignatureSpi(Digest var1, AsymmetricBlockCipher var2) {
      this.digest = var1;
      this.cipher = var2;
      this.algId = null;
   }

   protected DigestSignatureSpi(ASN1ObjectIdentifier var1, Digest var2, AsymmetricBlockCipher var3) {
      this.digest = var2;
      this.cipher = var3;
      this.algId = new AlgorithmIdentifier(var1, DERNull.INSTANCE);
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (!(var1 instanceof RSAPublicKey)) {
         throw new InvalidKeyException("Supplied key (" + this.getType(var1) + ") is not a RSAPublicKey instance");
      } else {
         RSAKeyParameters var2 = RSAUtil.generatePublicKeyParameter((RSAPublicKey)var1);
         this.digest.reset();
         this.cipher.init(false, var2);
      }
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof AnnotatedPrivateKey) {
         var1 = ((AnnotatedPrivateKey)var1).getKey();
      }

      if (!(var1 instanceof RSAPrivateKey)) {
         throw new InvalidKeyException("Supplied key (" + this.getType(var1) + ") is not a RSAPrivateKey instance");
      } else {
         RSAKeyParameters var2 = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)var1);
         this.digest.reset();
         this.cipher.init(true, var2);
      }
   }

   private String getType(Object var1) {
      return var1 == null ? null : var1.getClass().getName();
   }

   @Override
   protected void engineUpdate(byte var1) throws SignatureException {
      this.digest.update(var1);
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      this.digest.update(var1, var2, var3);
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);

      try {
         byte[] var2 = this.derEncode(var1);
         return this.cipher.processBlock(var2, 0, var2.length);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new SignatureException("key too small for signature type");
      } catch (Exception var4) {
         throw new SignatureException(var4.toString());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      byte[] var2 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var2, 0);

      byte[] var3;
      byte[] var4;
      try {
         var3 = this.cipher.processBlock(var1, 0, var1.length);
         var4 = this.derEncode(var2);
      } catch (Exception var9) {
         return false;
      }

      if (var3.length == var4.length) {
         return Arrays.constantTimeAreEqual(var3, var4);
      } else if (var3.length != var4.length - 2) {
         Arrays.constantTimeAreEqual(var4, var4);
         return false;
      } else {
         var4[1] = (byte)(var4[1] - 2);
         var4[3] = (byte)(var4[3] - 2);
         int var5 = 4 + var4[3];
         int var6 = var5 + 2;
         int var7 = 0;

         for (int var8 = 0; var8 < var4.length - var6; var8++) {
            var7 |= var3[var5 + var8] ^ var4[var6 + var8];
         }

         for (int var10 = 0; var10 < var5; var10++) {
            var7 |= var3[var10] ^ var4[var10];
         }

         return var7 == 0;
      }
   }

   @Override
   protected void engineSetParameter(AlgorithmParameterSpec var1) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   /** @deprecated */
   @Override
   protected void engineSetParameter(String var1, Object var2) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   /** @deprecated */
   @Override
   protected Object engineGetParameter(String var1) {
      return null;
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      return null;
   }

   private byte[] derEncode(byte[] var1) throws IOException {
      if (this.algId == null) {
         return var1;
      } else {
         DigestInfo var2 = new DigestInfo(this.algId, var1);
         return var2.getEncoded("DER");
      }
   }

   public static class MD2 extends DigestSignatureSpi {
      public MD2() {
         super(PKCSObjectIdentifiers.md2, new MD2Digest(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class MD4 extends DigestSignatureSpi {
      public MD4() {
         super(PKCSObjectIdentifiers.md4, new MD4Digest(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class MD5 extends DigestSignatureSpi {
      public MD5() {
         super(PKCSObjectIdentifiers.md5, DigestFactory.createMD5(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class RIPEMD128 extends DigestSignatureSpi {
      public RIPEMD128() {
         super(TeleTrusTObjectIdentifiers.ripemd128, new RIPEMD128Digest(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class RIPEMD160 extends DigestSignatureSpi {
      public RIPEMD160() {
         super(TeleTrusTObjectIdentifiers.ripemd160, new RIPEMD160Digest(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class RIPEMD256 extends DigestSignatureSpi {
      public RIPEMD256() {
         super(TeleTrusTObjectIdentifiers.ripemd256, new RIPEMD256Digest(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA1 extends DigestSignatureSpi {
      public SHA1() {
         super(OIWObjectIdentifiers.idSHA1, DigestFactory.createSHA1(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA224 extends DigestSignatureSpi {
      public SHA224() {
         super(NISTObjectIdentifiers.id_sha224, DigestFactory.createSHA224(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA256 extends DigestSignatureSpi {
      public SHA256() {
         super(NISTObjectIdentifiers.id_sha256, DigestFactory.createSHA256(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA384 extends DigestSignatureSpi {
      public SHA384() {
         super(NISTObjectIdentifiers.id_sha384, DigestFactory.createSHA384(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA3_224 extends DigestSignatureSpi {
      public SHA3_224() {
         super(NISTObjectIdentifiers.id_sha3_224, DigestFactory.createSHA3_224(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA3_256 extends DigestSignatureSpi {
      public SHA3_256() {
         super(NISTObjectIdentifiers.id_sha3_256, DigestFactory.createSHA3_256(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA3_384 extends DigestSignatureSpi {
      public SHA3_384() {
         super(NISTObjectIdentifiers.id_sha3_384, DigestFactory.createSHA3_384(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA3_512 extends DigestSignatureSpi {
      public SHA3_512() {
         super(NISTObjectIdentifiers.id_sha3_512, DigestFactory.createSHA3_512(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA512 extends DigestSignatureSpi {
      public SHA512() {
         super(NISTObjectIdentifiers.id_sha512, DigestFactory.createSHA512(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA512_224 extends DigestSignatureSpi {
      public SHA512_224() {
         super(NISTObjectIdentifiers.id_sha512_224, DigestFactory.createSHA512_224(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class SHA512_256 extends DigestSignatureSpi {
      public SHA512_256() {
         super(NISTObjectIdentifiers.id_sha512_256, DigestFactory.createSHA512_256(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class noneRSA extends DigestSignatureSpi {
      public noneRSA() {
         super(new NullDigest(), new PKCS1Encoding(new RSABlindedEngine()));
      }
   }
}
