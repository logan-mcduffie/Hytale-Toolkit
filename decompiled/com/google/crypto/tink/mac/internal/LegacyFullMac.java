package com.google.crypto.tink.mac.internal;

import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.subtle.Bytes;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public final class LegacyFullMac implements Mac {
   private static final byte[] formatVersion = new byte[]{0};
   static final int MIN_TAG_SIZE_IN_BYTES = 10;
   private final Mac rawMac;
   private final OutputPrefixType outputPrefixType;
   private final byte[] identifier;

   public static Mac create(LegacyProtoKey key) throws GeneralSecurityException {
      ProtoKeySerialization protoKeySerialization = key.getSerialization(InsecureSecretKeyAccess.get());
      KeyManager<Mac> manager = KeyManagerRegistry.globalInstance().getKeyManager(protoKeySerialization.getTypeUrl(), Mac.class);
      Mac rawPrimitive = manager.getPrimitive(protoKeySerialization.getValue());
      OutputPrefixType outputPrefixType = protoKeySerialization.getOutputPrefixType();
      byte[] outputPrefix;
      switch (outputPrefixType) {
         case RAW:
            outputPrefix = OutputPrefixUtil.EMPTY_PREFIX.toByteArray();
            break;
         case LEGACY:
         case CRUNCHY:
            outputPrefix = OutputPrefixUtil.getLegacyOutputPrefix(key.getIdRequirementOrNull()).toByteArray();
            break;
         case TINK:
            outputPrefix = OutputPrefixUtil.getTinkOutputPrefix(key.getIdRequirementOrNull()).toByteArray();
            break;
         default:
            throw new GeneralSecurityException("unknown output prefix type");
      }

      return new LegacyFullMac(rawPrimitive, outputPrefixType, outputPrefix);
   }

   private LegacyFullMac(Mac rawMac, OutputPrefixType outputPrefixType, byte[] identifier) {
      this.rawMac = rawMac;
      this.outputPrefixType = outputPrefixType;
      this.identifier = identifier;
   }

   @Override
   public byte[] computeMac(byte[] data) throws GeneralSecurityException {
      byte[] data2 = data;
      if (this.outputPrefixType.equals(OutputPrefixType.LEGACY)) {
         data2 = Bytes.concat(data, formatVersion);
      }

      return Bytes.concat(this.identifier, this.rawMac.computeMac(data2));
   }

   @Override
   public void verifyMac(byte[] mac, byte[] data) throws GeneralSecurityException {
      if (mac.length < 10) {
         throw new GeneralSecurityException("tag too short");
      } else {
         byte[] data2 = data;
         if (this.outputPrefixType.equals(OutputPrefixType.LEGACY)) {
            data2 = Bytes.concat(data, formatVersion);
         }

         byte[] prefix = new byte[0];
         byte[] macNoPrefix = mac;
         if (!this.outputPrefixType.equals(OutputPrefixType.RAW)) {
            prefix = Arrays.copyOf(mac, 5);
            macNoPrefix = Arrays.copyOfRange(mac, 5, mac.length);
         }

         if (!Arrays.equals(this.identifier, prefix)) {
            throw new GeneralSecurityException("wrong prefix");
         } else {
            this.rawMac.verifyMac(macNoPrefix, data2);
         }
      }
   }
}
