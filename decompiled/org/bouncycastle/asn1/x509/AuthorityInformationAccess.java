package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class AuthorityInformationAccess extends ASN1Object {
   private AccessDescription[] descriptions;

   private static AccessDescription[] copy(AccessDescription[] var0) {
      AccessDescription[] var1 = new AccessDescription[var0.length];
      System.arraycopy(var0, 0, var1, 0, var0.length);
      return var1;
   }

   public static AuthorityInformationAccess getInstance(Object var0) {
      if (var0 instanceof AuthorityInformationAccess) {
         return (AuthorityInformationAccess)var0;
      } else {
         return var0 != null ? new AuthorityInformationAccess(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static AuthorityInformationAccess fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.authorityInfoAccess));
   }

   private AuthorityInformationAccess(ASN1Sequence var1) {
      if (var1.size() < 1) {
         throw new IllegalArgumentException("sequence may not be empty");
      } else {
         this.descriptions = new AccessDescription[var1.size()];

         for (int var2 = 0; var2 != var1.size(); var2++) {
            this.descriptions[var2] = AccessDescription.getInstance(var1.getObjectAt(var2));
         }
      }
   }

   public AuthorityInformationAccess(AccessDescription var1) {
      this.descriptions = new AccessDescription[]{var1};
   }

   public AuthorityInformationAccess(AccessDescription[] var1) {
      this.descriptions = copy(var1);
   }

   public AuthorityInformationAccess(ASN1ObjectIdentifier var1, GeneralName var2) {
      this(new AccessDescription(var1, var2));
   }

   public AccessDescription[] getAccessDescriptions() {
      return copy(this.descriptions);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.descriptions);
   }

   @Override
   public String toString() {
      return "AuthorityInformationAccess: Oid(" + this.descriptions[0].getAccessMethod().getId() + ")";
   }
}
