package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PBKDF2Params extends ASN1Object {
   private static final AlgorithmIdentifier algid_hmacWithSHA1 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE);
   private final ASN1OctetString octStr;
   private final ASN1Integer iterationCount;
   private final ASN1Integer keyLength;
   private final AlgorithmIdentifier prf;

   public static PBKDF2Params getInstance(Object var0) {
      if (var0 instanceof PBKDF2Params) {
         return (PBKDF2Params)var0;
      } else {
         return var0 != null ? new PBKDF2Params(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PBKDF2Params(byte[] var1, int var2) {
      this(var1, var2, 0);
   }

   public PBKDF2Params(byte[] var1, int var2, int var3) {
      this(var1, var2, var3, null);
   }

   public PBKDF2Params(byte[] var1, int var2, int var3, AlgorithmIdentifier var4) {
      this.octStr = new DEROctetString(Arrays.clone(var1));
      this.iterationCount = new ASN1Integer(var2);
      if (var3 > 0) {
         this.keyLength = new ASN1Integer(var3);
      } else {
         this.keyLength = null;
      }

      this.prf = var4;
   }

   public PBKDF2Params(byte[] var1, int var2, AlgorithmIdentifier var3) {
      this(var1, var2, 0, var3);
   }

   private PBKDF2Params(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.octStr = (ASN1OctetString)var2.nextElement();
      this.iterationCount = (ASN1Integer)var2.nextElement();
      if (var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (var3 instanceof ASN1Integer) {
            this.keyLength = ASN1Integer.getInstance(var3);
            if (var2.hasMoreElements()) {
               var3 = var2.nextElement();
            } else {
               var3 = null;
            }
         } else {
            this.keyLength = null;
         }

         if (var3 != null) {
            this.prf = AlgorithmIdentifier.getInstance(var3);
         } else {
            this.prf = null;
         }
      } else {
         this.keyLength = null;
         this.prf = null;
      }
   }

   public byte[] getSalt() {
      return Arrays.clone(this.octStr.getOctets());
   }

   public BigInteger getIterationCount() {
      return this.iterationCount.getValue();
   }

   public BigInteger getKeyLength() {
      return this.keyLength != null ? this.keyLength.getValue() : null;
   }

   public boolean isDefaultPrf() {
      return this.prf == null || this.prf.equals(algid_hmacWithSHA1);
   }

   public AlgorithmIdentifier getPrf() {
      return this.prf != null ? this.prf : algid_hmacWithSHA1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.octStr);
      var1.add(this.iterationCount);
      if (this.keyLength != null) {
         var1.add(this.keyLength);
      }

      if (this.prf != null && !this.prf.equals(algid_hmacWithSHA1)) {
         var1.add(this.prf);
      }

      return new DERSequence(var1);
   }
}
