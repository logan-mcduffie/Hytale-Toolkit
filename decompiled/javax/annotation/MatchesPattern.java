package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;
import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;

@Documented
@TypeQualifier(applicableTo = String.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchesPattern {
   @RegEx
   String value();

   int flags() default 0;

   public static class Checker implements TypeQualifierValidator<MatchesPattern> {
      public When forConstantValue(MatchesPattern annotation, Object value) {
         Pattern p = Pattern.compile(annotation.value(), annotation.flags());
         return p.matcher((String)value).matches() ? When.ALWAYS : When.NEVER;
      }
   }
}
