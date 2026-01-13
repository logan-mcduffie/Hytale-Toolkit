package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CMSSignedDataGenerator extends CMSSignedGenerator {
   private boolean isDefiniteLength = false;

   public CMSSignedDataGenerator() {
   }

   public CMSSignedDataGenerator(DigestAlgorithmIdentifierFinder var1) {
      super(var1);
   }

   public void setDefiniteLengthEncoding(boolean var1) {
      this.isDefiniteLength = var1;
   }

   public CMSSignedData generate(CMSTypedData var1) throws CMSException {
      return this.generate(var1, false);
   }

   public CMSSignedData generate(CMSTypedData var1, boolean var2) throws CMSException {
      LinkedHashSet var3 = new LinkedHashSet();
      ASN1EncodableVector var4 = new ASN1EncodableVector();
      this.digests.clear();

      for (SignerInformation var6 : this._signers) {
         CMSUtils.addDigestAlgs(var3, var6, this.digestAlgIdFinder);
         var4.add(var6.toASN1Structure());
      }

      ASN1ObjectIdentifier var12 = var1.getContentType();
      Object var13 = null;
      if (var1.getContent() != null) {
         if (var2) {
            ByteArrayOutputStream var7 = new ByteArrayOutputStream();
            this.writeContentViaSignerGens(var1, var7);
            if (this.isDefiniteLength) {
               var13 = new DEROctetString(var7.toByteArray());
            } else {
               var13 = new BEROctetString(var7.toByteArray());
            }
         } else {
            this.writeContentViaSignerGens(var1, null);
         }
      }

      for (SignerInfoGenerator var8 : this.signerGens) {
         SignerInfo var9 = this.generateSignerInfo(var8, var12);
         var3.add(var9.getDigestAlgorithm());
         var4.add(var9);
      }

      ASN1Set var15 = createSetFromList(this.certs, this.isDefiniteLength);
      ASN1Set var16 = createSetFromList(this.crls, this.isDefiniteLength);
      ContentInfo var17 = new ContentInfo(var12, (ASN1Encodable)var13);
      SignedData var10 = new SignedData(CMSUtils.convertToDlSet(var3), var17, var15, var16, new DERSet(var4));
      ContentInfo var11 = new ContentInfo(CMSObjectIdentifiers.signedData, var10);
      return new CMSSignedData(var1, var11);
   }

   public SignerInformationStore generateCounterSigners(SignerInformation var1) throws CMSException {
      this.digests.clear();
      CMSProcessableByteArray var2 = new CMSProcessableByteArray(null, var1.getSignature());
      ArrayList var3 = new ArrayList();

      for (SignerInformation var5 : this._signers) {
         SignerInfo var6 = var5.toASN1Structure();
         var3.add(new SignerInformation(var6, null, var2, null));
      }

      this.writeContentViaSignerGens(var2, null);

      for (SignerInfoGenerator var8 : this.signerGens) {
         SignerInfo var9 = this.generateSignerInfo(var8, null);
         var3.add(new SignerInformation(var9, null, var2, null));
      }

      return new SignerInformationStore(var3);
   }

   private SignerInfo generateSignerInfo(SignerInfoGenerator var1, ASN1ObjectIdentifier var2) throws CMSException {
      SignerInfo var3 = var1.generate(var2);
      byte[] var4 = var1.getCalculatedDigest();
      if (var4 != null) {
         this.digests.put(var3.getDigestAlgorithm().getAlgorithm().getId(), var4);
      }

      return var3;
   }

   private void writeContentViaSignerGens(CMSTypedData var1, OutputStream var2) throws CMSException {
      OutputStream var3 = CMSUtils.attachSignersToOutputStream(this.signerGens, var2);
      var3 = CMSUtils.getSafeOutputStream(var3);

      try {
         var1.write(var3);
         var3.close();
      } catch (IOException var5) {
         throw new CMSException("data processing exception: " + var5.getMessage(), var5);
      }
   }

   private static ASN1Set createSetFromList(List var0, boolean var1) {
      return var0.size() < 1 ? null : (var1 ? CMSUtils.createDlSetFromList(var0) : CMSUtils.createBerSetFromList(var0));
   }
}
