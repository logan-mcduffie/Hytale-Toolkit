package org.bouncycastle.oer.its.etsi102941;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SequenceOfCtlCommand extends ASN1Object {
   private final List<CtlCommand> ctlCommands;

   public SequenceOfCtlCommand(List<CtlCommand> var1) {
      this.ctlCommands = Collections.unmodifiableList(var1);
   }

   private SequenceOfCtlCommand(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(CtlCommand.getInstance(var3.next()));
      }

      this.ctlCommands = Collections.unmodifiableList(var2);
   }

   public static SequenceOfCtlCommand.Builder builder() {
      return new SequenceOfCtlCommand.Builder();
   }

   public static SequenceOfCtlCommand getInstance(Object var0) {
      if (var0 instanceof SequenceOfCtlCommand) {
         return (SequenceOfCtlCommand)var0;
      } else {
         return var0 != null ? new SequenceOfCtlCommand(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<CtlCommand> getCtlCommands() {
      return this.ctlCommands;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.ctlCommands.toArray(new ASN1Encodable[0]));
   }

   public static class Builder {
      private final List<CtlCommand> items = new ArrayList<>();

      public SequenceOfCtlCommand.Builder addHashId8(CtlCommand... var1) {
         this.items.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfCtlCommand build() {
         return new SequenceOfCtlCommand(this.items);
      }
   }
}
