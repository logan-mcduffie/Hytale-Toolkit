package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;

public interface MLDSAPrivateKey extends PrivateKey, MLDSAKey {
   MLDSAPublicKey getPublicKey();

   byte[] getPrivateData();

   byte[] getSeed();

   MLDSAPrivateKey getPrivateKey(boolean var1);
}
