package org.bouncycastle.pkcs.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;

public class BcPKCS12PBEOutputEncryptorBuilder {
   private ExtendedDigest digest;
   private BufferedBlockCipher engine;
   private ASN1ObjectIdentifier algorithm;
   private SecureRandom random;
   private int iterationCount = 1024;

   public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier var1, BlockCipher var2) {
      this(var1, var2, new SHA1Digest());
   }

   public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier var1, BlockCipher var2, ExtendedDigest var3) {
      this.algorithm = var1;
      this.engine = new PaddedBufferedBlockCipher(var2, new PKCS7Padding());
      this.digest = var3;
   }

   public BcPKCS12PBEOutputEncryptorBuilder setIterationCount(int var1) {
      this.iterationCount = var1;
      return this;
   }

   public OutputEncryptor build(final char[] var1) {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      byte[] var2 = new byte[20];
      this.random.nextBytes(var2);
      final PKCS12PBEParams var3 = new PKCS12PBEParams(var2, this.iterationCount);
      CipherParameters var4 = PKCS12PBEUtils.createCipherParameters(this.algorithm, this.digest, this.engine.getBlockSize(), var3, var1);
      this.engine.init(true, var4);
      return new OutputEncryptor() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, var3);
         }

         @Override
         public OutputStream getOutputStream(OutputStream var1x) {
            return new CipherOutputStream(var1x, BcPKCS12PBEOutputEncryptorBuilder.this.engine);
         }

         @Override
         public GenericKey getKey() {
            return new GenericKey(
               new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, var3), PKCS12ParametersGenerator.PKCS12PasswordToBytes(var1)
            );
         }
      };
   }
}
