package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.XMSSMTParameterSpec;

public class XMSSMTKeyPairGeneratorSpi extends KeyPairGenerator {
   private XMSSMTKeyGenerationParameters param;
   private XMSSMTKeyPairGenerator engine = new XMSSMTKeyPairGenerator();
   private ASN1ObjectIdentifier treeDigest;
   private SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   private boolean initialised = false;

   public XMSSMTKeyPairGeneratorSpi() {
      super("XMSSMT");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof XMSSMTParameterSpec)) {
         throw new InvalidAlgorithmParameterException("parameter object not a XMSSMTParameterSpec");
      } else {
         XMSSMTParameterSpec var3 = (XMSSMTParameterSpec)var1;
         if (var3.getTreeDigest().equals("SHA256")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha256;
            this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(var3.getHeight(), var3.getLayers(), new SHA256Digest()), var2);
         } else if (var3.getTreeDigest().equals("SHA512")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha512;
            this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(var3.getHeight(), var3.getLayers(), new SHA512Digest()), var2);
         } else if (var3.getTreeDigest().equals("SHAKE128")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake128;
            this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(var3.getHeight(), var3.getLayers(), new SHAKEDigest(128)), var2);
         } else if (var3.getTreeDigest().equals("SHAKE256")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake256;
            this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(var3.getHeight(), var3.getLayers(), new SHAKEDigest(256)), var2);
         }

         this.engine.init(this.param);
         this.initialised = true;
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(10, 20, new SHA512Digest()), this.random);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      XMSSMTPublicKeyParameters var2 = (XMSSMTPublicKeyParameters)var1.getPublic();
      XMSSMTPrivateKeyParameters var3 = (XMSSMTPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCXMSSMTPublicKey(this.treeDigest, var2), new BCXMSSMTPrivateKey(this.treeDigest, var3));
   }
}
