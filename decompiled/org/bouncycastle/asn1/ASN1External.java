package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Objects;

public abstract class ASN1External extends ASN1Primitive {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1External.class, 8) {
      @Override
      ASN1Primitive fromImplicitConstructed(ASN1Sequence var1) {
         return var1.toASN1External();
      }
   };
   ASN1ObjectIdentifier directReference;
   ASN1Integer indirectReference;
   ASN1Primitive dataValueDescriptor;
   int encoding;
   ASN1Primitive externalContent;

   public static ASN1External getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1External)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1External) {
               return (ASN1External)var1;
            }
         } else if (var0 instanceof byte[]) {
            try {
               return (ASN1External)TYPE.fromByteArray((byte[])var0);
            } catch (IOException var2) {
               throw new IllegalArgumentException("failed to construct external from byte[]: " + var2.getMessage());
            }
         }

         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      } else {
         return (ASN1External)var0;
      }
   }

   public static ASN1External getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1External)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1External getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1External)TYPE.getTagged(var0, var1);
   }

   ASN1External(ASN1Sequence var1) {
      int var2 = 0;
      ASN1Primitive var3 = getObjFromSequence(var1, var2);
      if (var3 instanceof ASN1ObjectIdentifier) {
         this.directReference = (ASN1ObjectIdentifier)var3;
         var3 = getObjFromSequence(var1, ++var2);
      }

      if (var3 instanceof ASN1Integer) {
         this.indirectReference = (ASN1Integer)var3;
         var3 = getObjFromSequence(var1, ++var2);
      }

      if (!(var3 instanceof ASN1TaggedObject)) {
         this.dataValueDescriptor = var3;
         var3 = getObjFromSequence(var1, ++var2);
      }

      if (var1.size() != var2 + 1) {
         throw new IllegalArgumentException("input sequence too large");
      } else if (!(var3 instanceof ASN1TaggedObject)) {
         throw new IllegalArgumentException("No tagged object found in sequence. Structure doesn't seem to be of type External");
      } else {
         ASN1TaggedObject var4 = (ASN1TaggedObject)var3;
         this.encoding = checkEncoding(var4.getTagNo());
         this.externalContent = getExternalContent(var4);
      }
   }

   ASN1External(ASN1ObjectIdentifier var1, ASN1Integer var2, ASN1Primitive var3, DERTaggedObject var4) {
      this.directReference = var1;
      this.indirectReference = var2;
      this.dataValueDescriptor = var3;
      this.encoding = checkEncoding(var4.getTagNo());
      this.externalContent = getExternalContent(var4);
   }

   ASN1External(ASN1ObjectIdentifier var1, ASN1Integer var2, ASN1Primitive var3, int var4, ASN1Primitive var5) {
      this.directReference = var1;
      this.indirectReference = var2;
      this.dataValueDescriptor = var3;
      this.encoding = checkEncoding(var4);
      this.externalContent = checkExternalContent(var4, var5);
   }

   abstract ASN1Sequence buildSequence();

   @Override
   int encodedLength(boolean var1) throws IOException {
      return this.buildSequence().encodedLength(var1);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeIdentifier(var2, 40);
      this.buildSequence().encode(var1, false);
   }

   @Override
   ASN1Primitive toDERObject() {
      return new DERExternal(this.directReference, this.indirectReference, this.dataValueDescriptor, this.encoding, this.externalContent);
   }

   @Override
   ASN1Primitive toDLObject() {
      return new DLExternal(this.directReference, this.indirectReference, this.dataValueDescriptor, this.encoding, this.externalContent);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.directReference)
         ^ Objects.hashCode(this.indirectReference)
         ^ Objects.hashCode(this.dataValueDescriptor)
         ^ this.encoding
         ^ this.externalContent.hashCode();
   }

   @Override
   boolean encodeConstructed() {
      return true;
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ASN1External)) {
         return false;
      } else {
         ASN1External var2 = (ASN1External)var1;
         return Objects.areEqual(this.directReference, var2.directReference)
            && Objects.areEqual(this.indirectReference, var2.indirectReference)
            && Objects.areEqual(this.dataValueDescriptor, var2.dataValueDescriptor)
            && this.encoding == var2.encoding
            && this.externalContent.equals(var2.externalContent);
      }
   }

   public ASN1Primitive getDataValueDescriptor() {
      return this.dataValueDescriptor;
   }

   public ASN1ObjectIdentifier getDirectReference() {
      return this.directReference;
   }

   public int getEncoding() {
      return this.encoding;
   }

   public ASN1Primitive getExternalContent() {
      return this.externalContent;
   }

   public ASN1Integer getIndirectReference() {
      return this.indirectReference;
   }

   private static int checkEncoding(int var0) {
      if (var0 >= 0 && var0 <= 2) {
         return var0;
      } else {
         throw new IllegalArgumentException("invalid encoding value: " + var0);
      }
   }

   private static ASN1Primitive checkExternalContent(int var0, ASN1Primitive var1) {
      switch (var0) {
         case 1:
            return ASN1OctetString.TYPE.checkedCast(var1);
         case 2:
            return ASN1BitString.TYPE.checkedCast(var1);
         default:
            return var1;
      }
   }

   private static ASN1Primitive getExternalContent(ASN1TaggedObject var0) {
      ASN1Util.checkContextTagClass(var0);
      switch (var0.getTagNo()) {
         case 0:
            return var0.getExplicitBaseObject().toASN1Primitive();
         case 1:
            return ASN1OctetString.getInstance(var0, false);
         case 2:
            return ASN1BitString.getInstance(var0, false);
         default:
            throw new IllegalArgumentException("invalid tag: " + ASN1Util.getTagText(var0));
      }
   }

   private static ASN1Primitive getObjFromSequence(ASN1Sequence var0, int var1) {
      if (var0.size() <= var1) {
         throw new IllegalArgumentException("too few objects in input sequence");
      } else {
         return var0.getObjectAt(var1).toASN1Primitive();
      }
   }
}
