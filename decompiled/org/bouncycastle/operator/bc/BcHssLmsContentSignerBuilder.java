package org.bouncycastle.operator.bc;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSSigner;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSSigner;

public class BcHssLmsContentSignerBuilder extends BcContentSignerBuilder {
   private static final AlgorithmIdentifier sigAlgId = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);

   public BcHssLmsContentSignerBuilder() {
      super(sigAlgId, null);
   }

   @Override
   protected Signer createSigner(AlgorithmIdentifier var1, AlgorithmIdentifier var2) throws OperatorCreationException {
      return new BcHssLmsContentSignerBuilder.HssSigner();
   }

   static class HssSigner implements Signer {
      private MessageSigner signer;
      private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

      public HssSigner() {
      }

      @Override
      public void init(boolean var1, CipherParameters var2) {
         if (!(var2 instanceof HSSPublicKeyParameters) && !(var2 instanceof HSSPrivateKeyParameters)) {
            if (!(var2 instanceof LMSPublicKeyParameters) && !(var2 instanceof LMSPrivateKeyParameters)) {
               throw new IllegalArgumentException("Incorrect Key Parameters");
            }

            this.signer = new LMSSigner();
         } else {
            this.signer = new HSSSigner();
         }

         this.signer.init(var1, var2);
      }

      @Override
      public void update(byte var1) {
         this.stream.write(var1);
      }

      @Override
      public void update(byte[] var1, int var2, int var3) {
         this.stream.write(var1, var2, var3);
      }

      @Override
      public byte[] generateSignature() throws CryptoException, DataLengthException {
         byte[] var1 = this.stream.toByteArray();
         this.stream.reset();
         return this.signer.generateSignature(var1);
      }

      @Override
      public boolean verifySignature(byte[] var1) {
         byte[] var2 = this.stream.toByteArray();
         this.stream.reset();
         return this.signer.verifySignature(var2, var1);
      }

      @Override
      public void reset() {
         this.stream.reset();
      }
   }
}
