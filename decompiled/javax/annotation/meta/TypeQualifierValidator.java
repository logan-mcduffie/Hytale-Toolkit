package javax.annotation.meta;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;

public interface TypeQualifierValidator<A extends Annotation> {
   @Nonnull
   When forConstantValue(@Nonnull A var1, Object var2);
}
