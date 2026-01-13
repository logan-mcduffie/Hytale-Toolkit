package org.bouncycastle.crypto.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Arrays;

public class JournalingSecureRandom extends SecureRandom {
   private static byte[] EMPTY_TRANSCRIPT = new byte[0];
   private final SecureRandom base;
   private JournalingSecureRandom.TranscriptStream tOut = new JournalingSecureRandom.TranscriptStream();
   private byte[] transcript;
   private int index = 0;

   public JournalingSecureRandom() {
      this(CryptoServicesRegistrar.getSecureRandom());
   }

   public JournalingSecureRandom(SecureRandom var1) {
      this.base = var1;
      this.transcript = EMPTY_TRANSCRIPT;
   }

   public JournalingSecureRandom(byte[] var1, SecureRandom var2) {
      this.base = var2;
      this.transcript = Arrays.clone(var1);
   }

   @Override
   public final void nextBytes(byte[] var1) {
      if (this.index >= this.transcript.length) {
         this.base.nextBytes(var1);
      } else {
         int var2;
         for (var2 = 0; var2 != var1.length && this.index < this.transcript.length; var2++) {
            var1[var2] = this.transcript[this.index++];
         }

         if (var2 != var1.length) {
            byte[] var3 = new byte[var1.length - var2];
            this.base.nextBytes(var3);
            System.arraycopy(var3, 0, var1, var2, var3.length);
         }
      }

      try {
         this.tOut.write(var1);
      } catch (IOException var4) {
         throw new IllegalStateException("unable to record transcript: " + var4.getMessage());
      }
   }

   public void clear() {
      Arrays.fill(this.transcript, (byte)0);
      this.tOut.clear();
   }

   public void reset() {
      this.index = 0;
      if (this.index == this.transcript.length) {
         this.transcript = this.tOut.toByteArray();
      }

      this.tOut.reset();
   }

   public byte[] getTranscript() {
      return this.tOut.toByteArray();
   }

   public byte[] getFullTranscript() {
      return this.index == this.transcript.length ? this.tOut.toByteArray() : Arrays.clone(this.transcript);
   }

   private static class TranscriptStream extends ByteArrayOutputStream {
      private TranscriptStream() {
      }

      public void clear() {
         Arrays.fill(this.buf, (byte)0);
      }
   }
}
