package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public abstract class ASN1TaggedObject extends ASN1Primitive implements ASN1TaggedObjectParser {
   private static final int DECLARED_EXPLICIT = 1;
   private static final int DECLARED_IMPLICIT = 2;
   private static final int PARSED_EXPLICIT = 3;
   private static final int PARSED_IMPLICIT = 4;
   final int explicitness;
   final int tagClass;
   final int tagNo;
   final ASN1Encodable obj;

   public static ASN1TaggedObject getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1TaggedObject)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1TaggedObject) {
               return (ASN1TaggedObject)var1;
            }
         } else if (var0 instanceof byte[]) {
            try {
               return checkedCast(fromByteArray((byte[])var0));
            } catch (IOException var2) {
               throw new IllegalArgumentException("failed to construct tagged object from byte[]: " + var2.getMessage());
            }
         }

         throw new IllegalArgumentException("unknown object in getInstance: " + var0.getClass().getName());
      } else {
         return (ASN1TaggedObject)var0;
      }
   }

   public static ASN1TaggedObject getInstance(Object var0, int var1) {
      return ASN1Util.checkTagClass(checkInstance(var0), var1);
   }

   public static ASN1TaggedObject getInstance(Object var0, int var1, int var2) {
      return ASN1Util.checkTag(checkInstance(var0), var1, var2);
   }

   public static ASN1TaggedObject getInstance(ASN1TaggedObject var0, boolean var1) {
      return ASN1Util.getExplicitContextBaseTagged(checkInstance(var0, var1));
   }

   public static ASN1TaggedObject getInstance(ASN1TaggedObject var0, int var1, boolean var2) {
      return ASN1Util.getExplicitBaseTagged(checkInstance(var0, var2), var1);
   }

   public static ASN1TaggedObject getInstance(ASN1TaggedObject var0, int var1, int var2, boolean var3) {
      return ASN1Util.getExplicitBaseTagged(checkInstance(var0, var3), var1, var2);
   }

   public static ASN1TaggedObject getOptional(ASN1Encodable var0) {
      if (var0 == null) {
         throw new NullPointerException("'element' cannot be null");
      } else {
         return var0 instanceof ASN1TaggedObject ? (ASN1TaggedObject)var0 : null;
      }
   }

   public static ASN1TaggedObject getOptional(ASN1Encodable var0, int var1) {
      ASN1TaggedObject var2 = getOptional(var0);
      return var2 != null && var2.hasTagClass(var1) ? var2 : null;
   }

   public static ASN1TaggedObject getOptional(ASN1Encodable var0, int var1, int var2) {
      ASN1TaggedObject var3 = getOptional(var0);
      return var3 != null && var3.hasTag(var1, var2) ? var3 : null;
   }

   private static ASN1TaggedObject checkInstance(Object var0) {
      if (var0 == null) {
         throw new NullPointerException("'obj' cannot be null");
      } else {
         return getInstance(var0);
      }
   }

   private static ASN1TaggedObject checkInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("this method not valid for implicitly tagged tagged objects");
      } else if (var0 == null) {
         throw new NullPointerException("'taggedObject' cannot be null");
      } else {
         return var0;
      }
   }

   protected ASN1TaggedObject(boolean var1, int var2, ASN1Encodable var3) {
      this(var1, 128, var2, var3);
   }

   protected ASN1TaggedObject(boolean var1, int var2, int var3, ASN1Encodable var4) {
      this(var1 ? 1 : 2, var2, var3, var4);
   }

   ASN1TaggedObject(int var1, int var2, int var3, ASN1Encodable var4) {
      if (null == var4) {
         throw new NullPointerException("'obj' cannot be null");
      } else if (var2 != 0 && (var2 & 192) == var2) {
         this.explicitness = var4 instanceof ASN1Choice ? 1 : var1;
         this.tagClass = var2;
         this.tagNo = var3;
         this.obj = var4;
      } else {
         throw new IllegalArgumentException("invalid tag class: " + var2);
      }
   }

   @Override
   final boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1TaggedObject)) {
         return false;
      } else {
         ASN1TaggedObject var2 = (ASN1TaggedObject)var1;
         if (this.tagNo == var2.tagNo && this.tagClass == var2.tagClass) {
            if (this.explicitness != var2.explicitness && this.isExplicit() != var2.isExplicit()) {
               return false;
            } else {
               ASN1Primitive var3 = this.obj.toASN1Primitive();
               ASN1Primitive var4 = var2.obj.toASN1Primitive();
               if (var3 == var4) {
                  return true;
               } else if (!this.isExplicit()) {
                  try {
                     byte[] var5 = this.getEncoded();
                     byte[] var6 = var2.getEncoded();
                     return Arrays.areEqual(var5, var6);
                  } catch (IOException var7) {
                     return false;
                  }
               } else {
                  return var3.asn1Equals(var4);
               }
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      return this.tagClass * 7919 ^ this.tagNo ^ (this.isExplicit() ? 15 : 240) ^ this.obj.toASN1Primitive().hashCode();
   }

   @Override
   public int getTagClass() {
      return this.tagClass;
   }

   @Override
   public int getTagNo() {
      return this.tagNo;
   }

   @Override
   public boolean hasContextTag() {
      return this.tagClass == 128;
   }

   @Override
   public boolean hasContextTag(int var1) {
      return this.tagClass == 128 && this.tagNo == var1;
   }

   @Override
   public boolean hasTag(int var1, int var2) {
      return this.tagClass == var1 && this.tagNo == var2;
   }

   @Override
   public boolean hasTagClass(int var1) {
      return this.tagClass == var1;
   }

   public boolean isExplicit() {
      switch (this.explicitness) {
         case 1:
         case 3:
            return true;
         default:
            return false;
      }
   }

   boolean isParsed() {
      switch (this.explicitness) {
         case 3:
         case 4:
            return true;
         default:
            return false;
      }
   }

   public ASN1Object getBaseObject() {
      return (ASN1Object)(this.obj instanceof ASN1Object ? (ASN1Object)this.obj : this.obj.toASN1Primitive());
   }

   public ASN1Object getExplicitBaseObject() {
      if (!this.isExplicit()) {
         throw new IllegalStateException("object implicit - explicit expected.");
      } else {
         return (ASN1Object)(this.obj instanceof ASN1Object ? (ASN1Object)this.obj : this.obj.toASN1Primitive());
      }
   }

   public ASN1TaggedObject getExplicitBaseTagged() {
      if (!this.isExplicit()) {
         throw new IllegalStateException("object implicit - explicit expected.");
      } else {
         return checkedCast(this.obj.toASN1Primitive());
      }
   }

   public ASN1TaggedObject getImplicitBaseTagged(int var1, int var2) {
      if (var1 != 0 && (var1 & 192) == var1) {
         switch (this.explicitness) {
            case 1:
               throw new IllegalStateException("object explicit - implicit expected.");
            case 2:
               ASN1TaggedObject var3 = checkedCast(this.obj.toASN1Primitive());
               return ASN1Util.checkTag(var3, var1, var2);
            default:
               return this.replaceTag(var1, var2);
         }
      } else {
         throw new IllegalArgumentException("invalid base tag class: " + var1);
      }
   }

   public ASN1Primitive getBaseUniversal(boolean var1, int var2) {
      ASN1UniversalType var3 = ASN1UniversalTypes.get(var2);
      if (null == var3) {
         throw new IllegalArgumentException("unsupported UNIVERSAL tag number: " + var2);
      } else {
         return this.getBaseUniversal(var1, var3);
      }
   }

   ASN1Primitive getBaseUniversal(boolean var1, ASN1UniversalType var2) {
      if (var1) {
         if (!this.isExplicit()) {
            throw new IllegalStateException("object implicit - explicit expected.");
         } else {
            return var2.checkedCast(this.obj.toASN1Primitive());
         }
      } else if (1 == this.explicitness) {
         throw new IllegalStateException("object explicit - implicit expected.");
      } else {
         ASN1Primitive var3 = this.obj.toASN1Primitive();
         switch (this.explicitness) {
            case 3:
               return var2.fromImplicitConstructed(this.rebuildConstructed(var3));
            case 4:
               if (var3 instanceof ASN1Sequence) {
                  return var2.fromImplicitConstructed((ASN1Sequence)var3);
               }

               return var2.fromImplicitPrimitive((DEROctetString)var3);
            default:
               return var2.checkedCast(var3);
         }
      }
   }

   @Override
   public ASN1Encodable parseBaseUniversal(boolean var1, int var2) throws IOException {
      ASN1Primitive var3 = this.getBaseUniversal(var1, var2);
      switch (var2) {
         case 3:
            return ((ASN1BitString)var3).parser();
         case 4:
            return ((ASN1OctetString)var3).parser();
         case 16:
            return ((ASN1Sequence)var3).parser();
         case 17:
            return ((ASN1Set)var3).parser();
         default:
            return var3;
      }
   }

   @Override
   public ASN1Encodable parseExplicitBaseObject() throws IOException {
      return this.getExplicitBaseObject();
   }

   @Override
   public ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException {
      return this.getExplicitBaseTagged();
   }

   @Override
   public ASN1TaggedObjectParser parseImplicitBaseTagged(int var1, int var2) throws IOException {
      return this.getImplicitBaseTagged(var1, var2);
   }

   @Override
   public final ASN1Primitive getLoadedObject() {
      return this;
   }

   abstract ASN1Sequence rebuildConstructed(ASN1Primitive var1);

   abstract ASN1TaggedObject replaceTag(int var1, int var2);

   @Override
   ASN1Primitive toDERObject() {
      return new DERTaggedObject(this.explicitness, this.tagClass, this.tagNo, this.obj);
   }

   @Override
   ASN1Primitive toDLObject() {
      return new DLTaggedObject(this.explicitness, this.tagClass, this.tagNo, this.obj);
   }

   @Override
   public String toString() {
      return ASN1Util.getTagText(this.tagClass, this.tagNo) + this.obj;
   }

   static ASN1Primitive createConstructedDL(int var0, int var1, ASN1EncodableVector var2) {
      boolean var3 = var2.size() == 1;
      return var3 ? new DLTaggedObject(3, var0, var1, var2.get(0)) : new DLTaggedObject(4, var0, var1, DLFactory.createSequence(var2));
   }

   static ASN1Primitive createConstructedIL(int var0, int var1, ASN1EncodableVector var2) {
      boolean var3 = var2.size() == 1;
      return var3 ? new BERTaggedObject(3, var0, var1, var2.get(0)) : new BERTaggedObject(4, var0, var1, BERFactory.createSequence(var2));
   }

   static ASN1Primitive createPrimitive(int var0, int var1, byte[] var2) {
      return new DLTaggedObject(4, var0, var1, new DEROctetString(var2));
   }

   private static ASN1TaggedObject checkedCast(ASN1Primitive var0) {
      if (var0 instanceof ASN1TaggedObject) {
         return (ASN1TaggedObject)var0;
      } else {
         throw new IllegalStateException("unexpected object: " + var0.getClass().getName());
      }
   }
}
