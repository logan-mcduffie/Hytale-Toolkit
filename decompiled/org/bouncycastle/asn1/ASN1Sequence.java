package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

public abstract class ASN1Sequence extends ASN1Primitive implements Iterable<ASN1Encodable> {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Sequence.class, 16) {
      @Override
      ASN1Primitive fromImplicitConstructed(ASN1Sequence var1) {
         return var1;
      }
   };
   ASN1Encodable[] elements;

   public static ASN1Sequence getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1Sequence)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1Sequence) {
               return (ASN1Sequence)var1;
            }
         } else if (var0 instanceof byte[]) {
            try {
               return (ASN1Sequence)TYPE.fromByteArray((byte[])var0);
            } catch (IOException var2) {
               throw new IllegalArgumentException("failed to construct sequence from byte[]: " + var2.getMessage());
            }
         }

         throw new IllegalArgumentException("unknown object in getInstance: " + var0.getClass().getName());
      } else {
         return (ASN1Sequence)var0;
      }
   }

   public static ASN1Sequence getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Sequence)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1Sequence getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Sequence)TYPE.getTagged(var0, var1);
   }

   protected ASN1Sequence() {
      this.elements = ASN1EncodableVector.EMPTY_ELEMENTS;
   }

   protected ASN1Sequence(ASN1Encodable var1) {
      if (null == var1) {
         throw new NullPointerException("'element' cannot be null");
      } else {
         this.elements = new ASN1Encodable[]{var1};
      }
   }

   protected ASN1Sequence(ASN1Encodable var1, ASN1Encodable var2) {
      if (null == var1) {
         throw new NullPointerException("'element1' cannot be null");
      } else if (null == var2) {
         throw new NullPointerException("'element2' cannot be null");
      } else {
         this.elements = new ASN1Encodable[]{var1, var2};
      }
   }

   protected ASN1Sequence(ASN1EncodableVector var1) {
      if (null == var1) {
         throw new NullPointerException("'elementVector' cannot be null");
      } else {
         this.elements = var1.takeElements();
      }
   }

   protected ASN1Sequence(ASN1Encodable[] var1) {
      if (Arrays.isNullOrContainsNull(var1)) {
         throw new NullPointerException("'elements' cannot be null, or contain null");
      } else {
         this.elements = ASN1EncodableVector.cloneElements(var1);
      }
   }

   ASN1Sequence(ASN1Encodable[] var1, boolean var2) {
      this.elements = var2 ? ASN1EncodableVector.cloneElements(var1) : var1;
   }

   public ASN1Encodable[] toArray() {
      return ASN1EncodableVector.cloneElements(this.elements);
   }

   ASN1Encodable[] toArrayInternal() {
      return this.elements;
   }

   public Enumeration getObjects() {
      return new Enumeration() {
         private int pos = 0;

         @Override
         public boolean hasMoreElements() {
            return this.pos < ASN1Sequence.this.elements.length;
         }

         @Override
         public Object nextElement() {
            if (this.pos < ASN1Sequence.this.elements.length) {
               return ASN1Sequence.this.elements[this.pos++];
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   public ASN1SequenceParser parser() {
      final int var1 = this.size();
      return new ASN1SequenceParser() {
         private int pos = 0;

         @Override
         public ASN1Encodable readObject() throws IOException {
            if (var1 == this.pos) {
               return null;
            } else {
               ASN1Encodable var1x = ASN1Sequence.this.elements[this.pos++];
               if (var1x instanceof ASN1Sequence) {
                  return ((ASN1Sequence)var1x).parser();
               } else {
                  return (ASN1Encodable)(var1x instanceof ASN1Set ? ((ASN1Set)var1x).parser() : var1x);
               }
            }
         }

         @Override
         public ASN1Primitive getLoadedObject() {
            return ASN1Sequence.this;
         }

         @Override
         public ASN1Primitive toASN1Primitive() {
            return ASN1Sequence.this;
         }
      };
   }

   public ASN1Encodable getObjectAt(int var1) {
      return this.elements[var1];
   }

   public int size() {
      return this.elements.length;
   }

   @Override
   public int hashCode() {
      int var1 = this.elements.length;
      int var2 = var1 + 1;

      while (--var1 >= 0) {
         var2 *= 257;
         var2 ^= this.elements[var1].toASN1Primitive().hashCode();
      }

      return var2;
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1Sequence)) {
         return false;
      } else {
         ASN1Sequence var2 = (ASN1Sequence)var1;
         int var3 = this.size();
         if (var2.size() != var3) {
            return false;
         } else {
            for (int var4 = 0; var4 < var3; var4++) {
               ASN1Primitive var5 = this.elements[var4].toASN1Primitive();
               ASN1Primitive var6 = var2.elements[var4].toASN1Primitive();
               if (var5 != var6 && !var5.asn1Equals(var6)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   ASN1Primitive toDERObject() {
      return new DERSequence(this.elements, false);
   }

   @Override
   ASN1Primitive toDLObject() {
      return new DLSequence(this.elements, false);
   }

   abstract ASN1BitString toASN1BitString();

   abstract ASN1External toASN1External();

   abstract ASN1OctetString toASN1OctetString();

   abstract ASN1Set toASN1Set();

   @Override
   boolean encodeConstructed() {
      return true;
   }

   @Override
   public String toString() {
      int var1 = this.size();
      if (0 == var1) {
         return "[]";
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append('[');
         int var3 = 0;

         while (true) {
            var2.append(this.elements[var3]);
            if (++var3 >= var1) {
               var2.append(']');
               return var2.toString();
            }

            var2.append(", ");
         }
      }
   }

   @Override
   public Iterator<ASN1Encodable> iterator() {
      return new Arrays.Iterator<>(this.elements);
   }

   ASN1BitString[] getConstructedBitStrings() {
      int var1 = this.size();
      ASN1BitString[] var2 = new ASN1BitString[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = ASN1BitString.getInstance(this.elements[var3]);
      }

      return var2;
   }

   ASN1OctetString[] getConstructedOctetStrings() {
      int var1 = this.size();
      ASN1OctetString[] var2 = new ASN1OctetString[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = ASN1OctetString.getInstance(this.elements[var3]);
      }

      return var2;
   }
}
