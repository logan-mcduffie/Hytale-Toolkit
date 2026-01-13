package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class BcDigestCalculatorProvider implements DigestCalculatorProvider {
   private BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;

   @Override
   public DigestCalculator get(final AlgorithmIdentifier var1) throws OperatorCreationException {
      ExtendedDigest var2 = this.digestProvider.get(var1);
      final BcDigestCalculatorProvider.DigestOutputStream var3 = new BcDigestCalculatorProvider.DigestOutputStream(var2);
      return new DigestCalculator() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return var1;
         }

         @Override
         public OutputStream getOutputStream() {
            return var3;
         }

         @Override
         public byte[] getDigest() {
            return var3.getDigest();
         }
      };
   }

   private static class DigestOutputStream extends OutputStream {
      private Digest dig;

      DigestOutputStream(Digest var1) {
         this.dig = var1;
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.dig.update(var1, var2, var3);
      }

      @Override
      public void write(byte[] var1) throws IOException {
         this.dig.update(var1, 0, var1.length);
      }

      @Override
      public void write(int var1) throws IOException {
         this.dig.update((byte)var1);
      }

      byte[] getDigest() {
         byte[] var1 = new byte[this.dig.getDigestSize()];
         this.dig.doFinal(var1, 0);
         return var1;
      }
   }
}
