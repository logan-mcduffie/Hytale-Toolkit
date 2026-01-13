package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;

public interface SLHDSAPrivateKey extends PrivateKey, SLHDSAKey {
   SLHDSAPublicKey getPublicKey();
}
