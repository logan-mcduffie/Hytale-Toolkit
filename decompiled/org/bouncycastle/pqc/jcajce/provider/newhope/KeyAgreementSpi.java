package org.bouncycastle.pqc.jcajce.provider.newhope;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class KeyAgreementSpi extends BaseAgreementSpi {
   private NHAgreement agreement;
   private BCNHPublicKey otherPartyKey;
   private NHExchangePairGenerator exchangePairGenerator;
   private byte[] shared;

   public KeyAgreementSpi() {
      super("NH", null);
   }

   @Override
   protected void engineInit(Key var1, SecureRandom var2) throws InvalidKeyException {
      if (var1 != null) {
         this.agreement = new NHAgreement();
         this.agreement.init(((BCNHPrivateKey)var1).getKeyParams());
      } else {
         this.exchangePairGenerator = new NHExchangePairGenerator(var2);
      }
   }

   @Override
   protected void doInitFromKey(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("NewHope does not require parameters");
   }

   @Override
   protected Key engineDoPhase(Key var1, boolean var2) throws InvalidKeyException, IllegalStateException {
      if (!var2) {
         throw new IllegalStateException("NewHope can only be between two parties.");
      } else {
         this.otherPartyKey = (BCNHPublicKey)var1;
         if (this.exchangePairGenerator != null) {
            ExchangePair var3 = this.exchangePairGenerator.generateExchange((AsymmetricKeyParameter)this.otherPartyKey.getKeyParams());
            this.shared = var3.getSharedValue();
            return new BCNHPublicKey((NHPublicKeyParameters)var3.getPublicKey());
         } else {
            this.shared = this.agreement.calculateAgreement(this.otherPartyKey.getKeyParams());
            return null;
         }
      }
   }

   @Override
   protected byte[] engineGenerateSecret() throws IllegalStateException {
      byte[] var1 = Arrays.clone(this.shared);
      Arrays.fill(this.shared, (byte)0);
      return var1;
   }

   @Override
   protected int engineGenerateSecret(byte[] var1, int var2) throws IllegalStateException, ShortBufferException {
      System.arraycopy(this.shared, 0, var1, var2, this.shared.length);
      Arrays.fill(this.shared, (byte)0);
      return this.shared.length;
   }

   @Override
   protected byte[] doCalcSecret() {
      return this.engineGenerateSecret();
   }
}
