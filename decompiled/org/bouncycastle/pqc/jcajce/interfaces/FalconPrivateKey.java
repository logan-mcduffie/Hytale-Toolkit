package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

public interface FalconPrivateKey extends PrivateKey, FalconKey {
   FalconPublicKey getPublicKey();
}
