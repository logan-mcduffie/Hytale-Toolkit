package org.bouncycastle.pqc.crypto;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.util.Arrays;

public final class MessageSignerAdapter implements Signer {
   private final MessageSignerAdapter.Buffer buffer = new MessageSignerAdapter.Buffer();
   private final MessageSigner messageSigner;

   public MessageSignerAdapter(MessageSigner var1) {
      if (var1 == null) {
         throw new NullPointerException("'messageSigner' cannot be null");
      } else {
         this.messageSigner = var1;
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.messageSigner.init(var1, var2);
   }

   @Override
   public void update(byte var1) {
      this.buffer.write(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.buffer.write(var1, var2, var3);
   }

   @Override
   public byte[] generateSignature() {
      return this.messageSigner.generateSignature(this.getMessage());
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      return this.messageSigner.verifySignature(this.getMessage(), var1);
   }

   @Override
   public void reset() {
      this.buffer.reset();
   }

   private byte[] getMessage() {
      byte[] var1;
      try {
         var1 = this.buffer.toByteArray();
      } finally {
         this.reset();
      }

      return var1;
   }

   private static final class Buffer extends ByteArrayOutputStream {
      private Buffer() {
      }

      @Override
      public synchronized void reset() {
         Arrays.fill(this.buf, 0, this.count, (byte)0);
         this.count = 0;
      }
   }
}
