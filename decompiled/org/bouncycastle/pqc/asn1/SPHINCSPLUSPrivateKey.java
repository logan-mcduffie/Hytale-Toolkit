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

public class SPHINCSPLUSPrivateKey extends ASN1Object {
   private int version;
   private byte[] skseed;
   private byte[] skprf;
   private SPHINCSPLUSPublicKey PublicKey;

   public int getVersion() {
      return this.version;
   }

   public byte[] getSkseed() {
      return Arrays.clone(this.skseed);
   }

   public byte[] getSkprf() {
      return Arrays.clone(this.skprf);
   }

   public SPHINCSPLUSPublicKey getPublicKey() {
      return this.PublicKey;
   }

   public SPHINCSPLUSPrivateKey(int var1, byte[] var2, byte[] var3) {
      this(var1, var2, var3, null);
   }

   public SPHINCSPLUSPrivateKey(int var1, byte[] var2, byte[] var3, SPHINCSPLUSPublicKey var4) {
      this.version = var1;
      this.skseed = var2;
      this.skprf = var3;
      this.PublicKey = var4;
   }

   @Deprecated
   public SPHINCSPLUSPrivateKey(ASN1Sequence var1) {
      this.version = ASN1Integer.getInstance(var1.getObjectAt(0)).intValueExact();
      if (this.version != 0) {
         throw new IllegalArgumentException("unrecognized version");
      } else {
         this.skseed = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets());
         this.skprf = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets());
         if (var1.size() == 4) {
            this.PublicKey = SPHINCSPLUSPublicKey.getInstance(var1.getObjectAt(3));
         }
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(new ASN1Integer(this.version));
      var1.add(new DEROctetString(this.skseed));
      var1.add(new DEROctetString(this.skprf));
      if (this.PublicKey != null) {
         var1.add(new SPHINCSPLUSPublicKey(this.PublicKey.getPkseed(), this.PublicKey.getPkroot()));
      }

      return new DERSequence(var1);
   }

   public static SPHINCSPLUSPrivateKey getInstance(Object var0) {
      if (var0 instanceof SPHINCSPLUSPrivateKey) {
         return (SPHINCSPLUSPrivateKey)var0;
      } else {
         return var0 != null ? new SPHINCSPLUSPrivateKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
