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

public class FalconPrivateKey extends ASN1Object {
   private int version;
   private byte[] f;
   private byte[] g;
   private byte[] F;
   private FalconPublicKey publicKey;

   public FalconPrivateKey(int var1, byte[] var2, byte[] var3, byte[] var4, FalconPublicKey var5) {
      this.version = var1;
      this.f = var2;
      this.g = var3;
      this.F = var4;
      this.publicKey = var5;
   }

   public FalconPrivateKey(int var1, byte[] var2, byte[] var3, byte[] var4) {
      this(var1, var2, var3, var4, null);
   }

   public int getVersion() {
      return this.version;
   }

   public byte[] getf() {
      return Arrays.clone(this.f);
   }

   public byte[] getF() {
      return Arrays.clone(this.F);
   }

   public FalconPublicKey getPublicKey() {
      return this.publicKey;
   }

   public byte[] getG() {
      return Arrays.clone(this.g);
   }

   private FalconPrivateKey(ASN1Sequence var1) {
      this.version = ASN1Integer.getInstance(var1.getObjectAt(0)).intValueExact();
      if (this.version != 0) {
         throw new IllegalArgumentException("unrecognized version");
      } else {
         this.f = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets());
         this.g = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets());
         this.F = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(3)).getOctets());
         if (var1.size() == 5) {
            this.publicKey = FalconPublicKey.getInstance(var1.getObjectAt(4));
         }
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(new ASN1Integer(this.version));
      var1.add(new DEROctetString(this.f));
      var1.add(new DEROctetString(this.g));
      var1.add(new DEROctetString(this.F));
      if (this.publicKey != null) {
         var1.add(new FalconPublicKey(this.publicKey.getH()));
      }

      return new DERSequence(var1);
   }

   public static FalconPrivateKey getInstance(Object var0) {
      if (var0 instanceof FalconPrivateKey) {
         return (FalconPrivateKey)var0;
      } else {
         return var0 != null ? new FalconPrivateKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
