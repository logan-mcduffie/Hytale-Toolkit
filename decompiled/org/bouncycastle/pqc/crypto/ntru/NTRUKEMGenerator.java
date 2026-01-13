package org.bouncycastle.pqc.crypto.ntru;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

public class NTRUKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom random;

   public NTRUKEMGenerator(SecureRandom var1) {
      if (var1 == null) {
         throw new NullPointerException("'random' cannot be null");
      } else {
         this.random = var1;
      }
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      if (var1 == null) {
         throw new NullPointerException("'recipientKey' cannot be null");
      } else {
         NTRUPublicKeyParameters var2 = (NTRUPublicKeyParameters)var1;
         NTRUParameterSet var3 = var2.getParameters().getParameterSet();
         NTRUSampling var4 = new NTRUSampling(var3);
         NTRUOWCPA var5 = new NTRUOWCPA(var3);
         byte[] var6 = new byte[var3.owcpaMsgBytes()];
         byte[] var7 = new byte[var3.sampleRmBytes()];
         this.random.nextBytes(var7);
         PolynomialPair var8 = var4.sampleRm(var7);
         Polynomial var9 = var8.r();
         Polynomial var10 = var8.m();
         var9.s3ToBytes(var6, 0);
         var10.s3ToBytes(var6, var3.packTrinaryBytes());
         SHA3Digest var11 = new SHA3Digest(256);
         byte[] var12 = new byte[var11.getDigestSize()];
         var11.update(var6, 0, var6.length);
         var11.doFinal(var12, 0);
         var9.z3ToZq();
         byte[] var13 = var5.encrypt(var9, var10, var2.publicKey);
         byte[] var14 = Arrays.copyOfRange(var12, 0, var3.sharedKeyBytes());
         Arrays.clear(var12);
         return new SecretWithEncapsulationImpl(var14, var13);
      }
   }
}
