package org.bouncycastle.asn1.x509.sigi;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.DirectoryString;

public class NameOrPseudonym extends ASN1Object implements ASN1Choice {
   private DirectoryString pseudonym;
   private DirectoryString surname;
   private ASN1Sequence givenName;

   public static NameOrPseudonym getInstance(Object var0) {
      if (var0 == null || var0 instanceof NameOrPseudonym) {
         return (NameOrPseudonym)var0;
      } else if (var0 instanceof ASN1String) {
         return new NameOrPseudonym(DirectoryString.getInstance(var0));
      } else if (var0 instanceof ASN1Sequence) {
         return new NameOrPseudonym((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   public NameOrPseudonym(DirectoryString var1) {
      this.pseudonym = var1;
   }

   private NameOrPseudonym(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else if (!(var1.getObjectAt(0) instanceof ASN1String)) {
         throw new IllegalArgumentException("Bad object encountered: " + var1.getObjectAt(0).getClass());
      } else {
         this.surname = DirectoryString.getInstance(var1.getObjectAt(0));
         this.givenName = ASN1Sequence.getInstance(var1.getObjectAt(1));
      }
   }

   public NameOrPseudonym(String var1) {
      this(new DirectoryString(var1));
   }

   public NameOrPseudonym(DirectoryString var1, ASN1Sequence var2) {
      this.surname = var1;
      this.givenName = var2;
   }

   public DirectoryString getPseudonym() {
      return this.pseudonym;
   }

   public DirectoryString getSurname() {
      return this.surname;
   }

   public DirectoryString[] getGivenName() {
      DirectoryString[] var1 = new DirectoryString[this.givenName.size()];
      int var2 = 0;
      Enumeration var3 = this.givenName.getObjects();

      while (var3.hasMoreElements()) {
         var1[var2++] = DirectoryString.getInstance(var3.nextElement());
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(this.pseudonym != null ? this.pseudonym.toASN1Primitive() : new DERSequence(this.surname, this.givenName));
   }
}
