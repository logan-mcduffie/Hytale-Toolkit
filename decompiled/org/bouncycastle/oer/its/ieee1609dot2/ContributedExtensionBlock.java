package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiOriginatingHeaderInfoExtension;

public class ContributedExtensionBlock extends ASN1Object {
   private final HeaderInfoContributorId contributorId;
   private final List<EtsiOriginatingHeaderInfoExtension> extns;

   public ContributedExtensionBlock(HeaderInfoContributorId var1, List<EtsiOriginatingHeaderInfoExtension> var2) {
      this.contributorId = var1;
      this.extns = var2;
   }

   private ContributedExtensionBlock(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.contributorId = HeaderInfoContributorId.getInstance(var1.getObjectAt(0));
         Iterator var2 = ASN1Sequence.getInstance(var1.getObjectAt(1)).iterator();
         ArrayList var3 = new ArrayList();

         while (var2.hasNext()) {
            var3.add(EtsiOriginatingHeaderInfoExtension.getInstance(var2.next()));
         }

         this.extns = Collections.unmodifiableList(var3);
      }
   }

   public static ContributedExtensionBlock getInstance(Object var0) {
      if (var0 instanceof ContributedExtensionBlock) {
         return (ContributedExtensionBlock)var0;
      } else {
         return var0 != null ? new ContributedExtensionBlock(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.contributorId, ItsUtils.toSequence(this.extns));
   }

   public HeaderInfoContributorId getContributorId() {
      return this.contributorId;
   }

   public List<EtsiOriginatingHeaderInfoExtension> getExtns() {
      return this.extns;
   }
}
