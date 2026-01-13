package com.google.crypto.tink;

import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.proto.Keyset;
import java.security.GeneralSecurityException;

public final class CryptoFormat {
   public static final int NON_RAW_PREFIX_SIZE = 5;
   public static final int LEGACY_PREFIX_SIZE = 5;
   public static final byte LEGACY_START_BYTE = 0;
   public static final int TINK_PREFIX_SIZE = 5;
   public static final byte TINK_START_BYTE = 1;
   public static final int RAW_PREFIX_SIZE = 0;
   public static final byte[] RAW_PREFIX = new byte[0];

   public static byte[] getOutputPrefix(Keyset.Key key) throws GeneralSecurityException {
      switch (key.getOutputPrefixType()) {
         case LEGACY:
         case CRUNCHY:
            return OutputPrefixUtil.getLegacyOutputPrefix(key.getKeyId()).toByteArray();
         case TINK:
            return OutputPrefixUtil.getTinkOutputPrefix(key.getKeyId()).toByteArray();
         case RAW:
            return RAW_PREFIX;
         default:
            throw new GeneralSecurityException("unknown output prefix type");
      }
   }

   private CryptoFormat() {
   }
}
