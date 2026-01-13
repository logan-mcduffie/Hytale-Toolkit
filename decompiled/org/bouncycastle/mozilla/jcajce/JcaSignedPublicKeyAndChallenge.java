package org.bouncycastle.mozilla.jcajce;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.mozilla.SignedPublicKeyAndChallenge;

public class JcaSignedPublicKeyAndChallenge extends SignedPublicKeyAndChallenge {
   JcaJceHelper helper = new DefaultJcaJceHelper();

   private JcaSignedPublicKeyAndChallenge(org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge var1, JcaJceHelper var2) {
      super(var1);
      this.helper = var2;
   }

   public JcaSignedPublicKeyAndChallenge(byte[] var1) {
      super(var1);
   }

   public JcaSignedPublicKeyAndChallenge setProvider(String var1) {
      return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, new NamedJcaJceHelper(var1));
   }

   public JcaSignedPublicKeyAndChallenge setProvider(Provider var1) {
      return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, new ProviderJcaJceHelper(var1));
   }

   public PublicKey getPublicKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
      try {
         SubjectPublicKeyInfo var1 = this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
         X509EncodedKeySpec var2 = new X509EncodedKeySpec(var1.getEncoded());
         AlgorithmIdentifier var3 = var1.getAlgorithm();
         KeyFactory var4 = this.helper.createKeyFactory(var3.getAlgorithm().getId());
         return var4.generatePublic(var2);
      } catch (Exception var5) {
         throw new InvalidKeyException("error encoding public key");
      }
   }
}
