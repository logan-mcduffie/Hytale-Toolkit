package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class CtlCommand extends ASN1Object implements ASN1Choice {
   private final int choice;
   private final ASN1Encodable ctlCommand;
   public static final int add = 0;
   public static final int delete = 1;

   public CtlCommand(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.ctlCommand = var2;
   }

   private CtlCommand(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.ctlCommand = CtlEntry.getInstance(var1.getExplicitBaseObject());
            return;
         case 1:
            this.ctlCommand = CtlDelete.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static CtlCommand getInstance(Object var0) {
      if (var0 instanceof CtlCommand) {
         return (CtlCommand)var0;
      } else {
         return var0 != null ? new CtlCommand(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static CtlCommand add(CtlEntry var0) {
      return new CtlCommand(0, var0);
   }

   public static CtlCommand delete(CtlDelete var0) {
      return new CtlCommand(1, var0);
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getCtlCommand() {
      return this.ctlCommand;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.ctlCommand);
   }
}
