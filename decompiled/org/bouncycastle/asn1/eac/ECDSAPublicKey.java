package org.bouncycastle.asn1.eac;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class ECDSAPublicKey extends PublicKeyDataObject {
   private ASN1ObjectIdentifier usage;
   private BigInteger primeModulusP;
   private BigInteger firstCoefA;
   private BigInteger secondCoefB;
   private byte[] basePointG;
   private BigInteger orderOfBasePointR;
   private byte[] publicPointY;
   private BigInteger cofactorF;
   private int options;
   private static final int P = 1;
   private static final int A = 2;
   private static final int B = 4;
   private static final int G = 8;
   private static final int R = 16;
   private static final int Y = 32;
   private static final int F = 64;

   ECDSAPublicKey(ASN1Sequence var1) throws IllegalArgumentException {
      Enumeration var2 = var1.getObjects();
      this.usage = ASN1ObjectIdentifier.getInstance(var2.nextElement());
      this.options = 0;

      while (var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (!(var3 instanceof ASN1TaggedObject)) {
            throw new IllegalArgumentException("Unknown Object Identifier!");
         }

         ASN1TaggedObject var4 = (ASN1TaggedObject)var3;
         switch (var4.getTagNo()) {
            case 1:
               this.setPrimeModulusP(UnsignedInteger.getInstance(var4).getValue());
               break;
            case 2:
               this.setFirstCoefA(UnsignedInteger.getInstance(var4).getValue());
               break;
            case 3:
               this.setSecondCoefB(UnsignedInteger.getInstance(var4).getValue());
               break;
            case 4:
               this.setBasePointG(ASN1OctetString.getInstance(var4, false));
               break;
            case 5:
               this.setOrderOfBasePointR(UnsignedInteger.getInstance(var4).getValue());
               break;
            case 6:
               this.setPublicPointY(ASN1OctetString.getInstance(var4, false));
               break;
            case 7:
               this.setCofactorF(UnsignedInteger.getInstance(var4).getValue());
               break;
            default:
               this.options = 0;
               throw new IllegalArgumentException("Unknown Object Identifier!");
         }
      }

      if (this.options != 32 && this.options != 127) {
         throw new IllegalArgumentException("All options must be either present or absent!");
      }
   }

   public ECDSAPublicKey(ASN1ObjectIdentifier var1, byte[] var2) throws IllegalArgumentException {
      this.usage = var1;
      this.setPublicPointY(new DEROctetString(var2));
   }

   public ECDSAPublicKey(ASN1ObjectIdentifier var1, BigInteger var2, BigInteger var3, BigInteger var4, byte[] var5, BigInteger var6, byte[] var7, int var8) {
      this.usage = var1;
      this.setPrimeModulusP(var2);
      this.setFirstCoefA(var3);
      this.setSecondCoefB(var4);
      this.setBasePointG(new DEROctetString(var5));
      this.setOrderOfBasePointR(var6);
      this.setPublicPointY(new DEROctetString(var7));
      this.setCofactorF(BigInteger.valueOf(var8));
   }

   @Override
   public ASN1ObjectIdentifier getUsage() {
      return this.usage;
   }

   public byte[] getBasePointG() {
      return (this.options & 8) != 0 ? Arrays.clone(this.basePointG) : null;
   }

   private void setBasePointG(ASN1OctetString var1) throws IllegalArgumentException {
      if ((this.options & 8) == 0) {
         this.options |= 8;
         this.basePointG = var1.getOctets();
      } else {
         throw new IllegalArgumentException("Base Point G already set");
      }
   }

   public BigInteger getCofactorF() {
      return (this.options & 64) != 0 ? this.cofactorF : null;
   }

   private void setCofactorF(BigInteger var1) throws IllegalArgumentException {
      if ((this.options & 64) == 0) {
         this.options |= 64;
         this.cofactorF = var1;
      } else {
         throw new IllegalArgumentException("Cofactor F already set");
      }
   }

   public BigInteger getFirstCoefA() {
      return (this.options & 2) != 0 ? this.firstCoefA : null;
   }

   private void setFirstCoefA(BigInteger var1) throws IllegalArgumentException {
      if ((this.options & 2) == 0) {
         this.options |= 2;
         this.firstCoefA = var1;
      } else {
         throw new IllegalArgumentException("First Coef A already set");
      }
   }

   public BigInteger getOrderOfBasePointR() {
      return (this.options & 16) != 0 ? this.orderOfBasePointR : null;
   }

   private void setOrderOfBasePointR(BigInteger var1) throws IllegalArgumentException {
      if ((this.options & 16) == 0) {
         this.options |= 16;
         this.orderOfBasePointR = var1;
      } else {
         throw new IllegalArgumentException("Order of base point R already set");
      }
   }

   public BigInteger getPrimeModulusP() {
      return (this.options & 1) != 0 ? this.primeModulusP : null;
   }

   private void setPrimeModulusP(BigInteger var1) {
      if ((this.options & 1) == 0) {
         this.options |= 1;
         this.primeModulusP = var1;
      } else {
         throw new IllegalArgumentException("Prime Modulus P already set");
      }
   }

   public byte[] getPublicPointY() {
      return (this.options & 32) != 0 ? Arrays.clone(this.publicPointY) : null;
   }

   private void setPublicPointY(ASN1OctetString var1) throws IllegalArgumentException {
      if ((this.options & 32) == 0) {
         this.options |= 32;
         this.publicPointY = var1.getOctets();
      } else {
         throw new IllegalArgumentException("Public Point Y already set");
      }
   }

   public BigInteger getSecondCoefB() {
      return (this.options & 4) != 0 ? this.secondCoefB : null;
   }

   private void setSecondCoefB(BigInteger var1) throws IllegalArgumentException {
      if ((this.options & 4) == 0) {
         this.options |= 4;
         this.secondCoefB = var1;
      } else {
         throw new IllegalArgumentException("Second Coef B already set");
      }
   }

   public boolean hasParameters() {
      return this.primeModulusP != null;
   }

   public ASN1EncodableVector getASN1EncodableVector(ASN1ObjectIdentifier var1, boolean var2) {
      ASN1EncodableVector var3 = new ASN1EncodableVector(8);
      var3.add(var1);
      if (!var2) {
         var3.add(new UnsignedInteger(1, this.getPrimeModulusP()));
         var3.add(new UnsignedInteger(2, this.getFirstCoefA()));
         var3.add(new UnsignedInteger(3, this.getSecondCoefB()));
         var3.add(new DERTaggedObject(false, 4, new DEROctetString(this.getBasePointG())));
         var3.add(new UnsignedInteger(5, this.getOrderOfBasePointR()));
      }

      var3.add(new DERTaggedObject(false, 6, new DEROctetString(this.getPublicPointY())));
      if (!var2) {
         var3.add(new UnsignedInteger(7, this.getCofactorF()));
      }

      return var3;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.getASN1EncodableVector(this.usage, !this.hasParameters()));
   }
}
