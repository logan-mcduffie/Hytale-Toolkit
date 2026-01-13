package org.bouncycastle.pqc.crypto.xwing;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class XWingKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom random;
   private static final byte[] XWING_LABEL = Strings.toByteArray("\\.//^\\");

   public XWingKEMGenerator(SecureRandom var1) {
      this.random = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      XWingPublicKeyParameters var2 = (XWingPublicKeyParameters)var1;
      MLKEMPublicKeyParameters var3 = var2.getKyberPublicKey();
      X25519PublicKeyParameters var4 = var2.getXDHPublicKey();
      byte[] var5 = var4.getEncoded();
      MLKEMGenerator var6 = new MLKEMGenerator(this.random);
      SecretWithEncapsulation var7 = var6.generateEncapsulated(var3);
      byte[] var8 = var7.getEncapsulation();
      X25519KeyPairGenerator var9 = new X25519KeyPairGenerator();
      var9.init(new X25519KeyGenerationParameters(this.random));
      AsymmetricCipherKeyPair var10 = var9.generateKeyPair();
      byte[] var11 = ((X25519PublicKeyParameters)var10.getPublic()).getEncoded();
      byte[] var12 = computeSSX(var4, (X25519PrivateKeyParameters)var10.getPrivate());
      byte[] var13 = computeSharedSecret(var5, var7.getSecret(), var11, var12);
      Arrays.clear(var12);
      return new SecretWithEncapsulationImpl(var13, Arrays.concatenate(var8, var11));
   }

   static byte[] computeSSX(X25519PublicKeyParameters var0, X25519PrivateKeyParameters var1) {
      X25519Agreement var2 = new X25519Agreement();
      var2.init(var1);
      byte[] var3 = new byte[var2.getAgreementSize()];
      var2.calculateAgreement(var0, var3, 0);
      return var3;
   }

   static byte[] computeSharedSecret(byte[] var0, byte[] var1, byte[] var2, byte[] var3) {
      SHA3Digest var4 = new SHA3Digest(256);
      var4.update(var1, 0, var1.length);
      var4.update(var3, 0, var3.length);
      var4.update(var2, 0, var2.length);
      var4.update(var0, 0, var0.length);
      var4.update(XWING_LABEL, 0, XWING_LABEL.length);
      byte[] var5 = new byte[32];
      var4.doFinal(var5, 0);
      return var5;
   }
}
