package io.netty.handler.codec.http3;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.meta.TypeQualifierDefault;
import org.jetbrains.annotations.NotNull;

@Documented
@TypeQualifierDefault({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PACKAGE)
@NotNull
@interface NotNullByDefault {
}
