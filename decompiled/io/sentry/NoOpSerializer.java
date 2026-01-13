package io.sentry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NoOpSerializer implements ISerializer {
   private static final NoOpSerializer instance = new NoOpSerializer();

   public static NoOpSerializer getInstance() {
      return instance;
   }

   private NoOpSerializer() {
   }

   @Nullable
   @Override
   public <T, R> T deserializeCollection(@NotNull Reader reader, @NotNull Class<T> clazz, @Nullable JsonDeserializer<R> elementDeserializer) {
      return null;
   }

   @Nullable
   @Override
   public <T> T deserialize(@NotNull Reader reader, @NotNull Class<T> clazz) {
      return null;
   }

   @Nullable
   @Override
   public SentryEnvelope deserializeEnvelope(@NotNull InputStream inputStream) {
      return null;
   }

   @Override
   public <T> void serialize(@NotNull T entity, @NotNull Writer writer) throws IOException {
   }

   @Override
   public void serialize(@NotNull SentryEnvelope envelope, @NotNull OutputStream outputStream) throws Exception {
   }

   @NotNull
   @Override
   public String serialize(@NotNull Map<String, Object> data) throws Exception {
      return "";
   }
}
