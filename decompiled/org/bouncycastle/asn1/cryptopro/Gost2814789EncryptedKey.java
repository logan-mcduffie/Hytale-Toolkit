package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class Gost2814789EncryptedKey extends ASN1Object {
   private final byte[] encryptedKey;
   private final byte[] maskKey;
   private final byte[] macKey;

   private Gost2814789EncryptedKey(ASN1Sequence var1) {
      if (var1.size() == 2) {
         this.encryptedKey = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(0)).getOctets());
         this.macKey = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets());
         this.maskKey = null;
      } else {
         if (var1.size() != 3) {
            throw new IllegalArgumentException("unknown sequence length: " + var1.size());
         }

         this.encryptedKey = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(0)).getOctets());
         this.maskKey = Arrays.clone(ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(1)), false).getOctets());
         this.macKey = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets());
      }
   }

   public static Gost2814789EncryptedKey getInstance(Object var0) {
      if (var0 instanceof Gost2814789EncryptedKey) {
         return (Gost2814789EncryptedKey)var0;
      } else {
         return var0 != null ? new Gost2814789EncryptedKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Gost2814789EncryptedKey(byte[] var1, byte[] var2) {
      this(var1, null, var2);
   }

   public Gost2814789EncryptedKey(byte[] var1, byte[] var2, byte[] var3) {
      this.encryptedKey = Arrays.clone(var1);
      this.maskKey = Arrays.clone(var2);
      this.macKey = Arrays.clone(var3);
   }

   public byte[] getEncryptedKey() {
      return Arrays.clone(this.encryptedKey);
   }

   public byte[] getMaskKey() {
      return Arrays.clone(this.maskKey);
   }

   public byte[] getMacKey() {
      return Arrays.clone(this.macKey);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(new DEROctetString(this.encryptedKey));
      if (this.maskKey != null) {
         var1.add(new DERTaggedObject(false, 0, new DEROctetString(this.encryptedKey)));
      }

      var1.add(new DEROctetString(this.macKey));
      return new DERSequence(var1);
   }
}
