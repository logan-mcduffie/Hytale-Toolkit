package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class ToBeSignedCrl extends ASN1Object {
   private final Version version;
   private final Time32 thisUpdate;
   private final Time32 nextUpdate;
   private final SequenceOfCrlEntry entries;

   public ToBeSignedCrl(Version var1, Time32 var2, Time32 var3, SequenceOfCrlEntry var4) {
      this.version = var1;
      this.thisUpdate = var2;
      this.nextUpdate = var3;
      this.entries = var4;
   }

   private ToBeSignedCrl(ASN1Sequence var1) {
      if (var1.size() != 4) {
         throw new IllegalArgumentException("expected sequence size of 4");
      } else {
         this.version = Version.getInstance(var1.getObjectAt(0));
         this.thisUpdate = Time32.getInstance(var1.getObjectAt(1));
         this.nextUpdate = Time32.getInstance(var1.getObjectAt(2));
         this.entries = SequenceOfCrlEntry.getInstance(var1.getObjectAt(3));
      }
   }

   public static ToBeSignedCrl getInstance(Object var0) {
      if (var0 instanceof ToBeSignedCrl) {
         return (ToBeSignedCrl)var0;
      } else {
         return var0 != null ? new ToBeSignedCrl(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Version getVersion() {
      return this.version;
   }

   public Time32 getThisUpdate() {
      return this.thisUpdate;
   }

   public Time32 getNextUpdate() {
      return this.nextUpdate;
   }

   public SequenceOfCrlEntry getEntries() {
      return this.entries;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.version, this.thisUpdate, this.nextUpdate, this.entries});
   }

   public static ToBeSignedCrl.Builder builder() {
      return new ToBeSignedCrl.Builder();
   }

   public static class Builder {
      private Version version;
      private Time32 thisUpdate;
      private Time32 nextUpdate;
      private SequenceOfCrlEntry entries;

      public ToBeSignedCrl.Builder setVersion(Version var1) {
         this.version = var1;
         return this;
      }

      public ToBeSignedCrl.Builder setThisUpdate(Time32 var1) {
         this.thisUpdate = var1;
         return this;
      }

      public ToBeSignedCrl.Builder setNextUpdate(Time32 var1) {
         this.nextUpdate = var1;
         return this;
      }

      public ToBeSignedCrl.Builder setEntries(SequenceOfCrlEntry var1) {
         this.entries = var1;
         return this;
      }

      public ToBeSignedCrl createToBeSignedCrl() {
         return new ToBeSignedCrl(this.version, this.thisUpdate, this.nextUpdate, this.entries);
      }
   }
}
