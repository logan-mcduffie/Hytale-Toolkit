package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.DirectoryString;

public class SignerLocation extends ASN1Object {
   private DirectoryString countryName;
   private DirectoryString localityName;
   private ASN1Sequence postalAddress;

   private SignerLocation(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement(), 128);
         switch (var3.getTagNo()) {
            case 0:
               this.countryName = DirectoryString.getInstance(var3, true);
               break;
            case 1:
               this.localityName = DirectoryString.getInstance(var3, true);
               break;
            case 2:
               if (var3.isExplicit()) {
                  this.postalAddress = ASN1Sequence.getInstance(var3, true);
               } else {
                  this.postalAddress = ASN1Sequence.getInstance(var3, false);
               }

               if (this.postalAddress != null && this.postalAddress.size() > 6) {
                  throw new IllegalArgumentException("postal address must contain less than 6 strings");
               }
               break;
            default:
               throw new IllegalArgumentException("illegal tag");
         }
      }
   }

   private SignerLocation(DirectoryString var1, DirectoryString var2, ASN1Sequence var3) {
      if (var3 != null && var3.size() > 6) {
         throw new IllegalArgumentException("postal address must contain less than 6 strings");
      } else {
         this.countryName = var1;
         this.localityName = var2;
         this.postalAddress = var3;
      }
   }

   public SignerLocation(DirectoryString var1, DirectoryString var2, DirectoryString[] var3) {
      this(var1, var2, new DERSequence(var3));
   }

   public SignerLocation(ASN1UTF8String var1, ASN1UTF8String var2, ASN1Sequence var3) {
      this(DirectoryString.getInstance(var1), DirectoryString.getInstance(var2), var3);
   }

   public static SignerLocation getInstance(Object var0) {
      return var0 != null && !(var0 instanceof SignerLocation) ? new SignerLocation(ASN1Sequence.getInstance(var0)) : (SignerLocation)var0;
   }

   public DirectoryString getCountry() {
      return this.countryName;
   }

   public DirectoryString getLocality() {
      return this.localityName;
   }

   public DirectoryString[] getPostal() {
      if (this.postalAddress == null) {
         return null;
      } else {
         DirectoryString[] var1 = new DirectoryString[this.postalAddress.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = DirectoryString.getInstance(this.postalAddress.getObjectAt(var2));
         }

         return var1;
      }
   }

   /** @deprecated */
   public DERUTF8String getCountryName() {
      return this.countryName == null ? null : new DERUTF8String(this.getCountry().getString());
   }

   /** @deprecated */
   public DERUTF8String getLocalityName() {
      return this.localityName == null ? null : new DERUTF8String(this.getLocality().getString());
   }

   public ASN1Sequence getPostalAddress() {
      return this.postalAddress;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.countryName != null) {
         var1.add(new DERTaggedObject(true, 0, this.countryName));
      }

      if (this.localityName != null) {
         var1.add(new DERTaggedObject(true, 1, this.localityName));
      }

      if (this.postalAddress != null) {
         var1.add(new DERTaggedObject(true, 2, this.postalAddress));
      }

      return new DERSequence(var1);
   }
}
