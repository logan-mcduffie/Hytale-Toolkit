package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;

@Documented
@Syntax("RegEx")
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface RegEx {
   When when() default When.ALWAYS;

   public static class Checker implements TypeQualifierValidator<RegEx> {
      public When forConstantValue(RegEx annotation, Object value) {
         if (!(value instanceof String)) {
            return When.NEVER;
         } else {
            try {
               Pattern.compile((String)value);
            } catch (PatternSyntaxException var4) {
               return When.NEVER;
            }

            return When.ALWAYS;
         }
      }
   }
}
