package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

public interface KyberPrivateKey extends PrivateKey, KyberKey {
   KyberPublicKey getPublicKey();
}
