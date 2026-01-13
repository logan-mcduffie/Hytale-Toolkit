package org.bouncycastle.its.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Arrays;

public class JcaITSContentSigner implements ITSContentSigner {
   private final ECPrivateKey privateKey;
   private final ITSCertificate signerCert;
   private final AlgorithmIdentifier digestAlgo;
   private final DigestCalculator digest;
   private final byte[] parentData;
   private final ASN1ObjectIdentifier curveID;
   private final byte[] parentDigest;
   private final String signer;
   private final JcaJceHelper helper;

   private JcaITSContentSigner(ECPrivateKey var1, ITSCertificate var2, JcaJceHelper var3) {
      this.privateKey = var1;
      this.signerCert = var2;
      this.helper = var3;
      PrivateKeyInfo var4 = PrivateKeyInfo.getInstance(var1.getEncoded());
      this.curveID = ASN1ObjectIdentifier.getInstance(var4.getPrivateKeyAlgorithm().getParameters());
      if (this.curveID.equals(SECObjectIdentifiers.secp256r1)) {
         this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
         this.signer = "SHA256withECDSA";
      } else if (this.curveID.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
         this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
         this.signer = "SHA256withECDSA";
      } else {
         if (!this.curveID.equals(TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
            throw new IllegalArgumentException("unknown key type");
         }

         this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
         this.signer = "SHA384withECDSA";
      }

      DigestCalculatorProvider var5;
      try {
         JcaDigestCalculatorProviderBuilder var6 = new JcaDigestCalculatorProviderBuilder().setHelper(var3);
         var5 = var6.build();
      } catch (Exception var9) {
         throw new IllegalStateException(var9.getMessage(), var9);
      }

      try {
         this.digest = var5.get(this.digestAlgo);
      } catch (OperatorCreationException var8) {
         throw new IllegalStateException("cannot recognise digest type: " + this.digestAlgo.getAlgorithm(), var8);
      }

      if (var2 != null) {
         try {
            this.parentData = var2.getEncoded();
            OutputStream var10 = this.digest.getOutputStream();
            var10.write(this.parentData, 0, this.parentData.length);
            var10.close();
            this.parentDigest = this.digest.getDigest();
         } catch (IOException var7) {
            throw new IllegalStateException("signer certificate encoding failed: " + var7.getMessage());
         }
      } else {
         this.parentData = null;
         this.parentDigest = this.digest.getDigest();
      }
   }

   @Override
   public OutputStream getOutputStream() {
      return this.digest.getOutputStream();
   }

   @Override
   public byte[] getSignature() {
      byte[] var1 = this.digest.getDigest();

      try {
         Signature var2 = this.helper.createSignature(this.signer);
         var2.initSign(this.privateKey);
         var2.update(var1, 0, var1.length);
         var2.update(this.parentDigest, 0, this.parentDigest.length);
         return var2.sign();
      } catch (Exception var4) {
         throw new RuntimeException(var4.getMessage(), var4);
      }
   }

   @Override
   public ITSCertificate getAssociatedCertificate() {
      return this.signerCert;
   }

   @Override
   public byte[] getAssociatedCertificateDigest() {
      return Arrays.clone(this.parentDigest);
   }

   @Override
   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.digestAlgo;
   }

   @Override
   public ASN1ObjectIdentifier getCurveID() {
      return this.curveID;
   }

   @Override
   public boolean isForSelfSigning() {
      return this.parentData == null;
   }

   public static class Builder {
      private JcaJceHelper helper = new DefaultJcaJceHelper();

      public JcaITSContentSigner.Builder setProvider(Provider var1) {
         this.helper = new ProviderJcaJceHelper(var1);
         return this;
      }

      public JcaITSContentSigner.Builder setProvider(String var1) {
         this.helper = new NamedJcaJceHelper(var1);
         return this;
      }

      public JcaITSContentSigner build(PrivateKey var1) {
         return new JcaITSContentSigner((ECPrivateKey)var1, null, this.helper);
      }

      public JcaITSContentSigner build(PrivateKey var1, ITSCertificate var2) {
         return new JcaITSContentSigner((ECPrivateKey)var1, var2, this.helper);
      }
   }
}
