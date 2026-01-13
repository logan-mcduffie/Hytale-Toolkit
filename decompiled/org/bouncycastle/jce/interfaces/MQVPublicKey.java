package org.bouncycastle.jce.interfaces;

import java.security.PublicKey;

/** @deprecated */
public interface MQVPublicKey extends PublicKey {
   PublicKey getStaticKey();

   PublicKey getEphemeralKey();
}
