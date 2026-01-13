package com.google.crypto.tink.jwt;

import com.google.crypto.tink.Parameters;

public abstract class JwtMacParameters extends Parameters {
   public abstract boolean allowKidAbsent();
}
