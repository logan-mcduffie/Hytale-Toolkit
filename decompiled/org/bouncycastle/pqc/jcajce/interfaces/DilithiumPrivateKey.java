package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

public interface DilithiumPrivateKey extends PrivateKey, DilithiumKey {
   DilithiumPublicKey getPublicKey();
}
