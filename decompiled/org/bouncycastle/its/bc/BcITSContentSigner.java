package org.bouncycastle.its.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.util.Arrays;

public class BcITSContentSigner implements ITSContentSigner {
   private final ECPrivateKeyParameters privKey;
   private final ITSCertificate signerCert;
   private final AlgorithmIdentifier digestAlgo;
   private final Digest digest;
   private final byte[] parentData;
   private final ASN1ObjectIdentifier curveID;
   private final byte[] parentDigest;

   public BcITSContentSigner(ECPrivateKeyParameters var1) {
      this(var1, null);
   }

   public BcITSContentSigner(ECPrivateKeyParameters var1, ITSCertificate var2) {
      this.privKey = var1;
      this.curveID = ((ECNamedDomainParameters)var1.getParameters()).getName();
      this.signerCert = var2;
      if (this.curveID.equals(SECObjectIdentifiers.secp256r1)) {
         this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
      } else if (this.curveID.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
         this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
      } else {
         if (!this.curveID.equals(TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
            throw new IllegalArgumentException("unknown key type");
         }

         this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
      }

      try {
         this.digest = BcDefaultDigestProvider.INSTANCE.get(this.digestAlgo);
      } catch (OperatorCreationException var5) {
         throw new IllegalStateException("cannot recognise digest type: " + this.digestAlgo.getAlgorithm());
      }

      if (var2 != null) {
         try {
            this.parentData = var2.getEncoded();
            this.parentDigest = new byte[this.digest.getDigestSize()];
            this.digest.update(this.parentData, 0, this.parentData.length);
            this.digest.doFinal(this.parentDigest, 0);
         } catch (IOException var4) {
            throw new IllegalStateException("signer certificate encoding failed: " + var4.getMessage());
         }
      } else {
         this.parentData = null;
         this.parentDigest = new byte[this.digest.getDigestSize()];
         this.digest.doFinal(this.parentDigest, 0);
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
   public OutputStream getOutputStream() {
      return new DigestOutputStream(this.digest);
   }

   @Override
   public boolean isForSelfSigning() {
      return this.parentData == null;
   }

   @Override
   public ASN1ObjectIdentifier getCurveID() {
      return this.curveID;
   }

   @Override
   public byte[] getSignature() {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);
      DSADigestSigner var2 = new DSADigestSigner(new ECDSASigner(), this.digest);
      var2.init(true, this.privKey);
      var2.update(var1, 0, var1.length);
      var2.update(this.parentDigest, 0, this.parentDigest.length);
      return var2.generateSignature();
   }
}
