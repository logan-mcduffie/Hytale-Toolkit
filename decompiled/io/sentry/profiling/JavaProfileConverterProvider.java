package io.sentry.profiling;

import io.sentry.IProfileConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface JavaProfileConverterProvider {
   @NotNull
   IProfileConverter getProfileConverter();
}
