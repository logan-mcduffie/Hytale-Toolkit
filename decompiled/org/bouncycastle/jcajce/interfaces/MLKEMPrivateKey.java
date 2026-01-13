package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;

public interface MLKEMPrivateKey extends PrivateKey, MLKEMKey {
   MLKEMPublicKey getPublicKey();

   byte[] getPrivateData();

   byte[] getSeed();

   MLKEMPrivateKey getPrivateKey(boolean var1);
}
