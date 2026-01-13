package org.bouncycastle.asn1.misc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class ScryptParams extends ASN1Object {
   private final byte[] salt;
   private final BigInteger costParameter;
   private final BigInteger blockSize;
   private final BigInteger parallelizationParameter;
   private final BigInteger keyLength;

   public ScryptParams(byte[] var1, int var2, int var3, int var4) {
      this(var1, BigInteger.valueOf(var2), BigInteger.valueOf(var3), BigInteger.valueOf(var4), null);
   }

   public ScryptParams(byte[] var1, int var2, int var3, int var4, int var5) {
      this(var1, BigInteger.valueOf(var2), BigInteger.valueOf(var3), BigInteger.valueOf(var4), BigInteger.valueOf(var5));
   }

   public ScryptParams(byte[] var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5) {
      this.salt = Arrays.clone(var1);
      this.costParameter = var2;
      this.blockSize = var3;
      this.parallelizationParameter = var4;
      this.keyLength = var5;
   }

   public static ScryptParams getInstance(Object var0) {
      if (var0 instanceof ScryptParams) {
         return (ScryptParams)var0;
      } else {
         return var0 != null ? new ScryptParams(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private ScryptParams(ASN1Sequence var1) {
      if (var1.size() != 4 && var1.size() != 5) {
         throw new IllegalArgumentException("invalid sequence: size = " + var1.size());
      } else {
         this.salt = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(0)).getOctets());
         this.costParameter = ASN1Integer.getInstance(var1.getObjectAt(1)).getValue();
         this.blockSize = ASN1Integer.getInstance(var1.getObjectAt(2)).getValue();
         this.parallelizationParameter = ASN1Integer.getInstance(var1.getObjectAt(3)).getValue();
         if (var1.size() == 5) {
            this.keyLength = ASN1Integer.getInstance(var1.getObjectAt(4)).getValue();
         } else {
            this.keyLength = null;
         }
      }
   }

   public byte[] getSalt() {
      return Arrays.clone(this.salt);
   }

   public BigInteger getCostParameter() {
      return this.costParameter;
   }

   public BigInteger getBlockSize() {
      return this.blockSize;
   }

   public BigInteger getParallelizationParameter() {
      return this.parallelizationParameter;
   }

   public BigInteger getKeyLength() {
      return this.keyLength;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      var1.add(new DEROctetString(this.salt));
      var1.add(new ASN1Integer(this.costParameter));
      var1.add(new ASN1Integer(this.blockSize));
      var1.add(new ASN1Integer(this.parallelizationParameter));
      if (this.keyLength != null) {
         var1.add(new ASN1Integer(this.keyLength));
      }

      return new DERSequence(var1);
   }
}
