package org.bouncycastle.crypto.signers;

import java.io.IOException;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class RSADigestSigner implements Signer {
   private final AsymmetricBlockCipher rsaEngine = new PKCS1Encoding(new RSABlindedEngine());
   private final AlgorithmIdentifier algId;
   private final Digest digest;
   private boolean forSigning;
   private static final Hashtable oidMap = new Hashtable();

   public RSADigestSigner(Digest var1) {
      this(var1, (ASN1ObjectIdentifier)oidMap.get(var1.getAlgorithmName()));
   }

   public RSADigestSigner(Digest var1, ASN1ObjectIdentifier var2) {
      this.digest = var1;
      if (var2 != null) {
         this.algId = new AlgorithmIdentifier(var2, DERNull.INSTANCE);
      } else {
         this.algId = null;
      }
   }

   /** @deprecated */
   public String getAlgorithmName() {
      return this.digest.getAlgorithmName() + "withRSA";
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forSigning = var1;
      AsymmetricKeyParameter var3;
      if (var2 instanceof ParametersWithRandom) {
         var3 = (AsymmetricKeyParameter)((ParametersWithRandom)var2).getParameters();
      } else {
         var3 = (AsymmetricKeyParameter)var2;
      }

      if (var1 && !var3.isPrivate()) {
         throw new IllegalArgumentException("signing requires private key");
      } else if (!var1 && var3.isPrivate()) {
         throw new IllegalArgumentException("verification requires public key");
      } else {
         this.reset();
         this.rsaEngine.init(var1, var2);
      }
   }

   @Override
   public void update(byte var1) {
      this.digest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.digest.update(var1, var2, var3);
   }

   @Override
   public byte[] generateSignature() throws CryptoException, DataLengthException {
      if (!this.forSigning) {
         throw new IllegalStateException("RSADigestSigner not initialised for signature generation.");
      } else {
         byte[] var1 = new byte[this.digest.getDigestSize()];
         this.digest.doFinal(var1, 0);

         try {
            byte[] var2 = this.derEncode(var1);
            return this.rsaEngine.processBlock(var2, 0, var2.length);
         } catch (IOException var3) {
            throw new CryptoException("unable to encode signature: " + var3.getMessage(), var3);
         }
      }
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      if (this.forSigning) {
         throw new IllegalStateException("RSADigestSigner not initialised for verification");
      } else {
         byte[] var2 = new byte[this.digest.getDigestSize()];
         this.digest.doFinal(var2, 0);

         byte[] var3;
         byte[] var4;
         try {
            var3 = this.rsaEngine.processBlock(var1, 0, var1.length);
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
            int var5 = var3.length - var2.length - 2;
            int var6 = var4.length - var2.length - 2;
            var4[1] = (byte)(var4[1] - 2);
            var4[3] = (byte)(var4[3] - 2);
            int var7 = 0;

            for (int var8 = 0; var8 < var2.length; var8++) {
               var7 |= var3[var5 + var8] ^ var4[var6 + var8];
            }

            for (int var10 = 0; var10 < var5; var10++) {
               var7 |= var3[var10] ^ var4[var10];
            }

            return var7 == 0;
         }
      }
   }

   @Override
   public void reset() {
      this.digest.reset();
   }

   private byte[] derEncode(byte[] var1) throws IOException {
      if (this.algId == null) {
         try {
            DigestInfo.getInstance(var1);
            return var1;
         } catch (IllegalArgumentException var3) {
            throw new IOException("malformed DigestInfo for NONEwithRSA hash: " + var3.getMessage());
         }
      } else {
         DigestInfo var2 = new DigestInfo(this.algId, var1);
         return var2.getEncoded("DER");
      }
   }

   static {
      oidMap.put("RIPEMD128", TeleTrusTObjectIdentifiers.ripemd128);
      oidMap.put("RIPEMD160", TeleTrusTObjectIdentifiers.ripemd160);
      oidMap.put("RIPEMD256", TeleTrusTObjectIdentifiers.ripemd256);
      oidMap.put("SHA-1", X509ObjectIdentifiers.id_SHA1);
      oidMap.put("SHA-224", NISTObjectIdentifiers.id_sha224);
      oidMap.put("SHA-256", NISTObjectIdentifiers.id_sha256);
      oidMap.put("SHA-384", NISTObjectIdentifiers.id_sha384);
      oidMap.put("SHA-512", NISTObjectIdentifiers.id_sha512);
      oidMap.put("SHA-512/224", NISTObjectIdentifiers.id_sha512_224);
      oidMap.put("SHA-512/256", NISTObjectIdentifiers.id_sha512_256);
      oidMap.put("SHA3-224", NISTObjectIdentifiers.id_sha3_224);
      oidMap.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
      oidMap.put("SHA3-384", NISTObjectIdentifiers.id_sha3_384);
      oidMap.put("SHA3-512", NISTObjectIdentifiers.id_sha3_512);
      oidMap.put("MD2", PKCSObjectIdentifiers.md2);
      oidMap.put("MD4", PKCSObjectIdentifiers.md4);
      oidMap.put("MD5", PKCSObjectIdentifiers.md5);
   }
}
