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
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.XMSSParameterSpec;

public class XMSSKeyPairGeneratorSpi extends KeyPairGenerator {
   private XMSSKeyGenerationParameters param;
   private ASN1ObjectIdentifier treeDigest;
   private XMSSKeyPairGenerator engine = new XMSSKeyPairGenerator();
   private SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   private boolean initialised = false;

   public XMSSKeyPairGeneratorSpi() {
      super("XMSS");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof XMSSParameterSpec)) {
         throw new InvalidAlgorithmParameterException("parameter object not a XMSSParameterSpec");
      } else {
         XMSSParameterSpec var3 = (XMSSParameterSpec)var1;
         if (var3.getTreeDigest().equals("SHA256")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha256;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(var3.getHeight(), new SHA256Digest()), var2);
         } else if (var3.getTreeDigest().equals("SHA512")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha512;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(var3.getHeight(), new SHA512Digest()), var2);
         } else if (var3.getTreeDigest().equals("SHAKE128")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake128;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(var3.getHeight(), new SHAKEDigest(128)), var2);
         } else if (var3.getTreeDigest().equals("SHAKE256")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake256;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(var3.getHeight(), new SHAKEDigest(256)), var2);
         }

         this.engine.init(this.param);
         this.initialised = true;
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new XMSSKeyGenerationParameters(new XMSSParameters(10, new SHA512Digest()), this.random);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      XMSSPublicKeyParameters var2 = (XMSSPublicKeyParameters)var1.getPublic();
      XMSSPrivateKeyParameters var3 = (XMSSPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCXMSSPublicKey(this.treeDigest, var2), new BCXMSSPrivateKey(this.treeDigest, var3));
   }
}
