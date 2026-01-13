package org.bouncycastle.pqc.crypto.newhope;

import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.ExchangePairGenerator;

public class NHExchangePairGenerator implements ExchangePairGenerator {
   private final SecureRandom random;

   public NHExchangePairGenerator(SecureRandom var1) {
      this.random = var1;
   }

   public ExchangePair GenerateExchange(AsymmetricKeyParameter var1) {
      return this.generateExchange(var1);
   }

   @Override
   public ExchangePair generateExchange(AsymmetricKeyParameter var1) {
      NHPublicKeyParameters var2 = (NHPublicKeyParameters)var1;
      byte[] var3 = new byte[32];
      byte[] var4 = new byte[2048];
      NewHope.sharedB(this.random, var3, var4, var2.pubData);
      return new ExchangePair(new NHPublicKeyParameters(var4), var3);
   }
}
