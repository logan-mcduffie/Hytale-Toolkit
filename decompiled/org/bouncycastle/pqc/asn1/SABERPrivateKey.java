package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class SABERPrivateKey extends ASN1Object {
   private int version;
   private byte[] z;
   private byte[] s;
   private byte[] hpk;
   private SABERPublicKey PublicKey;

   public SABERPrivateKey(int var1, byte[] var2, byte[] var3, byte[] var4) {
      this.version = var1;
      if (var1 != 0) {
         throw new IllegalArgumentException("unrecognized version");
      } else {
         this.z = var2;
         this.s = var3;
         this.hpk = var4;
      }
   }

   public SABERPrivateKey(int var1, byte[] var2, byte[] var3, byte[] var4, SABERPublicKey var5) {
      this.version = var1;
      if (var1 != 0) {
         throw new IllegalArgumentException("unrecognized version");
      } else {
         this.z = var2;
         this.s = var3;
         this.hpk = var4;
         this.PublicKey = var5;
      }
   }

   private SABERPrivateKey(ASN1Sequence var1) {
      this.version = ASN1Integer.getInstance(var1.getObjectAt(0)).intValueExact();
      if (this.version != 0) {
         throw new IllegalArgumentException("unrecognized version");
      } else {
         this.z = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets());
         this.s = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets());
         this.PublicKey = SABERPublicKey.getInstance(var1.getObjectAt(3));
         this.hpk = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(4)).getOctets());
      }
   }

   public int getVersion() {
      return this.version;
   }

   public byte[] getZ() {
      return this.z;
   }

   public byte[] getS() {
      return this.s;
   }

   public byte[] getHpk() {
      return this.hpk;
   }

   public SABERPublicKey getPublicKey() {
      return this.PublicKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(new ASN1Integer(this.version));
      var1.add(new DEROctetString(this.z));
      var1.add(new DEROctetString(this.s));
      var1.add(new DEROctetString(this.hpk));
      return new DERSequence(var1);
   }

   public static SABERPrivateKey getInstance(Object var0) {
      if (var0 instanceof SABERPrivateKey) {
         return (SABERPrivateKey)var0;
      } else {
         return var0 != null ? new SABERPrivateKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
