package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.io.Streams;

public class ERSInputStreamData extends ERSCachingData {
   private final File contentFile;
   private final byte[] contentBytes;

   public ERSInputStreamData(File var1) throws FileNotFoundException {
      if (var1.isDirectory()) {
         throw new IllegalArgumentException("directory not allowed");
      } else if (!var1.exists()) {
         throw new FileNotFoundException(var1 + " not found");
      } else {
         this.contentBytes = null;
         this.contentFile = var1;
      }
   }

   public ERSInputStreamData(InputStream var1) {
      try {
         this.contentBytes = Streams.readAll(var1);
      } catch (IOException var3) {
         throw ExpUtil.createIllegalState("unable to open content: " + var3.getMessage(), var3);
      }

      this.contentFile = null;
   }

   @Override
   protected byte[] calculateHash(DigestCalculator var1, byte[] var2) {
      byte[] var3;
      if (this.contentBytes != null) {
         var3 = ERSUtil.calculateDigest(var1, this.contentBytes);
      } else {
         try {
            FileInputStream var4 = new FileInputStream(this.contentFile);
            var3 = ERSUtil.calculateDigest(var1, var4);
            var4.close();
         } catch (IOException var5) {
            throw ExpUtil.createIllegalState("unable to open content: " + var5.getMessage(), var5);
         }
      }

      return var2 != null ? ERSUtil.concatPreviousHashes(var1, var2, var3) : var3;
   }
}
