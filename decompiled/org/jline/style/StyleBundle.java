package org.jline.style;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface StyleBundle {
   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.METHOD)
   @Documented
   public @interface DefaultStyle {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.TYPE)
   @Documented
   public @interface StyleGroup {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.METHOD)
   @Documented
   public @interface StyleName {
      String value();
   }
}
