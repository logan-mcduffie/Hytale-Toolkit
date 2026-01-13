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

public class CMCEPrivateKey extends ASN1Object {
   private int version;
   private byte[] delta;
   private byte[] C;
   private byte[] g;
   private byte[] alpha;
   private byte[] s;
   private CMCEPublicKey PublicKey;

   public CMCEPrivateKey(int var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      this(var1, var2, var3, var4, var5, var6, null);
   }

   public CMCEPrivateKey(int var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, CMCEPublicKey var7) {
      this.version = var1;
      if (var1 != 0) {
         throw new IllegalArgumentException("unrecognized version");
      } else {
         this.delta = Arrays.clone(var2);
         this.C = Arrays.clone(var3);
         this.g = Arrays.clone(var4);
         this.alpha = Arrays.clone(var5);
         this.s = Arrays.clone(var6);
         this.PublicKey = var7;
      }
   }

   private CMCEPrivateKey(ASN1Sequence var1) {
      this.version = ASN1Integer.getInstance(var1.getObjectAt(0)).intValueExact();
      if (this.version != 0) {
         throw new IllegalArgumentException("unrecognized version");
      } else {
         this.delta = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets());
         this.C = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets());
         this.g = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(3)).getOctets());
         this.alpha = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(4)).getOctets());
         this.s = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(5)).getOctets());
         if (var1.size() == 7) {
            this.PublicKey = CMCEPublicKey.getInstance(var1.getObjectAt(6));
         }
      }
   }

   public int getVersion() {
      return this.version;
   }

   public byte[] getDelta() {
      return Arrays.clone(this.delta);
   }

   public byte[] getC() {
      return Arrays.clone(this.C);
   }

   public byte[] getG() {
      return Arrays.clone(this.g);
   }

   public byte[] getAlpha() {
      return Arrays.clone(this.alpha);
   }

   public byte[] getS() {
      return Arrays.clone(this.s);
   }

   public CMCEPublicKey getPublicKey() {
      return this.PublicKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(new ASN1Integer(this.version));
      var1.add(new DEROctetString(this.delta));
      var1.add(new DEROctetString(this.C));
      var1.add(new DEROctetString(this.g));
      var1.add(new DEROctetString(this.alpha));
      var1.add(new DEROctetString(this.s));
      if (this.PublicKey != null) {
         var1.add(new CMCEPublicKey(this.PublicKey.getT()));
      }

      return new DERSequence(var1);
   }

   public static CMCEPrivateKey getInstance(Object var0) {
      if (var0 instanceof CMCEPrivateKey) {
         return (CMCEPrivateKey)var0;
      } else {
         return var0 != null ? new CMCEPrivateKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
