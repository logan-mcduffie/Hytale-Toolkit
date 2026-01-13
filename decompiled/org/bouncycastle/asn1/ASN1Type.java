package org.bouncycastle.asn1;

abstract class ASN1Type {
   final Class javaClass;

   ASN1Type(Class var1) {
      this.javaClass = var1;
   }

   final Class getJavaClass() {
      return this.javaClass;
   }

   @Override
   public final boolean equals(Object var1) {
      return this == var1;
   }

   @Override
   public final int hashCode() {
      return super.hashCode();
   }
}
