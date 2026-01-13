package com.google.crypto.tink.internal;

import com.google.crypto.tink.util.Bytes;
import java.nio.ByteBuffer;

public final class OutputPrefixUtil {
   public static final int NON_EMPTY_PREFIX_SIZE = 5;
   public static final byte LEGACY_START_BYTE = 0;
   public static final byte TINK_START_BYTE = 1;
   public static final Bytes EMPTY_PREFIX = Bytes.copyFrom(new byte[0]);

   public static final Bytes getLegacyOutputPrefix(int keyId) {
      return Bytes.copyFrom(ByteBuffer.allocate(5).put((byte)0).putInt(keyId).array());
   }

   public static final Bytes getTinkOutputPrefix(int keyId) {
      return Bytes.copyFrom(ByteBuffer.allocate(5).put((byte)1).putInt(keyId).array());
   }

   private OutputPrefixUtil() {
   }
}
