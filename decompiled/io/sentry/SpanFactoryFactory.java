package io.sentry;

import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SpanFactoryFactory {
   private static final String OTEL_SPAN_FACTORY = "io.sentry.opentelemetry.OtelSpanFactory";

   @NotNull
   public static ISpanFactory create(@NotNull LoadClass loadClass, @NotNull ILogger logger) {
      if (Platform.isJvm() && loadClass.isClassAvailable("io.sentry.opentelemetry.OtelSpanFactory", logger)) {
         Class<?> otelSpanFactoryClazz = loadClass.loadClass("io.sentry.opentelemetry.OtelSpanFactory", logger);
         if (otelSpanFactoryClazz != null) {
            try {
               Object otelSpanFactory = otelSpanFactoryClazz.getDeclaredConstructor().newInstance();
               if (otelSpanFactory != null && otelSpanFactory instanceof ISpanFactory) {
                  return (ISpanFactory)otelSpanFactory;
               }
            } catch (InstantiationException var4) {
            } catch (IllegalAccessException var5) {
            } catch (InvocationTargetException var6) {
            } catch (NoSuchMethodException var7) {
            }
         }
      }

      return new DefaultSpanFactory();
   }
}
