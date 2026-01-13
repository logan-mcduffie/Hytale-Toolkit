package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.util.Arrays;

public class ExternalValue extends ASN1Object {
   private final GeneralNames location;
   private final AlgorithmIdentifier hashAlg;
   private final byte[] hashValue;

   public ExternalValue(GeneralName var1, AlgorithmIdentifier var2, byte[] var3) {
      this.location = new GeneralNames(var1);
      this.hashAlg = var2;
      this.hashValue = Arrays.clone(var3);
   }

   private ExternalValue(ASN1Sequence var1) {
      if (var1.size() == 3) {
         this.location = GeneralNames.getInstance(var1.getObjectAt(0));
         this.hashAlg = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         if (var1.getObjectAt(2) instanceof ASN1BitString) {
            this.hashValue = ASN1BitString.getInstance(var1.getObjectAt(2)).getOctets();
         } else {
            this.hashValue = ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets();
         }
      } else {
         throw new IllegalArgumentException("unknown sequence");
      }
   }

   public static ExternalValue getInstance(Object var0) {
      if (var0 instanceof ExternalValue) {
         return (ExternalValue)var0;
      } else {
         return var0 != null ? new ExternalValue(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public GeneralName getLocation() {
      return this.location.getNames()[0];
   }

   public GeneralName[] getLocations() {
      return this.location.getNames();
   }

   public AlgorithmIdentifier getHashAlg() {
      return this.hashAlg;
   }

   public byte[] getHashValue() {
      return Arrays.clone(this.hashValue);
   }

   /** @deprecated */
   public ASN1BitString getHashVal() {
      return new DERBitString(this.hashValue);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(this.location);
      var1.add(this.hashAlg);
      var1.add(new DEROctetString(this.hashValue));
      return new DERSequence(var1);
   }
}
