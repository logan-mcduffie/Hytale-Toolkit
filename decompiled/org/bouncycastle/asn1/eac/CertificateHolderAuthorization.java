package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Integers;

public class CertificateHolderAuthorization extends ASN1Object {
   public static final ASN1ObjectIdentifier id_role_EAC = EACObjectIdentifiers.bsi_de.branch("3.1.2.1");
   public static final int CVCA = 192;
   public static final int DV_DOMESTIC = 128;
   public static final int DV_FOREIGN = 64;
   public static final int IS = 0;
   public static final int RADG4 = 2;
   public static final int RADG3 = 1;
   static Map RightsDecodeMap = new HashMap();
   static BidirectionalMap AuthorizationRole = new BidirectionalMap();
   private ASN1ObjectIdentifier oid;
   private byte accessRights;

   public static String getRoleDescription(int var0) {
      return (String)AuthorizationRole.get(Integers.valueOf(var0));
   }

   public static int getFlag(String var0) {
      Integer var1 = (Integer)AuthorizationRole.getReverse(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown value " + var0);
      } else {
         return var1;
      }
   }

   private void setPrivateData(ASN1Sequence var1) {
      ASN1Primitive var2 = (ASN1Primitive)var1.getObjectAt(0);
      if (var2 instanceof ASN1ObjectIdentifier) {
         this.oid = (ASN1ObjectIdentifier)var2;
         var2 = (ASN1Primitive)var1.getObjectAt(1);
         if (var2 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2, 64, 19);
            this.accessRights = ASN1OctetString.getInstance(var3.getBaseUniversal(false, 4)).getOctets()[0];
         } else {
            throw new IllegalArgumentException("No access rights in CerticateHolderAuthorization");
         }
      } else {
         throw new IllegalArgumentException("no Oid in CerticateHolderAuthorization");
      }
   }

   public CertificateHolderAuthorization(ASN1ObjectIdentifier var1, int var2) throws IOException {
      this.setOid(var1);
      this.setAccessRights((byte)var2);
   }

   public CertificateHolderAuthorization(ASN1TaggedObject var1) throws IOException {
      if (var1.hasTag(64, 76)) {
         this.setPrivateData(ASN1Sequence.getInstance(var1.getBaseUniversal(false, 16)));
      } else {
         throw new IllegalArgumentException("Unrecognized object in CerticateHolderAuthorization");
      }
   }

   public int getAccessRights() {
      return this.accessRights & 0xFF;
   }

   private void setAccessRights(byte var1) {
      this.accessRights = var1;
   }

   public ASN1ObjectIdentifier getOid() {
      return this.oid;
   }

   private void setOid(ASN1ObjectIdentifier var1) {
      this.oid = var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      DERSequence var1 = new DERSequence(this.oid, EACTagged.create(19, new byte[]{this.accessRights}));
      return EACTagged.create(76, var1);
   }

   static {
      RightsDecodeMap.put(Integers.valueOf(2), "RADG4");
      RightsDecodeMap.put(Integers.valueOf(1), "RADG3");
      AuthorizationRole.put(Integers.valueOf(192), "CVCA");
      AuthorizationRole.put(Integers.valueOf(128), "DV_DOMESTIC");
      AuthorizationRole.put(Integers.valueOf(64), "DV_FOREIGN");
      AuthorizationRole.put(Integers.valueOf(0), "IS");
   }
}
