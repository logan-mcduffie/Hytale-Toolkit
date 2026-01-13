package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

public abstract class ASN1Set extends ASN1Primitive implements Iterable<ASN1Encodable> {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Set.class, 17) {
      @Override
      ASN1Primitive fromImplicitConstructed(ASN1Sequence var1) {
         return var1.toASN1Set();
      }
   };
   protected final ASN1Encodable[] elements;
   protected ASN1Encodable[] sortedElements;

   public static ASN1Set getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1Set)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1Set) {
               return (ASN1Set)var1;
            }
         } else if (var0 instanceof byte[]) {
            try {
               return (ASN1Set)TYPE.fromByteArray((byte[])var0);
            } catch (IOException var2) {
               throw new IllegalArgumentException("failed to construct set from byte[]: " + var2.getMessage());
            }
         }

         throw new IllegalArgumentException("unknown object in getInstance: " + var0.getClass().getName());
      } else {
         return (ASN1Set)var0;
      }
   }

   public static ASN1Set getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Set)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1Set getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Set)TYPE.getTagged(var0, var1);
   }

   protected ASN1Set() {
      this.elements = ASN1EncodableVector.EMPTY_ELEMENTS;
      this.sortedElements = this.elements;
   }

   protected ASN1Set(ASN1Encodable var1) {
      if (null == var1) {
         throw new NullPointerException("'element' cannot be null");
      } else {
         this.elements = new ASN1Encodable[]{var1};
         this.sortedElements = this.elements;
      }
   }

   protected ASN1Set(ASN1EncodableVector var1, boolean var2) {
      if (null == var1) {
         throw new NullPointerException("'elementVector' cannot be null");
      } else {
         ASN1Encodable[] var3;
         if (var2 && var1.size() >= 2) {
            var3 = var1.copyElements();
            sort(var3);
         } else {
            var3 = var1.takeElements();
         }

         this.elements = var3;
         this.sortedElements = !var2 && var3.length >= 2 ? null : this.elements;
      }
   }

   protected ASN1Set(ASN1Encodable[] var1, boolean var2) {
      if (Arrays.isNullOrContainsNull(var1)) {
         throw new NullPointerException("'elements' cannot be null, or contain null");
      } else {
         ASN1Encodable[] var3 = ASN1EncodableVector.cloneElements(var1);
         if (var2 && var3.length >= 2) {
            sort(var3);
         }

         this.elements = var3;
         this.sortedElements = !var2 && var3.length >= 2 ? null : var1;
      }
   }

   ASN1Set(boolean var1, ASN1Encodable[] var2) {
      this.elements = var2;
      this.sortedElements = !var1 && var2.length >= 2 ? null : var2;
   }

   ASN1Set(ASN1Encodable[] var1, ASN1Encodable[] var2) {
      this.elements = var1;
      this.sortedElements = var2;
   }

   public Enumeration getObjects() {
      return new Enumeration() {
         private int pos = 0;

         @Override
         public boolean hasMoreElements() {
            return this.pos < ASN1Set.this.elements.length;
         }

         @Override
         public Object nextElement() {
            if (this.pos < ASN1Set.this.elements.length) {
               return ASN1Set.this.elements[this.pos++];
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   public ASN1Encodable getObjectAt(int var1) {
      return this.elements[var1];
   }

   public int size() {
      return this.elements.length;
   }

   public ASN1Encodable[] toArray() {
      return ASN1EncodableVector.cloneElements(this.elements);
   }

   public ASN1SetParser parser() {
      final int var1 = this.size();
      return new ASN1SetParser() {
         private int pos = 0;

         @Override
         public ASN1Encodable readObject() throws IOException {
            if (var1 == this.pos) {
               return null;
            } else {
               ASN1Encodable var1x = ASN1Set.this.elements[this.pos++];
               if (var1x instanceof ASN1Sequence) {
                  return ((ASN1Sequence)var1x).parser();
               } else {
                  return (ASN1Encodable)(var1x instanceof ASN1Set ? ((ASN1Set)var1x).parser() : var1x);
               }
            }
         }

         @Override
         public ASN1Primitive getLoadedObject() {
            return ASN1Set.this;
         }

         @Override
         public ASN1Primitive toASN1Primitive() {
            return ASN1Set.this;
         }
      };
   }

   @Override
   public int hashCode() {
      int var1 = this.elements.length;
      int var2 = var1 + 1;

      while (--var1 >= 0) {
         var2 += this.elements[var1].toASN1Primitive().hashCode();
      }

      return var2;
   }

   @Override
   ASN1Primitive toDERObject() {
      if (this.sortedElements == null) {
         this.sortedElements = (ASN1Encodable[])this.elements.clone();
         sort(this.sortedElements);
      }

      return new DERSet(true, this.sortedElements);
   }

   @Override
   ASN1Primitive toDLObject() {
      return new DLSet(this.elements, this.sortedElements);
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1Set)) {
         return false;
      } else {
         ASN1Set var2 = (ASN1Set)var1;
         int var3 = this.size();
         if (var2.size() != var3) {
            return false;
         } else {
            DERSet var4 = (DERSet)this.toDERObject();
            DERSet var5 = (DERSet)var2.toDERObject();

            for (int var6 = 0; var6 < var3; var6++) {
               ASN1Primitive var7 = var4.elements[var6].toASN1Primitive();
               ASN1Primitive var8 = var5.elements[var6].toASN1Primitive();
               if (var7 != var8 && !var7.asn1Equals(var8)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

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
      return new Arrays.Iterator<>(this.toArray());
   }

   private static byte[] getDEREncoded(ASN1Encodable var0) {
      try {
         return var0.toASN1Primitive().getEncoded("DER");
      } catch (IOException var2) {
         throw new IllegalArgumentException("cannot encode object added to SET");
      }
   }

   private static boolean lessThanOrEqual(byte[] var0, byte[] var1) {
      int var2 = var0[0] & 223;
      int var3 = var1[0] & 223;
      if (var2 != var3) {
         return var2 < var3;
      } else {
         int var4 = Math.min(var0.length, var1.length) - 1;

         for (int var5 = 1; var5 < var4; var5++) {
            if (var0[var5] != var1[var5]) {
               return (var0[var5] & 255) < (var1[var5] & 255);
            }
         }

         return (var0[var4] & 255) <= (var1[var4] & 255);
      }
   }

   private static void sort(ASN1Encodable[] var0) {
      int var1 = var0.length;
      if (var1 >= 2) {
         ASN1Encodable var2 = var0[0];
         ASN1Encodable var3 = var0[1];
         byte[] var4 = getDEREncoded(var2);
         byte[] var5 = getDEREncoded(var3);
         if (lessThanOrEqual(var5, var4)) {
            ASN1Encodable var6 = var3;
            var3 = var2;
            var2 = var6;
            byte[] var7 = var5;
            var5 = var4;
            var4 = var7;
         }

         for (int var12 = 2; var12 < var1; var12++) {
            ASN1Encodable var13 = var0[var12];
            byte[] var8 = getDEREncoded(var13);
            if (lessThanOrEqual(var5, var8)) {
               var0[var12 - 2] = var2;
               var2 = var3;
               var4 = var5;
               var3 = var13;
               var5 = var8;
            } else if (lessThanOrEqual(var4, var8)) {
               var0[var12 - 2] = var2;
               var2 = var13;
               var4 = var8;
            } else {
               int var9 = var12 - 1;

               while (--var9 > 0) {
                  ASN1Encodable var10 = var0[var9 - 1];
                  byte[] var11 = getDEREncoded(var10);
                  if (lessThanOrEqual(var11, var8)) {
                     break;
                  }

                  var0[var9] = var10;
               }

               var0[var9] = var13;
            }
         }

         var0[var1 - 2] = var2;
         var0[var1 - 1] = var3;
      }
   }
}
