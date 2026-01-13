package org.bouncycastle.asn1.x509.qualified;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class SemanticsInformation extends ASN1Object {
   private ASN1ObjectIdentifier semanticsIdentifier;
   private GeneralName[] nameRegistrationAuthorities;

   public static SemanticsInformation getInstance(Object var0) {
      if (var0 instanceof SemanticsInformation) {
         return (SemanticsInformation)var0;
      } else {
         return var0 != null ? new SemanticsInformation(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private SemanticsInformation(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      if (var1.size() < 1) {
         throw new IllegalArgumentException("no objects in SemanticsInformation");
      } else {
         Object var3 = var2.nextElement();
         if (var3 instanceof ASN1ObjectIdentifier) {
            this.semanticsIdentifier = ASN1ObjectIdentifier.getInstance(var3);
            if (var2.hasMoreElements()) {
               var3 = var2.nextElement();
            } else {
               var3 = null;
            }
         }

         if (var3 != null) {
            ASN1Sequence var4 = ASN1Sequence.getInstance(var3);
            this.nameRegistrationAuthorities = new GeneralName[var4.size()];

            for (int var5 = 0; var5 < var4.size(); var5++) {
               this.nameRegistrationAuthorities[var5] = GeneralName.getInstance(var4.getObjectAt(var5));
            }
         }
      }
   }

   public SemanticsInformation(ASN1ObjectIdentifier var1, GeneralName[] var2) {
      this.semanticsIdentifier = var1;
      this.nameRegistrationAuthorities = cloneNames(var2);
   }

   public SemanticsInformation(ASN1ObjectIdentifier var1) {
      this.semanticsIdentifier = var1;
      this.nameRegistrationAuthorities = null;
   }

   public SemanticsInformation(GeneralName[] var1) {
      this.semanticsIdentifier = null;
      this.nameRegistrationAuthorities = cloneNames(var1);
   }

   public ASN1ObjectIdentifier getSemanticsIdentifier() {
      return this.semanticsIdentifier;
   }

   public GeneralName[] getNameRegistrationAuthorities() {
      return cloneNames(this.nameRegistrationAuthorities);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.semanticsIdentifier != null) {
         var1.add(this.semanticsIdentifier);
      }

      if (this.nameRegistrationAuthorities != null) {
         var1.add(new DERSequence(this.nameRegistrationAuthorities));
      }

      return new DERSequence(var1);
   }

   private static GeneralName[] cloneNames(GeneralName[] var0) {
      if (var0 != null) {
         GeneralName[] var1 = new GeneralName[var0.length];
         System.arraycopy(var0, 0, var1, 0, var0.length);
         return var1;
      } else {
         return null;
      }
   }
}
