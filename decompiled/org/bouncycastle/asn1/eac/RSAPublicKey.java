package org.bouncycastle.asn1.eac;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class RSAPublicKey extends PublicKeyDataObject {
   private ASN1ObjectIdentifier usage;
   private BigInteger modulus;
   private BigInteger exponent;
   private int valid = 0;
   private static int modulusValid = 1;
   private static int exponentValid = 2;

   RSAPublicKey(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.usage = ASN1ObjectIdentifier.getInstance(var2.nextElement());

      while (var2.hasMoreElements()) {
         UnsignedInteger var3 = UnsignedInteger.getInstance(var2.nextElement());
         switch (var3.getTagNo()) {
            case 1:
               this.setModulus(var3);
               break;
            case 2:
               this.setExponent(var3);
               break;
            default:
               throw new IllegalArgumentException("Unknown DERTaggedObject :" + var3.getTagNo() + "-> not an Iso7816RSAPublicKeyStructure");
         }
      }

      if (this.valid != 3) {
         throw new IllegalArgumentException("missing argument -> not an Iso7816RSAPublicKeyStructure");
      }
   }

   public RSAPublicKey(ASN1ObjectIdentifier var1, BigInteger var2, BigInteger var3) {
      this.usage = var1;
      this.modulus = var2;
      this.exponent = var3;
   }

   @Override
   public ASN1ObjectIdentifier getUsage() {
      return this.usage;
   }

   public BigInteger getModulus() {
      return this.modulus;
   }

   public BigInteger getPublicExponent() {
      return this.exponent;
   }

   private void setModulus(UnsignedInteger var1) {
      if ((this.valid & modulusValid) == 0) {
         this.valid = this.valid | modulusValid;
         this.modulus = var1.getValue();
      } else {
         throw new IllegalArgumentException("Modulus already set");
      }
   }

   private void setExponent(UnsignedInteger var1) {
      if ((this.valid & exponentValid) == 0) {
         this.valid = this.valid | exponentValid;
         this.exponent = var1.getValue();
      } else {
         throw new IllegalArgumentException("Exponent already set");
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.usage);
      var1.add(new UnsignedInteger(1, this.getModulus()));
      var1.add(new UnsignedInteger(2, this.getPublicExponent()));
      return new DERSequence(var1);
   }
}
