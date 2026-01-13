package org.bouncycastle.asn1.cryptopro;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class GOST28147Parameters extends ASN1Object {
   private ASN1OctetString iv;
   private ASN1ObjectIdentifier paramSet;

   public static GOST28147Parameters getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static GOST28147Parameters getInstance(Object var0) {
      if (var0 instanceof GOST28147Parameters) {
         return (GOST28147Parameters)var0;
      } else {
         return var0 != null ? new GOST28147Parameters(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public GOST28147Parameters(byte[] var1, ASN1ObjectIdentifier var2) {
      this.iv = new DEROctetString(Arrays.clone(var1));
      this.paramSet = var2;
   }

   private GOST28147Parameters(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.iv = (ASN1OctetString)var2.nextElement();
      this.paramSet = (ASN1ObjectIdentifier)var2.nextElement();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.iv, this.paramSet);
   }

   public ASN1ObjectIdentifier getEncryptionParamSet() {
      return this.paramSet;
   }

   public byte[] getIV() {
      return Arrays.clone(this.iv.getOctets());
   }
}
