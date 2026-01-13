package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ArchiveTimeStampChain extends ASN1Object {
   private ASN1Sequence archiveTimeStamps;

   public static ArchiveTimeStampChain getInstance(Object var0) {
      if (var0 instanceof ArchiveTimeStampChain) {
         return (ArchiveTimeStampChain)var0;
      } else {
         return var0 != null ? new ArchiveTimeStampChain(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ArchiveTimeStampChain(ArchiveTimeStamp var1) {
      this.archiveTimeStamps = new DERSequence(var1);
   }

   public ArchiveTimeStampChain(ArchiveTimeStamp[] var1) {
      this.archiveTimeStamps = new DERSequence(var1);
   }

   private ArchiveTimeStampChain(ASN1Sequence var1) {
      ASN1EncodableVector var2 = new ASN1EncodableVector(var1.size());
      Enumeration var3 = var1.getObjects();

      while (var3.hasMoreElements()) {
         var2.add(ArchiveTimeStamp.getInstance(var3.nextElement()));
      }

      this.archiveTimeStamps = new DERSequence(var2);
   }

   public ArchiveTimeStamp[] getArchiveTimestamps() {
      ArchiveTimeStamp[] var1 = new ArchiveTimeStamp[this.archiveTimeStamps.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = ArchiveTimeStamp.getInstance(this.archiveTimeStamps.getObjectAt(var2));
      }

      return var1;
   }

   public ArchiveTimeStampChain append(ArchiveTimeStamp var1) {
      ASN1EncodableVector var2 = new ASN1EncodableVector(this.archiveTimeStamps.size() + 1);

      for (int var3 = 0; var3 != this.archiveTimeStamps.size(); var3++) {
         var2.add(this.archiveTimeStamps.getObjectAt(var3));
      }

      var2.add(var1);
      return new ArchiveTimeStampChain(new DERSequence(var2));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.archiveTimeStamps;
   }
}
