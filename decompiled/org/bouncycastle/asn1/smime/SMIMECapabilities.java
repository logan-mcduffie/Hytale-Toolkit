package org.bouncycastle.asn1.smime;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public class SMIMECapabilities extends ASN1Object {
   public static final ASN1ObjectIdentifier preferSignedData = PKCSObjectIdentifiers.preferSignedData;
   public static final ASN1ObjectIdentifier canNotDecryptAny = PKCSObjectIdentifiers.canNotDecryptAny;
   public static final ASN1ObjectIdentifier sMIMECapabilitesVersions = PKCSObjectIdentifiers.sMIMECapabilitiesVersions;
   public static final ASN1ObjectIdentifier aes256_CBC = NISTObjectIdentifiers.id_aes256_CBC;
   public static final ASN1ObjectIdentifier aes192_CBC = NISTObjectIdentifiers.id_aes192_CBC;
   public static final ASN1ObjectIdentifier aes128_CBC = NISTObjectIdentifiers.id_aes128_CBC;
   public static final ASN1ObjectIdentifier idea_CBC = new ASN1ObjectIdentifier("1.3.6.1.4.1.188.7.1.1.2");
   public static final ASN1ObjectIdentifier cast5_CBC = new ASN1ObjectIdentifier("1.2.840.113533.7.66.10");
   public static final ASN1ObjectIdentifier dES_CBC = new ASN1ObjectIdentifier("1.3.14.3.2.7");
   public static final ASN1ObjectIdentifier dES_EDE3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC;
   public static final ASN1ObjectIdentifier rC2_CBC = PKCSObjectIdentifiers.RC2_CBC;
   private ASN1Sequence capabilities;

   public static SMIMECapabilities getInstance(Object var0) {
      if (var0 == null || var0 instanceof SMIMECapabilities) {
         return (SMIMECapabilities)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new SMIMECapabilities((ASN1Sequence)var0);
      } else if (var0 instanceof Attribute) {
         return new SMIMECapabilities((ASN1Sequence)((Attribute)var0).getAttrValues().getObjectAt(0));
      } else {
         throw new IllegalArgumentException("unknown object in factory: " + var0.getClass().getName());
      }
   }

   public SMIMECapabilities(ASN1Sequence var1) {
      this.capabilities = var1;
   }

   public Vector getCapabilities(ASN1ObjectIdentifier var1) {
      Enumeration var2 = this.capabilities.getObjects();
      Vector var3 = new Vector();
      if (var1 == null) {
         while (var2.hasMoreElements()) {
            SMIMECapability var5 = SMIMECapability.getInstance(var2.nextElement());
            var3.addElement(var5);
         }
      } else {
         while (var2.hasMoreElements()) {
            SMIMECapability var4 = SMIMECapability.getInstance(var2.nextElement());
            if (var1.equals(var4.getCapabilityID())) {
               var3.addElement(var4);
            }
         }
      }

      return var3;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.capabilities;
   }
}
