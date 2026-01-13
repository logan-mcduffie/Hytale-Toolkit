package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;

public class SMIMEEncryptionKeyPreferenceAttribute extends Attribute {
   public SMIMEEncryptionKeyPreferenceAttribute(IssuerAndSerialNumber var1) {
      super(SMIMEAttributes.encrypKeyPref, new DERSet(new DERTaggedObject(false, 0, var1)));
   }

   public SMIMEEncryptionKeyPreferenceAttribute(RecipientKeyIdentifier var1) {
      super(SMIMEAttributes.encrypKeyPref, new DERSet(new DERTaggedObject(false, 1, var1)));
   }

   public SMIMEEncryptionKeyPreferenceAttribute(ASN1OctetString var1) {
      super(SMIMEAttributes.encrypKeyPref, new DERSet(new DERTaggedObject(false, 2, var1)));
   }
}
