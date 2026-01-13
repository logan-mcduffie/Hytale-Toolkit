package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.BufferingOutputStream;

public class BufferingContentSigner implements ExtendedContentSigner {
   private final ContentSigner contentSigner;
   private final OutputStream output;

   public BufferingContentSigner(ContentSigner var1) {
      this.contentSigner = var1;
      this.output = new BufferingOutputStream(var1.getOutputStream());
   }

   public BufferingContentSigner(ContentSigner var1, int var2) {
      this.contentSigner = var1;
      this.output = new BufferingOutputStream(var1.getOutputStream(), var2);
   }

   @Override
   public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.contentSigner.getAlgorithmIdentifier();
   }

   @Override
   public OutputStream getOutputStream() {
      return this.output;
   }

   @Override
   public byte[] getSignature() {
      return this.contentSigner.getSignature();
   }

   @Override
   public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
      return this.contentSigner instanceof ExtendedContentSigner ? ((ExtendedContentSigner)this.contentSigner).getDigestAlgorithmIdentifier() : null;
   }
}
