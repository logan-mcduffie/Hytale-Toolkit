package com.google.crypto.tink.jwt;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.PrivateKey;
import com.google.errorprone.annotations.Immutable;
import java.util.Optional;
import javax.annotation.Nullable;

@Immutable
public abstract class JwtSignaturePrivateKey extends Key implements PrivateKey {
   public abstract JwtSignaturePublicKey getPublicKey();

   public Optional<String> getKid() {
      return this.getPublicKey().getKid();
   }

   public abstract JwtSignatureParameters getParameters();

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.getPublicKey().getIdRequirementOrNull();
   }
}
