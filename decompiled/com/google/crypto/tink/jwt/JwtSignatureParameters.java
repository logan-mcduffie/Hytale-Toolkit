package com.google.crypto.tink.jwt;

import com.google.crypto.tink.Parameters;

public abstract class JwtSignatureParameters extends Parameters {
   public abstract boolean allowKidAbsent();
}
