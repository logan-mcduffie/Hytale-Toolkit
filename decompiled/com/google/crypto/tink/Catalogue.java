package com.google.crypto.tink;

import com.google.crypto.tink.internal.PrimitiveWrapper;
import java.security.GeneralSecurityException;

@Deprecated
public interface Catalogue<P> {
   KeyManager<P> getKeyManager(String typeUrl, String primitiveName, int minVersion) throws GeneralSecurityException;

   PrimitiveWrapper<?, P> getPrimitiveWrapper() throws GeneralSecurityException;
}
