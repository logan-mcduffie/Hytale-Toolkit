package org.bouncycastle.pqc.crypto.util;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.util.Arrays;

public class SecretWithEncapsulationImpl implements SecretWithEncapsulation {
   private final AtomicBoolean hasBeenDestroyed = new AtomicBoolean(false);
   private final byte[] sessionKey;
   private final byte[] cipher_text;

   public SecretWithEncapsulationImpl(byte[] var1, byte[] var2) {
      this.sessionKey = var1;
      this.cipher_text = var2;
   }

   @Override
   public byte[] getSecret() {
      byte[] var1 = Arrays.clone(this.sessionKey);
      this.checkDestroyed();
      return var1;
   }

   @Override
   public byte[] getEncapsulation() {
      byte[] var1 = Arrays.clone(this.cipher_text);
      this.checkDestroyed();
      return var1;
   }

   @Override
   public void destroy() throws DestroyFailedException {
      if (!this.hasBeenDestroyed.getAndSet(true)) {
         Arrays.clear(this.sessionKey);
         Arrays.clear(this.cipher_text);
      }
   }

   @Override
   public boolean isDestroyed() {
      return this.hasBeenDestroyed.get();
   }

   void checkDestroyed() {
      if (this.isDestroyed()) {
         throw new IllegalStateException("data has been destroyed");
      }
   }
}
