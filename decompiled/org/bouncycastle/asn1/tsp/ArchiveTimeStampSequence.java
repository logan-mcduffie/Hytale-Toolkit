package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ArchiveTimeStampSequence extends ASN1Object {
   private ASN1Sequence archiveTimeStampChains;

   public static ArchiveTimeStampSequence getInstance(Object var0) {
      if (var0 instanceof ArchiveTimeStampSequence) {
         return (ArchiveTimeStampSequence)var0;
      } else {
         return var0 != null ? new ArchiveTimeStampSequence(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private ArchiveTimeStampSequence(ASN1Sequence var1) {
      ASN1EncodableVector var2 = new ASN1EncodableVector(var1.size());
      Enumeration var3 = var1.getObjects();

      while (var3.hasMoreElements()) {
         var2.add(ArchiveTimeStampChain.getInstance(var3.nextElement()));
      }

      this.archiveTimeStampChains = new DERSequence(var2);
   }

   public ArchiveTimeStampSequence(ArchiveTimeStampChain var1) {
      this.archiveTimeStampChains = new DERSequence(var1);
   }

   public ArchiveTimeStampSequence(ArchiveTimeStampChain[] var1) {
      this.archiveTimeStampChains = new DERSequence(var1);
   }

   public ArchiveTimeStampChain[] getArchiveTimeStampChains() {
      ArchiveTimeStampChain[] var1 = new ArchiveTimeStampChain[this.archiveTimeStampChains.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = ArchiveTimeStampChain.getInstance(this.archiveTimeStampChains.getObjectAt(var2));
      }

      return var1;
   }

   public int size() {
      return this.archiveTimeStampChains.size();
   }

   public ArchiveTimeStampSequence append(ArchiveTimeStampChain var1) {
      ASN1EncodableVector var2 = new ASN1EncodableVector(this.archiveTimeStampChains.size() + 1);

      for (int var3 = 0; var3 != this.archiveTimeStampChains.size(); var3++) {
         var2.add(this.archiveTimeStampChains.getObjectAt(var3));
      }

      var2.add(var1);
      return new ArchiveTimeStampSequence(new DERSequence(var2));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.archiveTimeStampChains;
   }
}
