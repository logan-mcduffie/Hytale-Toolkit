package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;

class DigestUpdatingOutputStream extends OutputStream {
   private MessageDigest digest;

   DigestUpdatingOutputStream(MessageDigest var1) {
      this.digest = var1;
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.digest.update(var1, var2, var3);
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.digest.update(var1);
   }

   @Override
   public void write(int var1) throws IOException {
      this.digest.update((byte)var1);
   }
}
