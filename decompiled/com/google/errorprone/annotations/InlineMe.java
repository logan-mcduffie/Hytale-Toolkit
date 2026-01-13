package com.google.errorprone.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface InlineMe {
   String replacement();

   String[] imports() default {};

   String[] staticImports() default {};
}
