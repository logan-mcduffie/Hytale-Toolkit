package com.google.crypto.tink.daead.internal;

import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.subtle.Bytes;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class LegacyFullDeterministicAead implements DeterministicAead {
   private final DeterministicAead rawDaead;
   private final OutputPrefixType outputPrefixType;
   private final byte[] identifier;

   public static DeterministicAead create(LegacyProtoKey key) throws GeneralSecurityException {
      ProtoKeySerialization protoKeySerialization = key.getSerialization(InsecureSecretKeyAccess.get());
      KeyManager<DeterministicAead> manager = KeyManagerRegistry.globalInstance().getKeyManager(protoKeySerialization.getTypeUrl(), DeterministicAead.class);
      DeterministicAead rawPrimitive = manager.getPrimitive(protoKeySerialization.getValue());
      OutputPrefixType outputPrefixType = protoKeySerialization.getOutputPrefixType();
      byte[] identifier;
      switch (outputPrefixType) {
         case RAW:
            identifier = OutputPrefixUtil.EMPTY_PREFIX.toByteArray();
            break;
         case LEGACY:
         case CRUNCHY:
            identifier = OutputPrefixUtil.getLegacyOutputPrefix(key.getIdRequirementOrNull()).toByteArray();
            break;
         case TINK:
            identifier = OutputPrefixUtil.getTinkOutputPrefix(key.getIdRequirementOrNull()).toByteArray();
            break;
         default:
            throw new GeneralSecurityException("unknown output prefix type " + outputPrefixType.getNumber());
      }

      return new LegacyFullDeterministicAead(rawPrimitive, outputPrefixType, identifier);
   }

   private LegacyFullDeterministicAead(DeterministicAead rawDaead, OutputPrefixType outputPrefixType, byte[] identifier) {
      this.rawDaead = rawDaead;
      this.outputPrefixType = outputPrefixType;
      this.identifier = identifier;
   }

   @Override
   public byte[] encryptDeterministically(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      return this.outputPrefixType == OutputPrefixType.RAW
         ? this.rawDaead.encryptDeterministically(plaintext, associatedData)
         : Bytes.concat(this.identifier, this.rawDaead.encryptDeterministically(plaintext, associatedData));
   }

   @Override
   public byte[] decryptDeterministically(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      if (this.outputPrefixType == OutputPrefixType.RAW) {
         return this.rawDaead.decryptDeterministically(ciphertext, associatedData);
      } else if (!Util.isPrefix(this.identifier, ciphertext)) {
         throw new GeneralSecurityException("wrong prefix");
      } else {
         return this.rawDaead.decryptDeterministically(Arrays.copyOfRange(ciphertext, 5, ciphertext.length), associatedData);
      }
   }
}
