package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Strings;

public class GeneralNames extends ASN1Object {
   private final GeneralName[] names;

   private static GeneralName[] copy(GeneralName[] var0) {
      GeneralName[] var1 = new GeneralName[var0.length];
      System.arraycopy(var0, 0, var1, 0, var0.length);
      return var1;
   }

   public static GeneralNames getInstance(Object var0) {
      if (var0 instanceof GeneralNames) {
         return (GeneralNames)var0;
      } else {
         return var0 != null ? new GeneralNames(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static GeneralNames getInstance(ASN1TaggedObject var0, boolean var1) {
      return new GeneralNames(ASN1Sequence.getInstance(var0, var1));
   }

   public static GeneralNames fromExtensions(Extensions var0, ASN1ObjectIdentifier var1) {
      return getInstance(Extensions.getExtensionParsedValue(var0, var1));
   }

   public GeneralNames(GeneralName var1) {
      this.names = new GeneralName[]{var1};
   }

   public GeneralNames(GeneralName[] var1) {
      this.names = copy(var1);
   }

   private GeneralNames(ASN1Sequence var1) {
      this.names = new GeneralName[var1.size()];

      for (int var2 = 0; var2 != var1.size(); var2++) {
         this.names[var2] = GeneralName.getInstance(var1.getObjectAt(var2));
      }
   }

   public GeneralName[] getNames() {
      return copy(this.names);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.names);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("GeneralNames:");
      var1.append(var2);

      for (int var3 = 0; var3 != this.names.length; var3++) {
         var1.append("    ");
         var1.append(this.names[var3]);
         var1.append(var2);
      }

      return var1.toString();
   }
}
