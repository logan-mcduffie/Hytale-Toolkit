package com.google.protobuf;

import java.nio.Buffer;

final class Java8Compatibility {
   static void clear(Buffer b) {
      b.clear();
   }

   static void flip(Buffer b) {
      b.flip();
   }

   static void limit(Buffer b, int limit) {
      b.limit(limit);
   }

   static void mark(Buffer b) {
      b.mark();
   }

   static void position(Buffer b, int position) {
      b.position(position);
   }

   static void reset(Buffer b) {
      b.reset();
   }

   private Java8Compatibility() {
   }
}
