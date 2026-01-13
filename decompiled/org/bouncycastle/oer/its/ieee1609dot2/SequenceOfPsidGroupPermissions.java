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

public class SequenceOfPsidGroupPermissions extends ASN1Object {
   private final List<PsidGroupPermissions> psidGroupPermissions;

   public SequenceOfPsidGroupPermissions(List<PsidGroupPermissions> var1) {
      this.psidGroupPermissions = Collections.unmodifiableList(var1);
   }

   private SequenceOfPsidGroupPermissions(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(PsidGroupPermissions.getInstance(var3.next()));
      }

      this.psidGroupPermissions = Collections.unmodifiableList(var2);
   }

   public static SequenceOfPsidGroupPermissions getInstance(Object var0) {
      if (var0 instanceof SequenceOfPsidGroupPermissions) {
         return (SequenceOfPsidGroupPermissions)var0;
      } else {
         return var0 != null ? new SequenceOfPsidGroupPermissions(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<PsidGroupPermissions> getPsidGroupPermissions() {
      return this.psidGroupPermissions;
   }

   public static SequenceOfPsidGroupPermissions.Builder builder() {
      return new SequenceOfPsidGroupPermissions.Builder();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.psidGroupPermissions.toArray(new PsidGroupPermissions[0]));
   }

   public static class Builder {
      private final List<PsidGroupPermissions> groupPermissions = new ArrayList<>();

      public SequenceOfPsidGroupPermissions.Builder setGroupPermissions(List<PsidGroupPermissions> var1) {
         this.groupPermissions.addAll(var1);
         return this;
      }

      public SequenceOfPsidGroupPermissions.Builder addGroupPermission(PsidGroupPermissions... var1) {
         this.groupPermissions.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfPsidGroupPermissions createSequenceOfPsidGroupPermissions() {
         return new SequenceOfPsidGroupPermissions(this.groupPermissions);
      }
   }
}
