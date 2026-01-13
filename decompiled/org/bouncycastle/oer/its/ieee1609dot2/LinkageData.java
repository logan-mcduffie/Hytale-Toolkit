package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.GroupLinkageValue;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.IValue;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.LinkageValue;

public class LinkageData extends ASN1Object {
   private final IValue iCert;
   private final LinkageValue linkageValue;
   private final GroupLinkageValue groupLinkageValue;

   public LinkageData(IValue var1, LinkageValue var2, GroupLinkageValue var3) {
      this.iCert = var1;
      this.linkageValue = var2;
      this.groupLinkageValue = var3;
   }

   private LinkageData(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("expected sequence size of 3");
      } else {
         this.iCert = IValue.getInstance(var1.getObjectAt(0));
         this.linkageValue = LinkageValue.getInstance(var1.getObjectAt(1));
         this.groupLinkageValue = OEROptional.getValue(GroupLinkageValue.class, var1.getObjectAt(2));
      }
   }

   public static LinkageData getInstance(Object var0) {
      if (var0 instanceof LinkageData) {
         return (LinkageData)var0;
      } else {
         return var0 != null ? new LinkageData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public IValue getICert() {
      return this.iCert;
   }

   public LinkageValue getLinkageValue() {
      return this.linkageValue;
   }

   public GroupLinkageValue getGroupLinkageValue() {
      return this.groupLinkageValue;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.iCert, this.linkageValue, OEROptional.getInstance(this.groupLinkageValue)});
   }

   public static LinkageData.Builder builder() {
      return new LinkageData.Builder();
   }

   public static class Builder {
      private IValue iCert;
      private LinkageValue linkageValue;
      private GroupLinkageValue groupLinkageValue;

      public LinkageData.Builder setICert(IValue var1) {
         this.iCert = var1;
         return this;
      }

      public LinkageData.Builder setLinkageValue(LinkageValue var1) {
         this.linkageValue = var1;
         return this;
      }

      public LinkageData.Builder setGroupLinkageValue(GroupLinkageValue var1) {
         this.groupLinkageValue = var1;
         return this;
      }

      public LinkageData createLinkageData() {
         return new LinkageData(this.iCert, this.linkageValue, this.groupLinkageValue);
      }
   }
}
