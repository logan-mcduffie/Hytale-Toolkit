package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class LinkageSeed extends ASN1Object {
   private final byte[] linkageSeed;

   public LinkageSeed(byte[] var1) {
      if (var1.length != 16) {
         throw new IllegalArgumentException("linkage seed not 16 bytes");
      } else {
         this.linkageSeed = Arrays.clone(var1);
      }
   }

   private LinkageSeed(ASN1OctetString var1) {
      this(var1.getOctets());
   }

   public static LinkageSeed getInstance(Object var0) {
      if (var0 instanceof LinkageSeed) {
         return (LinkageSeed)var0;
      } else {
         return var0 != null ? new LinkageSeed(DEROctetString.getInstance(var0)) : null;
      }
   }

   public byte[] getLinkageSeed() {
      return this.linkageSeed;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DEROctetString(this.linkageSeed);
   }
}
