package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class JcaDigestCalculatorProviderBuilder {
   private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());

   public JcaDigestCalculatorProviderBuilder setHelper(JcaJceHelper var1) {
      this.helper = new OperatorHelper(var1);
      return this;
   }

   public JcaDigestCalculatorProviderBuilder setProvider(Provider var1) {
      this.helper = new OperatorHelper(new ProviderJcaJceHelper(var1));
      return this;
   }

   public JcaDigestCalculatorProviderBuilder setProvider(String var1) {
      this.helper = new OperatorHelper(new NamedJcaJceHelper(var1));
      return this;
   }

   public DigestCalculatorProvider build() throws OperatorCreationException {
      return new DigestCalculatorProvider() {
         @Override
         public DigestCalculator get(final AlgorithmIdentifier var1) throws OperatorCreationException {
            final JcaDigestCalculatorProviderBuilder.DigestOutputStream var2;
            try {
               MessageDigest var3 = JcaDigestCalculatorProviderBuilder.this.helper.createDigest(var1);
               var2 = new JcaDigestCalculatorProviderBuilder.DigestOutputStream(var3);
            } catch (GeneralSecurityException var4) {
               throw new OperatorCreationException("exception on setup: " + var4, var4);
            }

            return new DigestCalculator() {
               @Override
               public AlgorithmIdentifier getAlgorithmIdentifier() {
                  return var1;
               }

               @Override
               public OutputStream getOutputStream() {
                  return var2;
               }

               @Override
               public byte[] getDigest() {
                  return var2.getDigest();
               }
            };
         }
      };
   }

   private static class DigestOutputStream extends OutputStream {
      private MessageDigest dig;

      DigestOutputStream(MessageDigest var1) {
         this.dig = var1;
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.dig.update(var1, var2, var3);
      }

      @Override
      public void write(byte[] var1) throws IOException {
         this.dig.update(var1);
      }

      @Override
      public void write(int var1) throws IOException {
         this.dig.update((byte)var1);
      }

      byte[] getDigest() {
         return this.dig.digest();
      }
   }
}
