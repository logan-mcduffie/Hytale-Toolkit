package com.google.crypto.tink.tinkkey.internal;

import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.tinkkey.TinkKey;
import com.google.errorprone.annotations.Immutable;

@Immutable
public final class ProtoKey implements TinkKey {
   private final KeyData keyData;
   private final boolean hasSecret;
   private final KeyTemplate.OutputPrefixType outputPrefixType;

   public ProtoKey(KeyData keyData, KeyTemplate.OutputPrefixType opt) {
      this.hasSecret = isSecret(keyData);
      this.keyData = keyData;
      this.outputPrefixType = opt;
   }

   private static boolean isSecret(KeyData keyData) {
      return keyData.getKeyMaterialType() == KeyData.KeyMaterialType.UNKNOWN_KEYMATERIAL
         || keyData.getKeyMaterialType() == KeyData.KeyMaterialType.SYMMETRIC
         || keyData.getKeyMaterialType() == KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE;
   }

   public KeyData getProtoKey() {
      return this.keyData;
   }

   public KeyTemplate.OutputPrefixType getOutputPrefixType() {
      return this.outputPrefixType;
   }

   @Override
   public boolean hasSecret() {
      return this.hasSecret;
   }

   @Override
   public KeyTemplate getKeyTemplate() {
      throw new UnsupportedOperationException();
   }
}
