package io.sentry;

import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IEnvelopeReader {
   @Nullable
   SentryEnvelope read(@NotNull InputStream var1) throws IOException;
}
