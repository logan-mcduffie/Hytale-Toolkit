package io.netty.handler.codec.http3;

import io.netty.util.internal.ObjectUtil;

class QpackHeaderField {
   static final int ENTRY_OVERHEAD = 32;
   final CharSequence name;
   final CharSequence value;

   static long sizeOf(CharSequence name, CharSequence value) {
      return name.length() + value.length() + 32;
   }

   QpackHeaderField(CharSequence name, CharSequence value) {
      this.name = ObjectUtil.checkNotNull(name, "name");
      this.value = ObjectUtil.checkNotNull(value, "value");
   }

   long size() {
      return sizeOf(this.name, this.value);
   }

   @Override
   public String toString() {
      return this.name + ": " + this.value;
   }
}
