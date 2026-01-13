package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;

@Documented
@TypeQualifier(applicableTo = Number.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnegative {
   When when() default When.ALWAYS;

   public static class Checker implements TypeQualifierValidator<Nonnegative> {
      public When forConstantValue(Nonnegative annotation, Object v) {
         if (!(v instanceof Number)) {
            return When.NEVER;
         } else {
            Number value = (Number)v;
            boolean isNegative;
            if (value instanceof Long) {
               isNegative = value.longValue() < 0L;
            } else if (value instanceof Double) {
               isNegative = value.doubleValue() < 0.0;
            } else if (value instanceof Float) {
               isNegative = value.floatValue() < 0.0F;
            } else {
               isNegative = value.intValue() < 0;
            }

            return isNegative ? When.NEVER : When.ALWAYS;
         }
      }
   }
}
