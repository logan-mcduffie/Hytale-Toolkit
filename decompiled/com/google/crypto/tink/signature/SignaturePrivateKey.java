package com.google.crypto.tink.signature;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.PrivateKey;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import javax.annotation.Nullable;

@Immutable
public abstract class SignaturePrivateKey extends Key implements PrivateKey {
   public abstract SignaturePublicKey getPublicKey();

   public final Bytes getOutputPrefix() {
      return this.getPublicKey().getOutputPrefix();
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.getPublicKey().getIdRequirementOrNull();
   }

   public SignatureParameters getParameters() {
      return this.getPublicKey().getParameters();
   }
}
