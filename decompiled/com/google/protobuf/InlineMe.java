package com.google.protobuf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@interface InlineMe {
   String replacement();

   String[] imports() default {};

   String[] staticImports() default {};
}
