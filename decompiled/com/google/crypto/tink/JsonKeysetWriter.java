package com.google.crypto.tink;

import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.proto.KeysetInfo;
import com.google.crypto.tink.subtle.Base64;
import com.google.errorprone.annotations.InlineMe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

@Deprecated
public final class JsonKeysetWriter implements KeysetWriter {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   private final OutputStream outputStream;

   private JsonKeysetWriter(OutputStream stream) {
      this.outputStream = stream;
   }

   public static KeysetWriter withOutputStream(OutputStream stream) {
      return new JsonKeysetWriter(stream);
   }

   @Deprecated
   @InlineMe(
      replacement = "JsonKeysetWriter.withOutputStream(new FileOutputStream(file))",
      imports = {"com.google.crypto.tink.JsonKeysetWriter", "java.io.FileOutputStream"}
   )
   public static KeysetWriter withFile(File file) throws IOException {
      return withOutputStream(new FileOutputStream(file));
   }

   @Deprecated
   @InlineMe(
      replacement = "JsonKeysetWriter.withOutputStream(new FileOutputStream(new File(path)))",
      imports = {"com.google.crypto.tink.JsonKeysetWriter", "java.io.File", "java.io.FileOutputStream"}
   )
   public static KeysetWriter withPath(String path) throws IOException {
      return withOutputStream(new FileOutputStream(new File(path)));
   }

   @Deprecated
   @InlineMe(
      replacement = "JsonKeysetWriter.withOutputStream(new FileOutputStream(path.toFile()))",
      imports = {"com.google.crypto.tink.JsonKeysetWriter", "java.io.FileOutputStream"}
   )
   public static KeysetWriter withPath(Path path) throws IOException {
      return withOutputStream(new FileOutputStream(path.toFile()));
   }

   @Override
   public void write(Keyset keyset) throws IOException {
      try {
         this.outputStream.write(this.toJson(keyset).toString().getBytes(UTF_8));
         this.outputStream.write(System.lineSeparator().getBytes(UTF_8));
      } catch (JsonParseException var6) {
         throw new IOException(var6);
      } finally {
         this.outputStream.close();
      }
   }

   @Override
   public void write(EncryptedKeyset keyset) throws IOException {
      this.outputStream.write(this.toJson(keyset).toString().getBytes(UTF_8));
      this.outputStream.write(System.lineSeparator().getBytes(UTF_8));
      this.outputStream.close();
   }

   private long toUnsignedLong(int x) {
      return x & 4294967295L;
   }

   private JsonObject toJson(Keyset keyset) {
      JsonObject json = new JsonObject();
      json.addProperty("primaryKeyId", this.toUnsignedLong(keyset.getPrimaryKeyId()));
      JsonArray keys = new JsonArray();

      for (Keyset.Key key : keyset.getKeyList()) {
         keys.add(this.toJson(key));
      }

      json.add("key", keys);
      return json;
   }

   private JsonObject toJson(Keyset.Key key) {
      JsonObject json = new JsonObject();
      json.add("keyData", this.toJson(key.getKeyData()));
      json.addProperty("status", key.getStatus().name());
      json.addProperty("keyId", this.toUnsignedLong(key.getKeyId()));
      json.addProperty("outputPrefixType", key.getOutputPrefixType().name());
      return json;
   }

   private JsonObject toJson(KeyData keyData) {
      JsonObject json = new JsonObject();
      json.addProperty("typeUrl", keyData.getTypeUrl());
      json.addProperty("value", Base64.encode(keyData.getValue().toByteArray()));
      json.addProperty("keyMaterialType", keyData.getKeyMaterialType().name());
      return json;
   }

   private JsonObject toJson(EncryptedKeyset keyset) {
      JsonObject json = new JsonObject();
      json.addProperty("encryptedKeyset", Base64.encode(keyset.getEncryptedKeyset().toByteArray()));
      json.add("keysetInfo", this.toJson(keyset.getKeysetInfo()));
      return json;
   }

   private JsonObject toJson(KeysetInfo keysetInfo) {
      JsonObject json = new JsonObject();
      json.addProperty("primaryKeyId", this.toUnsignedLong(keysetInfo.getPrimaryKeyId()));
      JsonArray keyInfos = new JsonArray();

      for (KeysetInfo.KeyInfo keyInfo : keysetInfo.getKeyInfoList()) {
         keyInfos.add(this.toJson(keyInfo));
      }

      json.add("keyInfo", keyInfos);
      return json;
   }

   private JsonObject toJson(KeysetInfo.KeyInfo keyInfo) {
      JsonObject json = new JsonObject();
      json.addProperty("typeUrl", keyInfo.getTypeUrl());
      json.addProperty("status", keyInfo.getStatus().name());
      json.addProperty("keyId", this.toUnsignedLong(keyInfo.getKeyId()));
      json.addProperty("outputPrefixType", keyInfo.getOutputPrefixType().name());
      return json;
   }
}
