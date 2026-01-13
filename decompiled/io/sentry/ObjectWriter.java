package io.sentry;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectWriter {
   ObjectWriter beginArray() throws IOException;

   ObjectWriter endArray() throws IOException;

   ObjectWriter beginObject() throws IOException;

   ObjectWriter endObject() throws IOException;

   ObjectWriter name(@NotNull String var1) throws IOException;

   ObjectWriter value(@Nullable String var1) throws IOException;

   ObjectWriter jsonValue(@Nullable String var1) throws IOException;

   ObjectWriter nullValue() throws IOException;

   ObjectWriter value(boolean var1) throws IOException;

   ObjectWriter value(@Nullable Boolean var1) throws IOException;

   ObjectWriter value(double var1) throws IOException;

   ObjectWriter value(long var1) throws IOException;

   ObjectWriter value(@Nullable Number var1) throws IOException;

   ObjectWriter value(@NotNull ILogger var1, @Nullable Object var2) throws IOException;

   void setLenient(boolean var1);

   void setIndent(@Nullable String var1);

   @Nullable
   String getIndent();
}
