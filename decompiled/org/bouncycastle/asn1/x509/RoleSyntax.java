package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class RoleSyntax extends ASN1Object {
   private GeneralNames roleAuthority;
   private GeneralName roleName;

   public static RoleSyntax getInstance(Object var0) {
      if (var0 instanceof RoleSyntax) {
         return (RoleSyntax)var0;
      } else {
         return var0 != null ? new RoleSyntax(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public RoleSyntax(GeneralNames var1, GeneralName var2) {
      if (var2 != null && var2.getTagNo() == 6 && !((ASN1String)var2.getName()).getString().equals("")) {
         this.roleAuthority = var1;
         this.roleName = var2;
      } else {
         throw new IllegalArgumentException("the role name MUST be non empty and MUST use the URI option of GeneralName");
      }
   }

   public RoleSyntax(GeneralName var1) {
      this(null, var1);
   }

   public RoleSyntax(String var1) {
      this(new GeneralName(6, var1 == null ? "" : var1));
   }

   private RoleSyntax(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 2) {
         for (int var2 = 0; var2 != var1.size(); var2++) {
            ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var1.getObjectAt(var2));
            switch (var3.getTagNo()) {
               case 0:
                  this.roleAuthority = GeneralNames.getInstance(var3, false);
                  break;
               case 1:
                  this.roleName = GeneralName.getInstance(var3, true);
                  break;
               default:
                  throw new IllegalArgumentException("Unknown tag in RoleSyntax");
            }
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public GeneralNames getRoleAuthority() {
      return this.roleAuthority;
   }

   public GeneralName getRoleName() {
      return this.roleName;
   }

   public String getRoleNameAsString() {
      ASN1String var1 = (ASN1String)this.roleName.getName();
      return var1.getString();
   }

   public String[] getRoleAuthorityAsString() {
      if (this.roleAuthority == null) {
         return new String[0];
      } else {
         GeneralName[] var1 = this.roleAuthority.getNames();
         String[] var2 = new String[var1.length];

         for (int var3 = 0; var3 < var1.length; var3++) {
            ASN1Encodable var4 = var1[var3].getName();
            if (var4 instanceof ASN1String) {
               var2[var3] = ((ASN1String)var4).getString();
            } else {
               var2[var3] = var4.toString();
            }
         }

         return var2;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.roleAuthority != null) {
         var1.add(new DERTaggedObject(false, 0, this.roleAuthority));
      }

      var1.add(new DERTaggedObject(true, 1, this.roleName));
      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("Name: " + this.getRoleNameAsString() + " - Auth: ");
      if (this.roleAuthority != null && this.roleAuthority.getNames().length != 0) {
         String[] var2 = this.getRoleAuthorityAsString();
         var1.append('[').append(var2[0]);

         for (int var3 = 1; var3 < var2.length; var3++) {
            var1.append(", ").append(var2[var3]);
         }

         var1.append(']');
      } else {
         var1.append("N/A");
      }

      return var1.toString();
   }
}
