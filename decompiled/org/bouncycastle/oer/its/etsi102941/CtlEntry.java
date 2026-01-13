package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class CtlEntry extends ASN1Object implements ASN1Choice {
   public static final int rca = 0;
   public static final int ea = 1;
   public static final int aa = 2;
   public static final int dc = 3;
   public static final int tlm = 4;
   private final int choice;
   private final ASN1Encodable ctlEntry;

   public CtlEntry(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.ctlEntry = var2;
   }

   private CtlEntry(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.ctlEntry = RootCaEntry.getInstance(var1.getExplicitBaseObject());
            return;
         case 1:
            this.ctlEntry = EaEntry.getInstance(var1.getExplicitBaseObject());
            return;
         case 2:
            this.ctlEntry = AaEntry.getInstance(var1.getExplicitBaseObject());
            return;
         case 3:
            this.ctlEntry = DcEntry.getInstance(var1.getExplicitBaseObject());
            return;
         case 4:
            this.ctlEntry = TlmEntry.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static CtlEntry getInstance(Object var0) {
      if (var0 instanceof CtlEntry) {
         return (CtlEntry)var0;
      } else {
         return var0 != null ? new CtlEntry(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static CtlEntry rca(RootCaEntry var0) {
      return new CtlEntry(0, var0);
   }

   public static CtlEntry ea(EaEntry var0) {
      return new CtlEntry(1, var0);
   }

   public static CtlEntry aa(AaEntry var0) {
      return new CtlEntry(2, var0);
   }

   public static CtlEntry dc(DcEntry var0) {
      return new CtlEntry(3, var0);
   }

   public static CtlEntry tlm(TlmEntry var0) {
      return new CtlEntry(4, var0);
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getCtlEntry() {
      return this.ctlEntry;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.ctlEntry);
   }
}
