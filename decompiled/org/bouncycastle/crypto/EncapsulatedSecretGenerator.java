package org.bouncycastle.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface EncapsulatedSecretGenerator {
   SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1);
}
