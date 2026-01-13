package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

public class Challenge extends ASN1Object {
   private final AlgorithmIdentifier owf;
   private final ASN1OctetString witness;
   private final ASN1OctetString challenge;
   private final EnvelopedData encryptedRand;

   private Challenge(ASN1Sequence var1) {
      int var2 = 0;
      if (var1.getObjectAt(0).toASN1Primitive() instanceof ASN1Sequence) {
         this.owf = AlgorithmIdentifier.getInstance(var1.getObjectAt(var2++));
      } else {
         this.owf = null;
      }

      this.witness = ASN1OctetString.getInstance(var1.getObjectAt(var2++));
      this.challenge = ASN1OctetString.getInstance(var1.getObjectAt(var2++));
      if (var1.size() > var2) {
         if (this.challenge.getOctets().length != 0) {
            throw new IllegalArgumentException("ambigous challenge");
         }

         this.encryptedRand = EnvelopedData.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(var2)), true);
      } else {
         this.encryptedRand = null;
      }
   }

   public Challenge(byte[] var1, byte[] var2) {
      this(null, var1, var2);
   }

   public Challenge(byte[] var1, EnvelopedData var2) {
      this(null, var1, var2);
   }

   public Challenge(AlgorithmIdentifier var1, byte[] var2, byte[] var3) {
      this.owf = var1;
      this.witness = new DEROctetString(var2);
      this.challenge = new DEROctetString(var3);
      this.encryptedRand = null;
   }

   public Challenge(AlgorithmIdentifier var1, byte[] var2, EnvelopedData var3) {
      this.owf = var1;
      this.witness = new DEROctetString(var2);
      this.challenge = new DEROctetString(new byte[0]);
      this.encryptedRand = var3;
   }

   public static Challenge getInstance(Object var0) {
      if (var0 instanceof Challenge) {
         return (Challenge)var0;
      } else {
         return var0 != null ? new Challenge(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getOwf() {
      return this.owf;
   }

   public byte[] getWitness() {
      return this.witness.getOctets();
   }

   public boolean isEncryptedRand() {
      return this.encryptedRand != null;
   }

   public byte[] getChallenge() {
      return this.challenge.getOctets();
   }

   public EnvelopedData getEncryptedRand() {
      return this.encryptedRand;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.addOptional(this.owf);
      var1.add(this.witness);
      var1.add(this.challenge);
      if (this.encryptedRand != null) {
         var1.add(new DERTaggedObject(0, this.encryptedRand));
      }

      return new DERSequence(var1);
   }

   public static class Rand extends ASN1Object {
      private final ASN1Integer integer;
      private final GeneralName sender;

      public Rand(byte[] var1, GeneralName var2) {
         this(new ASN1Integer(var1), var2);
      }

      public Rand(ASN1Integer var1, GeneralName var2) {
         this.integer = var1;
         this.sender = var2;
      }

      private Rand(ASN1Sequence var1) {
         if (var1.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
         } else {
            this.integer = ASN1Integer.getInstance(var1.getObjectAt(0));
            this.sender = GeneralName.getInstance(var1.getObjectAt(1));
         }
      }

      public static Challenge.Rand getInstance(Object var0) {
         if (var0 instanceof Challenge.Rand) {
            return (Challenge.Rand)var0;
         } else {
            return var0 != null ? new Challenge.Rand(ASN1Sequence.getInstance(var0)) : null;
         }
      }

      public ASN1Integer getInt() {
         return this.integer;
      }

      public GeneralName getSender() {
         return this.sender;
      }

      @Override
      public ASN1Primitive toASN1Primitive() {
         return new DERSequence(new ASN1Encodable[]{this.integer, this.sender});
      }
   }
}
