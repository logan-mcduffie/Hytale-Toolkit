package org.bouncycastle.crypto.signers;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class StandardDSAEncoding implements DSAEncoding {
   public static final StandardDSAEncoding INSTANCE = new StandardDSAEncoding();

   @Override
   public byte[] encode(BigInteger var1, BigInteger var2, BigInteger var3) throws IOException {
      ASN1EncodableVector var4 = new ASN1EncodableVector();
      this.encodeValue(var1, var4, var2);
      this.encodeValue(var1, var4, var3);
      return new DERSequence(var4).getEncoded("DER");
   }

   @Override
   public BigInteger[] decode(BigInteger var1, byte[] var2) throws IOException {
      ASN1Sequence var3 = (ASN1Sequence)ASN1Primitive.fromByteArray(var2);
      if (var3.size() == 2) {
         BigInteger var4 = this.decodeValue(var1, var3, 0);
         BigInteger var5 = this.decodeValue(var1, var3, 1);
         byte[] var6 = this.encode(var1, var4, var5);
         if (Arrays.areEqual(var6, var2)) {
            return new BigInteger[]{var4, var5};
         }
      }

      throw new IllegalArgumentException("Malformed signature");
   }

   protected BigInteger checkValue(BigInteger var1, BigInteger var2) {
      if (var2.signum() >= 0 && (null == var1 || var2.compareTo(var1) < 0)) {
         return var2;
      } else {
         throw new IllegalArgumentException("Value out of range");
      }
   }

   protected BigInteger decodeValue(BigInteger var1, ASN1Sequence var2, int var3) {
      return this.checkValue(var1, ((ASN1Integer)var2.getObjectAt(var3)).getValue());
   }

   protected void encodeValue(BigInteger var1, ASN1EncodableVector var2, BigInteger var3) {
      var2.add(new ASN1Integer(this.checkValue(var1, var3)));
   }
}
