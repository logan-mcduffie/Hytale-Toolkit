package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.subtle.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Immutable
public final class LegacyFullVerify implements PublicKeyVerify {
   private final PublicKeyVerify rawVerifier;
   private final byte[] outputPrefix;
   private final byte[] messageSuffix;

   public static PublicKeyVerify create(LegacyProtoKey key) throws GeneralSecurityException {
      ProtoKeySerialization protoKeySerialization = key.getSerialization(InsecureSecretKeyAccess.get());
      KeyManager<PublicKeyVerify> manager = KeyManagerRegistry.globalInstance().getKeyManager(protoKeySerialization.getTypeUrl(), PublicKeyVerify.class);
      PublicKeyVerify rawVerifier = manager.getPrimitive(protoKeySerialization.getValue());
      return new LegacyFullVerify(rawVerifier, getOutputPrefix(protoKeySerialization), getMessageSuffix(protoKeySerialization));
   }

   static byte[] getOutputPrefix(ProtoKeySerialization key) throws GeneralSecurityException {
      switch (key.getOutputPrefixType()) {
         case LEGACY:
         case CRUNCHY:
            return OutputPrefixUtil.getLegacyOutputPrefix(key.getIdRequirementOrNull()).toByteArray();
         case TINK:
            return OutputPrefixUtil.getTinkOutputPrefix(key.getIdRequirementOrNull()).toByteArray();
         case RAW:
            return OutputPrefixUtil.EMPTY_PREFIX.toByteArray();
         default:
            throw new GeneralSecurityException("unknown output prefix type");
      }
   }

   static byte[] getMessageSuffix(ProtoKeySerialization key) {
      return key.getOutputPrefixType().equals(OutputPrefixType.LEGACY) ? new byte[]{0} : new byte[0];
   }

   private LegacyFullVerify(PublicKeyVerify rawVerifier, byte[] outputPrefix, byte[] messageSuffix) {
      this.rawVerifier = rawVerifier;
      this.outputPrefix = outputPrefix;
      this.messageSuffix = messageSuffix;
   }

   @Override
   public void verify(byte[] signature, byte[] data) throws GeneralSecurityException {
      if (this.outputPrefix.length == 0 && this.messageSuffix.length == 0) {
         this.rawVerifier.verify(signature, data);
      } else if (!Util.isPrefix(this.outputPrefix, signature)) {
         throw new GeneralSecurityException("Invalid signature (output prefix mismatch)");
      } else {
         byte[] dataCopy = data;
         if (this.messageSuffix.length != 0) {
            dataCopy = Bytes.concat(data, this.messageSuffix);
         }

         byte[] signatureNoPrefix = Arrays.copyOfRange(signature, this.outputPrefix.length, signature.length);
         this.rawVerifier.verify(signatureNoPrefix, dataCopy);
      }
   }
}
