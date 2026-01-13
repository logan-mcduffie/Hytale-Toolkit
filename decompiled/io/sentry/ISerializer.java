package io.sentry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISerializer {
   @Nullable
   <T, R> T deserializeCollection(@NotNull Reader var1, @NotNull Class<T> var2, @Nullable JsonDeserializer<R> var3);

   @Nullable
   <T> T deserialize(@NotNull Reader var1, @NotNull Class<T> var2);

   @Nullable
   SentryEnvelope deserializeEnvelope(@NotNull InputStream var1);

   <T> void serialize(@NotNull T var1, @NotNull Writer var2) throws IOException;

   void serialize(@NotNull SentryEnvelope var1, @NotNull OutputStream var2) throws Exception;

   @NotNull
   String serialize(@NotNull Map<String, Object> var1) throws Exception;
}
