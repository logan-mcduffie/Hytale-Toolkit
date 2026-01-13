package org.bouncycastle.operator;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

public class NoSignatureContentSigner implements ContentSigner {
   @Override
   public AlgorithmIdentifier getAlgorithmIdentifier() {
      return new AlgorithmIdentifier(X509ObjectIdentifiers.id_alg_unsigned);
   }

   @Override
   public OutputStream getOutputStream() {
      return new OutputStream() {
         @Override
         public void write(byte[] var1, int var2, int var3) throws IOException {
         }

         @Override
         public void write(int var1) throws IOException {
         }
      };
   }

   @Override
   public byte[] getSignature() {
      return new byte[0];
   }
}
