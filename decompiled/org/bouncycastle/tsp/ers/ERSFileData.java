package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bouncycastle.operator.DigestCalculator;

public class ERSFileData extends ERSCachingData {
   private final File content;

   public ERSFileData(File var1) throws FileNotFoundException {
      if (var1.isDirectory()) {
         throw new IllegalArgumentException("directory not allowed as ERSFileData");
      } else if (!var1.exists()) {
         throw new FileNotFoundException(var1.getAbsolutePath() + " does not exist");
      } else if (!var1.canRead()) {
         throw new FileNotFoundException(var1.getAbsolutePath() + " is not readable");
      } else {
         this.content = var1;
      }
   }

   @Override
   protected byte[] calculateHash(DigestCalculator var1, byte[] var2) {
      try {
         FileInputStream var3 = new FileInputStream(this.content);
         byte[] var4 = ERSUtil.calculateDigest(var1, var3);
         var3.close();
         return var2 != null ? ERSUtil.concatPreviousHashes(var1, var2, var4) : var4;
      } catch (IOException var5) {
         throw new IllegalStateException("unable to process " + this.content.getAbsolutePath());
      }
   }
}
