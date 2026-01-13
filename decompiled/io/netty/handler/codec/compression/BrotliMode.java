package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.Encoder.Mode;

public enum BrotliMode {
   GENERIC,
   TEXT,
   FONT;

   Mode adapt() {
      switch (this) {
         case GENERIC:
            return Mode.GENERIC;
         case TEXT:
            return Mode.TEXT;
         case FONT:
            return Mode.FONT;
         default:
            throw new IllegalStateException("Unsupported enum value: " + this);
      }
   }
}
