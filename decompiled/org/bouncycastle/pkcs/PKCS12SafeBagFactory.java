package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.cms.CMSEncryptedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.InputDecryptorProvider;

public class PKCS12SafeBagFactory {
   private ASN1Sequence safeBagSeq;

   public PKCS12SafeBagFactory(ContentInfo var1) {
      if (var1.getContentType().equals(PKCSObjectIdentifiers.encryptedData)) {
         throw new IllegalArgumentException("encryptedData requires constructor with decryptor.");
      } else {
         this.safeBagSeq = ASN1Sequence.getInstance(ASN1OctetString.getInstance(var1.getContent()).getOctets());
      }
   }

   public PKCS12SafeBagFactory(ContentInfo var1, InputDecryptorProvider var2) throws PKCSException {
      if (var1.getContentType().equals(PKCSObjectIdentifiers.encryptedData)) {
         CMSEncryptedData var3 = new CMSEncryptedData(org.bouncycastle.asn1.cms.ContentInfo.getInstance(var1));

         try {
            this.safeBagSeq = ASN1Sequence.getInstance(var3.getContent(var2));
         } catch (CMSException var5) {
            throw new PKCSException("unable to extract data: " + var5.getMessage(), var5);
         }
      } else {
         throw new IllegalArgumentException("encryptedData requires constructor with decryptor.");
      }
   }

   public PKCS12SafeBag[] getSafeBags() {
      PKCS12SafeBag[] var1 = new PKCS12SafeBag[this.safeBagSeq.size()];

      for (int var2 = 0; var2 != this.safeBagSeq.size(); var2++) {
         var1[var2] = new PKCS12SafeBag(SafeBag.getInstance(this.safeBagSeq.getObjectAt(var2)));
      }

      return var1;
   }
}
