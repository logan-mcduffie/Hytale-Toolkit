package org.bouncycastle.cms.jcajce;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputExpander;
import org.bouncycastle.operator.InputExpanderProvider;
import org.bouncycastle.util.io.StreamOverflowException;

public class ZlibExpanderProvider implements InputExpanderProvider {
   private final long limit;

   public ZlibExpanderProvider() {
      this.limit = -1L;
   }

   public ZlibExpanderProvider(long var1) {
      this.limit = var1;
   }

   @Override
   public InputExpander get(final AlgorithmIdentifier var1) {
      return new InputExpander() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return var1;
         }

         @Override
         public InputStream getInputStream(InputStream var1x) {
            Object var2 = new InflaterInputStream(var1x);
            if (ZlibExpanderProvider.this.limit >= 0L) {
               var2 = new ZlibExpanderProvider.LimitedInputStream((InputStream)var2, ZlibExpanderProvider.this.limit);
            }

            return (InputStream)var2;
         }
      };
   }

   private static class LimitedInputStream extends FilterInputStream {
      private long remaining;

      public LimitedInputStream(InputStream var1, long var2) {
         super(var1);
         this.remaining = var2;
      }

      @Override
      public int read() throws IOException {
         if (this.remaining >= 0L) {
            int var1 = super.in.read();
            if (var1 < 0 || --this.remaining >= 0L) {
               return var1;
            }
         }

         throw new StreamOverflowException("expanded byte limit exceeded");
      }

      @Override
      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var3 < 1) {
            return super.read(var1, var2, var3);
         } else if (this.remaining < 1L) {
            this.read();
            return -1;
         } else {
            int var4 = this.remaining > var3 ? var3 : (int)this.remaining;
            int var5 = super.in.read(var1, var2, var4);
            if (var5 > 0) {
               this.remaining -= var5;
            }

            return var5;
         }
      }
   }
}
