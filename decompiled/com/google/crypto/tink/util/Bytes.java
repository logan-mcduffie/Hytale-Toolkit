package com.google.crypto.tink.util;

import com.google.crypto.tink.subtle.Hex;
import com.google.errorprone.annotations.Immutable;
import java.util.Arrays;

@Immutable
public final class Bytes {
   private final byte[] data;

   public static Bytes copyFrom(final byte[] data) {
      if (data == null) {
         throw new NullPointerException("data must be non-null");
      } else {
         return copyFrom(data, 0, data.length);
      }
   }

   public static Bytes copyFrom(byte[] data, int start, int len) {
      if (data == null) {
         throw new NullPointerException("data must be non-null");
      } else {
         if (start + len > data.length) {
            len = data.length - start;
         }

         return new Bytes(data, start, len);
      }
   }

   public byte[] toByteArray() {
      byte[] result = new byte[this.data.length];
      System.arraycopy(this.data, 0, result, 0, this.data.length);
      return result;
   }

   public int size() {
      return this.data.length;
   }

   private Bytes(final byte[] buf, final int start, final int len) {
      this.data = new byte[len];
      System.arraycopy(buf, start, this.data, 0, len);
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Bytes)) {
         return false;
      } else {
         Bytes other = (Bytes)o;
         return Arrays.equals(other.data, this.data);
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   @Override
   public String toString() {
      return "Bytes(" + Hex.encode(this.data) + ")";
   }
}
