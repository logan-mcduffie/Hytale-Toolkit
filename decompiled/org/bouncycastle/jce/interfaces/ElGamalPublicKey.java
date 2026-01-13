package org.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;

/** @deprecated */
public interface ElGamalPublicKey extends ElGamalKey, DHPublicKey {
   @Override
   BigInteger getY();
}
