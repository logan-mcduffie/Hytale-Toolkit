package io.sentry.config;

import java.util.Properties;
import org.jetbrains.annotations.Nullable;

interface PropertiesLoader {
   @Nullable
   Properties load();
}
