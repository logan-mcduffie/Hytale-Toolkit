package com.google.errorprone.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface RestrictedApi {
   String explanation();

   String link() default "";

   String allowedOnPath() default "";

   Class<? extends Annotation>[] allowlistAnnotations() default {};

   Class<? extends Annotation>[] allowlistWithWarningAnnotations() default {};
}
