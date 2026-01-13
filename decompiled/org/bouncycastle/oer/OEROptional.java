package org.bouncycastle.oer;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.bouncycastle.asn1.ASN1Absent;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class OEROptional extends ASN1Object {
   public static final OEROptional ABSENT = new OEROptional(false, null);
   private final boolean defined;
   private final ASN1Encodable value;

   private OEROptional(boolean var1, ASN1Encodable var2) {
      this.defined = var1;
      this.value = var2;
   }

   public static OEROptional getInstance(Object var0) {
      if (var0 instanceof OEROptional) {
         return (OEROptional)var0;
      } else {
         return var0 instanceof ASN1Encodable ? new OEROptional(true, (ASN1Encodable)var0) : ABSENT;
      }
   }

   public static <T> T getValue(Class<T> var0, Object var1) {
      OEROptional var2 = getInstance(var1);
      return !var2.defined ? null : var2.getObject(var0);
   }

   public <T> T getObject(final Class<T> var1) {
      if (this.defined) {
         return (T)(this.value.getClass().isInstance(var1) ? var1.cast(this.value) : AccessController.doPrivileged(new PrivilegedAction<T>() {
            @Override
            public T run() {
               try {
                  Method var1x = var1.getMethod("getInstance", Object.class);
                  return (T)var1.cast(var1x.invoke(null, OEROptional.this.value));
               } catch (Exception var2) {
                  throw new IllegalStateException("could not invoke getInstance on type " + var2.getMessage(), var2);
               }
            }
         }));
      } else {
         return null;
      }
   }

   public ASN1Encodable get() {
      return (ASN1Encodable)(!this.defined ? ABSENT : this.value);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(!this.defined ? ASN1Absent.INSTANCE : this.get().toASN1Primitive());
   }

   public boolean isDefined() {
      return this.defined;
   }

   @Override
   public String toString() {
      return this.defined ? "OPTIONAL(" + this.value + ")" : "ABSENT";
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null || this.getClass() != var1.getClass()) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         OEROptional var2 = (OEROptional)var1;
         if (this.defined != var2.defined) {
            return false;
         } else {
            return this.value != null ? this.value.equals(var2.value) : var2.value == null;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + (this.defined ? 1 : 0);
      return 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
   }
}
