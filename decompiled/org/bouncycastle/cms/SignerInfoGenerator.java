package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.TeeOutputStream;

public class SignerInfoGenerator {
   private final SignerIdentifier signerIdentifier;
   private final CMSAttributeTableGenerator sAttrGen;
   private final CMSAttributeTableGenerator unsAttrGen;
   private final ContentSigner signer;
   private final DigestCalculator digester;
   private final AlgorithmIdentifier digestAlgorithm;
   private final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
   private byte[] calculatedDigest = null;
   private X509CertificateHolder certHolder;

   SignerInfoGenerator(SignerIdentifier var1, ContentSigner var2, AlgorithmIdentifier var3, CMSSignatureEncryptionAlgorithmFinder var4) {
      this.signerIdentifier = var1;
      this.signer = var2;
      this.digestAlgorithm = var3;
      this.digester = null;
      this.sAttrGen = null;
      this.unsAttrGen = null;
      this.sigEncAlgFinder = var4;
   }

   SignerInfoGenerator(
      SignerIdentifier var1,
      ContentSigner var2,
      DigestCalculator var3,
      CMSSignatureEncryptionAlgorithmFinder var4,
      CMSAttributeTableGenerator var5,
      CMSAttributeTableGenerator var6
   ) {
      this.signerIdentifier = var1;
      this.signer = var2;
      this.digestAlgorithm = var3.getAlgorithmIdentifier();
      this.digester = var3;
      this.sAttrGen = var5;
      this.unsAttrGen = var6;
      this.sigEncAlgFinder = var4;
   }

   public SignerInfoGenerator(SignerInfoGenerator var1, CMSAttributeTableGenerator var2, CMSAttributeTableGenerator var3) {
      this.signerIdentifier = var1.signerIdentifier;
      this.signer = var1.signer;
      this.digestAlgorithm = var1.digestAlgorithm;
      this.digester = var1.digester;
      this.sigEncAlgFinder = var1.sigEncAlgFinder;
      this.certHolder = var1.certHolder;
      this.sAttrGen = var2;
      this.unsAttrGen = var3;
   }

   public SignerIdentifier getSID() {
      return this.signerIdentifier;
   }

   public int getGeneratedVersion() {
      return this.signerIdentifier.isTagged() ? 3 : 1;
   }

   public boolean hasAssociatedCertificate() {
      return this.certHolder != null;
   }

   public X509CertificateHolder getAssociatedCertificate() {
      return this.certHolder;
   }

   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.digestAlgorithm;
   }

   public OutputStream getCalculatingOutputStream() {
      if (this.digester != null) {
         return (OutputStream)(this.sAttrGen == null
            ? new TeeOutputStream(this.digester.getOutputStream(), this.signer.getOutputStream())
            : this.digester.getOutputStream());
      } else {
         return this.signer.getOutputStream();
      }
   }

   public SignerInfo generate(ASN1ObjectIdentifier var1) throws CMSException {
      try {
         ASN1Set var2 = null;
         AlgorithmIdentifier var3 = this.sigEncAlgFinder.findEncryptionAlgorithm(this.signer.getAlgorithmIdentifier());
         Object var4 = null;
         if (this.sAttrGen != null) {
            var4 = this.digester.getAlgorithmIdentifier();
            this.calculatedDigest = this.digester.getDigest();
            Map var5 = this.getBaseParameters(var1, this.digester.getAlgorithmIdentifier(), var3, this.calculatedDigest);
            AttributeTable var6 = this.sAttrGen.getAttributes(Collections.unmodifiableMap(var5));
            var2 = this.getAttributeSet(var6);
            OutputStream var7 = this.signer.getOutputStream();
            var7.write(var2.getEncoded("DER"));
            var7.close();
         } else {
            var4 = this.digestAlgorithm;
            if (this.digester != null) {
               this.calculatedDigest = this.digester.getDigest();
            } else {
               this.calculatedDigest = null;
            }
         }

         byte[] var11 = this.signer.getSignature();
         ASN1Set var12 = null;
         if (this.unsAttrGen != null) {
            Map var13 = this.getBaseParameters(var1, (AlgorithmIdentifier)var4, var3, this.calculatedDigest);
            var13.put("encryptedDigest", Arrays.clone(var11));
            AttributeTable var8 = this.unsAttrGen.getAttributes(Collections.unmodifiableMap(var13));
            var12 = this.getAttributeSet(var8);
         }

         if (this.sAttrGen == null && EdECObjectIdentifiers.id_Ed448.equals(var3.getAlgorithm())) {
            var4 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256);
         }

         return new SignerInfo(this.signerIdentifier, (AlgorithmIdentifier)var4, var2, var3, new DEROctetString(var11), var12);
      } catch (IOException var9) {
         throw new CMSException("encoding error.", var9);
      }
   }

   void setAssociatedCertificate(X509CertificateHolder var1) {
      this.certHolder = var1;
   }

   private ASN1Set getAttributeSet(AttributeTable var1) {
      return var1 != null ? new DERSet(var1.toASN1EncodableVector()) : null;
   }

   private Map getBaseParameters(ASN1ObjectIdentifier var1, AlgorithmIdentifier var2, AlgorithmIdentifier var3, byte[] var4) {
      HashMap var5 = new HashMap();
      if (var1 != null) {
         var5.put("contentType", var1);
      }

      var5.put("digestAlgID", var2);
      var5.put("signatureAlgID", var3);
      var5.put("digest", Arrays.clone(var4));
      return var5;
   }

   public byte[] getCalculatedDigest() {
      return this.calculatedDigest != null ? Arrays.clone(this.calculatedDigest) : null;
   }

   public CMSAttributeTableGenerator getSignedAttributeTableGenerator() {
      return this.sAttrGen;
   }

   public CMSAttributeTableGenerator getUnsignedAttributeTableGenerator() {
      return this.unsAttrGen;
   }
}
