package com.google.crypto.tink.jwt;

import com.google.crypto.tink.Key;
import java.util.Optional;

public abstract class JwtSignaturePublicKey extends Key {
   public abstract Optional<String> getKid();

   public abstract JwtSignatureParameters getParameters();
}
