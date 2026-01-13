package com.nimbusds.jose.proc;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import java.security.Key;
import java.util.List;

public interface JWSKeySelector<C extends SecurityContext> {
   List<? extends Key> selectJWSKeys(JWSHeader var1, C var2) throws KeySourceException;
}
