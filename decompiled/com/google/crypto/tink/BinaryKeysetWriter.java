package com.google.crypto.tink;

import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.Keyset;
import com.google.errorprone.annotations.InlineMe;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class BinaryKeysetWriter implements KeysetWriter {
   private final OutputStream outputStream;

   private BinaryKeysetWriter(OutputStream stream) {
      this.outputStream = stream;
   }

   public static KeysetWriter withOutputStream(OutputStream stream) {
      return new BinaryKeysetWriter(stream);
   }

   @Deprecated
   @InlineMe(
      replacement = "BinaryKeysetWriter.withOutputStream(new FileOutputStream(file))",
      imports = {"com.google.crypto.tink.BinaryKeysetWriter", "java.io.FileOutputStream"}
   )
   public static KeysetWriter withFile(File file) throws IOException {
      return withOutputStream(new FileOutputStream(file));
   }

   @Override
   public void write(Keyset keyset) throws IOException {
      try {
         keyset.writeTo(this.outputStream);
      } finally {
         this.outputStream.close();
      }
   }

   @Override
   public void write(EncryptedKeyset keyset) throws IOException {
      try {
         keyset.toBuilder().clearKeysetInfo().build().writeTo(this.outputStream);
      } finally {
         this.outputStream.close();
      }
   }
}
