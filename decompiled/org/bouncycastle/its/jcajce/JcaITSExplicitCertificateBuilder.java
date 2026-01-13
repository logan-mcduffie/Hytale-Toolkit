package org.bouncycastle.its.jcajce;

import java.security.Provider;
import java.security.interfaces.ECPublicKey;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSExplicitCertificateBuilder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;

public class JcaITSExplicitCertificateBuilder extends ITSExplicitCertificateBuilder {
   private JcaJceHelper helper;

   public JcaITSExplicitCertificateBuilder(ITSContentSigner var1, ToBeSignedCertificate.Builder var2) {
      this(var1, var2, new DefaultJcaJceHelper());
   }

   private JcaITSExplicitCertificateBuilder(ITSContentSigner var1, ToBeSignedCertificate.Builder var2, JcaJceHelper var3) {
      super(var1, var2);
      this.helper = var3;
   }

   public JcaITSExplicitCertificateBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public JcaITSExplicitCertificateBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public ITSCertificate build(CertificateId var1, ECPublicKey var2) {
      return this.build(var1, var2, null);
   }

   public ITSCertificate build(CertificateId var1, ECPublicKey var2, ECPublicKey var3) {
      JceITSPublicEncryptionKey var4 = null;
      if (var3 != null) {
         var4 = new JceITSPublicEncryptionKey(var3, this.helper);
      }

      return super.build(var1, new JcaITSPublicVerificationKey(var2, this.helper), var4);
   }
}
