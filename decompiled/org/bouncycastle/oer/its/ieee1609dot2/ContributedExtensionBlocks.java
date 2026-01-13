package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ContributedExtensionBlocks extends ASN1Object {
   private final List<ContributedExtensionBlock> contributedExtensionBlocks;

   public ContributedExtensionBlocks(List<ContributedExtensionBlock> var1) {
      this.contributedExtensionBlocks = Collections.unmodifiableList(var1);
   }

   private ContributedExtensionBlocks(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(ContributedExtensionBlock.getInstance(var3.next()));
      }

      this.contributedExtensionBlocks = Collections.unmodifiableList(var2);
   }

   public static ContributedExtensionBlocks.Builder builder() {
      return new ContributedExtensionBlocks.Builder();
   }

   public List<ContributedExtensionBlock> getContributedExtensionBlocks() {
      return this.contributedExtensionBlocks;
   }

   public int size() {
      return this.contributedExtensionBlocks.size();
   }

   public static ContributedExtensionBlocks getInstance(Object var0) {
      if (var0 instanceof ContributedExtensionBlocks) {
         return (ContributedExtensionBlocks)var0;
      } else {
         return var0 != null ? new ContributedExtensionBlocks(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.contributedExtensionBlocks.toArray(new ContributedExtensionBlock[0]));
   }

   public static class Builder {
      private final List<ContributedExtensionBlock> extensionBlocks = new ArrayList<>();

      public ContributedExtensionBlocks.Builder add(ContributedExtensionBlock... var1) {
         this.extensionBlocks.addAll(Arrays.asList(var1));
         return this;
      }

      public ContributedExtensionBlocks build() {
         return new ContributedExtensionBlocks(this.extensionBlocks);
      }
   }
}
