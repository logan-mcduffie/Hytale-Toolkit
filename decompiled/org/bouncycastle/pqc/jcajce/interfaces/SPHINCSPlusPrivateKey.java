package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

public interface SPHINCSPlusPrivateKey extends PrivateKey, SPHINCSPlusKey {
   SPHINCSPlusPublicKey getPublicKey();
}
