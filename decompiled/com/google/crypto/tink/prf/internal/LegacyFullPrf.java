package com.google.crypto.tink.prf.internal;

import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.prf.Prf;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public class LegacyFullPrf implements Prf {
   private final Prf rawPrf;

   public static Prf create(LegacyProtoKey key) throws GeneralSecurityException {
      ProtoKeySerialization protoKeySerialization = key.getSerialization(InsecureSecretKeyAccess.get());
      KeyManager<Prf> manager = KeyManagerRegistry.globalInstance().getKeyManager(protoKeySerialization.getTypeUrl(), Prf.class);
      return new LegacyFullPrf(manager.getPrimitive(protoKeySerialization.getValue()));
   }

   private LegacyFullPrf(Prf rawPrf) {
      this.rawPrf = rawPrf;
   }

   @Override
   public byte[] compute(byte[] input, int outputLength) throws GeneralSecurityException {
      return this.rawPrf.compute(input, outputLength);
   }
}
