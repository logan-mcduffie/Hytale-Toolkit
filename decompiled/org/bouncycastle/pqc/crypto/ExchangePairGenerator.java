package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface ExchangePairGenerator {
   ExchangePair generateExchange(AsymmetricKeyParameter var1);
}
