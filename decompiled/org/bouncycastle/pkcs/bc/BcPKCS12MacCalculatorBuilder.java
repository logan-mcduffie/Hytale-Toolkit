package org.bouncycastle.pkcs.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

public class BcPKCS12MacCalculatorBuilder implements PKCS12MacCalculatorBuilder {
   private ExtendedDigest digest;
   private AlgorithmIdentifier algorithmIdentifier;
   private SecureRandom random;
   private int saltLength;
   private int iterationCount = 1024;

   public BcPKCS12MacCalculatorBuilder() {
      this(new SHA1Digest(), new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE));
   }

   public BcPKCS12MacCalculatorBuilder(ExtendedDigest var1, AlgorithmIdentifier var2) {
      this.digest = var1;
      this.algorithmIdentifier = var2;
      this.saltLength = var1.getDigestSize();
   }

   public BcPKCS12MacCalculatorBuilder setIterationCount(int var1) {
      this.iterationCount = var1;
      return this;
   }

   @Override
   public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
      return this.algorithmIdentifier;
   }

   @Override
   public MacCalculator build(char[] var1) {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      byte[] var2 = new byte[this.saltLength];
      this.random.nextBytes(var2);
      return PKCS12PBEUtils.createMacCalculator(this.algorithmIdentifier.getAlgorithm(), this.digest, new PKCS12PBEParams(var2, this.iterationCount), var1);
   }
}
