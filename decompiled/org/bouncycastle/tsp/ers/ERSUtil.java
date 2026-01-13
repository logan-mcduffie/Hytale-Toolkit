package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.io.Streams;

class ERSUtil {
   private static final Comparator<byte[]> hashComp = new ByteArrayComparator();

   private ERSUtil() {
   }

   static byte[] calculateDigest(DigestCalculator var0, byte[] var1) {
      try {
         OutputStream var2 = var0.getOutputStream();
         var2.write(var1);
         var2.close();
         return var0.getDigest();
      } catch (IOException var3) {
         throw ExpUtil.createIllegalState("unable to calculate hash: " + var3.getMessage(), var3);
      }
   }

   static byte[] calculateBranchHash(DigestCalculator var0, byte[] var1, byte[] var2) {
      return hashComp.compare(var1, var2) <= 0 ? calculateDigest(var0, var1, var2) : calculateDigest(var0, var2, var1);
   }

   static byte[] calculateBranchHash(DigestCalculator var0, byte[][] var1) {
      return var1.length == 2 ? calculateBranchHash(var0, var1[0], var1[1]) : calculateDigest(var0, buildIndexedHashList(var1).iterator());
   }

   static byte[] calculateDigest(DigestCalculator var0, byte[] var1, byte[] var2) {
      try {
         OutputStream var3 = var0.getOutputStream();
         var3.write(var1);
         var3.write(var2);
         var3.close();
         return var0.getDigest();
      } catch (IOException var4) {
         throw ExpUtil.createIllegalState("unable to calculate hash: " + var4.getMessage(), var4);
      }
   }

   static byte[] calculateDigest(DigestCalculator var0, Iterator<byte[]> var1) {
      try {
         OutputStream var2 = var0.getOutputStream();

         while (var1.hasNext()) {
            var2.write((byte[])var1.next());
         }

         var2.close();
         return var0.getDigest();
      } catch (IOException var3) {
         throw ExpUtil.createIllegalState("unable to calculate hash: " + var3.getMessage(), var3);
      }
   }

   static byte[] calculateDigest(DigestCalculator var0, InputStream var1) {
      try {
         OutputStream var2 = var0.getOutputStream();
         Streams.pipeAll(var1, var2);
         var2.close();
         return var0.getDigest();
      } catch (IOException var3) {
         throw ExpUtil.createIllegalState("unable to calculate hash: " + var3.getMessage(), var3);
      }
   }

   static byte[] computeNodeHash(DigestCalculator var0, PartialHashtree var1) {
      byte[][] var2 = var1.getValues();
      return var2.length > 1 ? calculateDigest(var0, buildIndexedHashList(var2).iterator()) : var2[0];
   }

   static List<byte[]> buildIndexedHashList(byte[][] var0) {
      SortedHashList var1 = new SortedHashList();

      for (int var2 = 0; var2 != var0.length; var2++) {
         var1.add(var0[var2]);
      }

      return var1.toList();
   }

   static List<byte[]> buildHashList(DigestCalculator var0, List<ERSData> var1, byte[] var2) {
      SortedHashList var3 = new SortedHashList();

      for (int var4 = 0; var4 != var1.size(); var4++) {
         var3.add(((ERSData)var1.get(var4)).getHash(var0, var2));
      }

      return var3.toList();
   }

   static List<IndexedHash> buildIndexedHashList(DigestCalculator var0, List<ERSData> var1, byte[] var2) {
      SortedIndexedHashList var3 = new SortedIndexedHashList();

      for (int var4 = 0; var4 != var1.size(); var4++) {
         byte[] var5 = ((ERSData)var1.get(var4)).getHash(var0, var2);
         var3.add(new IndexedHash(var4, var5));
      }

      return var3.toList();
   }

   static byte[] concatPreviousHashes(DigestCalculator var0, byte[] var1, byte[] var2) {
      if (var1 == null) {
         return var2;
      } else {
         try {
            OutputStream var3 = var0.getOutputStream();
            var3.write(var2);
            var3.write(var1);
            var3.close();
            return var0.getDigest();
         } catch (IOException var4) {
            throw new IllegalStateException("unable to hash data");
         }
      }
   }
}
