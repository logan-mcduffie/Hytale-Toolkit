package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class CtlFormat extends ASN1Object {
   private final Version version;
   private final Time32 nextUpdate;
   private final ASN1Boolean isFullCtl;
   private final UINT8 ctlSequence;
   private final SequenceOfCtlCommand ctlCommands;

   public CtlFormat(Version var1, Time32 var2, ASN1Boolean var3, UINT8 var4, SequenceOfCtlCommand var5) {
      this.version = var1;
      this.nextUpdate = var2;
      this.isFullCtl = var3;
      this.ctlSequence = var4;
      this.ctlCommands = var5;
   }

   protected CtlFormat(ASN1Sequence var1) {
      if (var1.size() != 5) {
         throw new IllegalArgumentException("expected sequence size of 5");
      } else {
         this.version = Version.getInstance(var1.getObjectAt(0));
         this.nextUpdate = Time32.getInstance(var1.getObjectAt(1));
         this.isFullCtl = ASN1Boolean.getInstance(var1.getObjectAt(2));
         this.ctlSequence = UINT8.getInstance(var1.getObjectAt(3));
         this.ctlCommands = SequenceOfCtlCommand.getInstance(var1.getObjectAt(4));
      }
   }

   public static CtlFormat getInstance(Object var0) {
      if (var0 instanceof CtlFormat) {
         return (CtlFormat)var0;
      } else {
         return var0 != null ? new CtlFormat(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Version getVersion() {
      return this.version;
   }

   public Time32 getNextUpdate() {
      return this.nextUpdate;
   }

   public ASN1Boolean getIsFullCtl() {
      return this.isFullCtl;
   }

   public UINT8 getCtlSequence() {
      return this.ctlSequence;
   }

   public SequenceOfCtlCommand getCtlCommands() {
      return this.ctlCommands;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands});
   }

   public static CtlFormat.Builder builder() {
      return new CtlFormat.Builder();
   }

   public static class Builder {
      private Version version;
      private Time32 nextUpdate;
      private ASN1Boolean isFullCtl;
      private UINT8 ctlSequence;
      private SequenceOfCtlCommand ctlCommands;

      public CtlFormat.Builder setVersion(Version var1) {
         this.version = var1;
         return this;
      }

      public CtlFormat.Builder setNextUpdate(Time32 var1) {
         this.nextUpdate = var1;
         return this;
      }

      public CtlFormat.Builder setIsFullCtl(ASN1Boolean var1) {
         this.isFullCtl = var1;
         return this;
      }

      public CtlFormat.Builder setCtlSequence(UINT8 var1) {
         this.ctlSequence = var1;
         return this;
      }

      public CtlFormat.Builder setCtlSequence(ASN1Integer var1) {
         this.ctlSequence = new UINT8(var1.getValue());
         return this;
      }

      public CtlFormat.Builder setCtlCommands(SequenceOfCtlCommand var1) {
         this.ctlCommands = var1;
         return this;
      }

      public CtlFormat createCtlFormat() {
         return new CtlFormat(this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands);
      }

      public DeltaCtl createDeltaCtl() {
         if (this.isFullCtl != null && ASN1Boolean.TRUE.equals(this.isFullCtl)) {
            throw new IllegalArgumentException("isFullCtl must be false for DeltaCtl");
         } else {
            return new DeltaCtl(this.version, this.nextUpdate, this.ctlSequence, this.ctlCommands);
         }
      }

      public FullCtl createFullCtl() {
         return new FullCtl(this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands);
      }

      public ToBeSignedRcaCtl createToBeSignedRcaCtl() {
         return new ToBeSignedRcaCtl(this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands);
      }
   }
}
