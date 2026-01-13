package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.Parameters;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

public interface KeyCreator<ParametersT extends Parameters> {
   Key createKey(ParametersT parameters, @Nullable Integer idRequirement) throws GeneralSecurityException;
}
