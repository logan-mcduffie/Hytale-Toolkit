package org.bouncycastle.crypto.agreement.kdf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Pack;

public class DHKEKGenerator implements DerivationFunction {
   private final Digest digest;
   private ASN1ObjectIdentifier algorithm;
   private int keySize;
   private byte[] z;
   private byte[] partyAInfo;

   public DHKEKGenerator(Digest var1) {
      this.digest = var1;
   }

   @Override
   public void init(DerivationParameters var1) {
      DHKDFParameters var2 = (DHKDFParameters)var1;
      this.algorithm = var2.getAlgorithm();
      this.keySize = var2.getKeySize();
      this.z = var2.getZ();
      this.partyAInfo = var2.getExtraInfo();
   }

   public Digest getDigest() {
      return this.digest;
   }

   @Override
   public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
      if (var1.length - var3 < var2) {
         throw new OutputLengthException("output buffer too small");
      } else {
         long var4 = var3;
         int var6 = this.digest.getDigestSize();
         if (var4 > 8589934591L) {
            throw new IllegalArgumentException("Output length too large");
         } else {
            int var7 = (int)((var4 + var6 - 1L) / var6);
            byte[] var8 = new byte[this.digest.getDigestSize()];
            int var9 = 1;

            for (int var10 = 0; var10 < var7; var10++) {
               this.digest.update(this.z, 0, this.z.length);
               ASN1EncodableVector var11 = new ASN1EncodableVector();
               ASN1EncodableVector var12 = new ASN1EncodableVector();
               var12.add(this.algorithm);
               var12.add(new DEROctetString(Pack.intToBigEndian(var9)));
               var11.add(new DERSequence(var12));
               if (this.partyAInfo != null) {
                  var11.add(new DERTaggedObject(true, 0, new DEROctetString(this.partyAInfo)));
               }

               var11.add(new DERTaggedObject(true, 2, new DEROctetString(Pack.intToBigEndian(this.keySize))));

               try {
                  byte[] var13 = new DERSequence(var11).getEncoded("DER");
                  this.digest.update(var13, 0, var13.length);
               } catch (IOException var14) {
                  throw new IllegalArgumentException("unable to encode parameter info: " + var14.getMessage());
               }

               this.digest.doFinal(var8, 0);
               if (var3 > var6) {
                  System.arraycopy(var8, 0, var1, var2, var6);
                  var2 += var6;
                  var3 -= var6;
               } else {
                  System.arraycopy(var8, 0, var1, var2, var3);
               }

               var9++;
            }

            this.digest.reset();
            return (int)var4;
         }
      }
   }
}
