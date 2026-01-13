package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cms.OtherRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public abstract class KEMRecipientInfoGenerator implements RecipientInfoGenerator {
   protected final KEMKeyWrapper wrapper;
   private IssuerAndSerialNumber issuerAndSerial;
   private byte[] subjectKeyIdentifier;

   protected KEMRecipientInfoGenerator(IssuerAndSerialNumber var1, KEMKeyWrapper var2) {
      this.issuerAndSerial = var1;
      this.wrapper = var2;
   }

   protected KEMRecipientInfoGenerator(byte[] var1, KEMKeyWrapper var2) {
      this.subjectKeyIdentifier = var1;
      this.wrapper = var2;
   }

   @Override
   public final RecipientInfo generate(GenericKey var1) throws CMSException {
      byte[] var2;
      try {
         var2 = this.wrapper.generateWrappedKey(var1);
      } catch (OperatorException var4) {
         throw new CMSException("exception wrapping content key: " + var4.getMessage(), var4);
      }

      RecipientIdentifier var3;
      if (this.issuerAndSerial != null) {
         var3 = new RecipientIdentifier(this.issuerAndSerial);
      } else {
         var3 = new RecipientIdentifier((ASN1OctetString)(new DEROctetString(this.subjectKeyIdentifier)));
      }

      return new RecipientInfo(
         new OtherRecipientInfo(
            CMSObjectIdentifiers.id_ori_kem,
            new KEMRecipientInfo(
               var3,
               this.wrapper.getAlgorithmIdentifier(),
               new DEROctetString(this.wrapper.getEncapsulation()),
               this.wrapper.getKdfAlgorithmIdentifier(),
               new ASN1Integer(this.wrapper.getKekLength()),
               null,
               this.wrapper.getWrapAlgorithmIdentifier(),
               new DEROctetString(var2)
            )
         )
      );
   }
}
