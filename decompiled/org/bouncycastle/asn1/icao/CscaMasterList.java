package org.bouncycastle.asn1.icao;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.Certificate;

public class CscaMasterList extends ASN1Object {
   private ASN1Integer version = new ASN1Integer(0L);
   private Certificate[] certList;

   public static CscaMasterList getInstance(Object var0) {
      if (var0 instanceof CscaMasterList) {
         return (CscaMasterList)var0;
      } else {
         return var0 != null ? new CscaMasterList(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private CscaMasterList(ASN1Sequence var1) {
      if (var1 != null && var1.size() != 0) {
         if (var1.size() != 2) {
            throw new IllegalArgumentException("Incorrect sequence size: " + var1.size());
         } else {
            this.version = ASN1Integer.getInstance(var1.getObjectAt(0));
            ASN1Set var2 = ASN1Set.getInstance(var1.getObjectAt(1));
            this.certList = new Certificate[var2.size()];

            for (int var3 = 0; var3 < this.certList.length; var3++) {
               this.certList[var3] = Certificate.getInstance(var2.getObjectAt(var3));
            }
         }
      } else {
         throw new IllegalArgumentException("null or empty sequence passed.");
      }
   }

   public CscaMasterList(Certificate[] var1) {
      this.certList = this.copyCertList(var1);
   }

   public int getVersion() {
      return this.version.intValueExact();
   }

   public Certificate[] getCertStructs() {
      return this.copyCertList(this.certList);
   }

   private Certificate[] copyCertList(Certificate[] var1) {
      Certificate[] var2 = new Certificate[var1.length];

      for (int var3 = 0; var3 != var2.length; var3++) {
         var2[var3] = var1[var3];
      }

      return var2;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.version, new DERSet(this.certList));
   }
}
