package org.jline.reader.impl;

import java.util.Objects;
import org.jline.reader.MaskingCallback;

public final class SimpleMaskingCallback implements MaskingCallback {
   private final Character mask;

   public SimpleMaskingCallback(Character mask) {
      this.mask = Objects.requireNonNull(mask, "mask must be a non null character");
   }

   @Override
   public String display(String line) {
      if (this.mask.equals('\u0000')) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder(line.length());
         int i = line.length();

         while (i-- > 0) {
            sb.append(this.mask.charValue());
         }

         return sb.toString();
      }
   }

   @Override
   public String history(String line) {
      return null;
   }
}
