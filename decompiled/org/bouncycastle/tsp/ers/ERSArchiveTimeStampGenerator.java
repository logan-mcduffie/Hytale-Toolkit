package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampSequence;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.util.Arrays;

public class ERSArchiveTimeStampGenerator {
   private final DigestCalculator digCalc;
   private List<ERSData> dataObjects = new ArrayList<>();
   private ERSRootNodeCalculator rootNodeCalculator = new BinaryTreeRootCalculator();
   private byte[] previousChainHash;

   public ERSArchiveTimeStampGenerator(DigestCalculator var1) {
      this.digCalc = var1;
   }

   public void addData(ERSData var1) {
      this.dataObjects.add(var1);
   }

   public void addAllData(List<ERSData> var1) {
      this.dataObjects.addAll(var1);
   }

   void addPreviousChains(ArchiveTimeStampSequence var1) throws IOException {
      OutputStream var2 = this.digCalc.getOutputStream();
      var2.write(var1.getEncoded("DER"));
      var2.close();
      this.previousChainHash = this.digCalc.getDigest();
   }

   public TimeStampRequest generateTimeStampRequest(TimeStampRequestGenerator var1) throws TSPException, IOException {
      ERSArchiveTimeStampGenerator.IndexedPartialHashtree[] var2 = this.getPartialHashtrees();
      byte[] var3 = this.rootNodeCalculator.computeRootHash(this.digCalc, var2);
      return var1.generate(this.digCalc.getAlgorithmIdentifier(), var3);
   }

   public TimeStampRequest generateTimeStampRequest(TimeStampRequestGenerator var1, BigInteger var2) throws TSPException, IOException {
      ERSArchiveTimeStampGenerator.IndexedPartialHashtree[] var3 = this.getPartialHashtrees();
      byte[] var4 = this.rootNodeCalculator.computeRootHash(this.digCalc, var3);
      return var1.generate(this.digCalc.getAlgorithmIdentifier(), var4, var2);
   }

   public ERSArchiveTimeStamp generateArchiveTimeStamp(TimeStampResponse var1) throws TSPException, ERSException {
      ERSArchiveTimeStampGenerator.IndexedPartialHashtree[] var2 = this.getPartialHashtrees();
      if (var2.length != 1) {
         throw new ERSException("multiple reduced hash trees found");
      } else {
         byte[] var3 = this.rootNodeCalculator.computeRootHash(this.digCalc, var2);
         if (var1.getStatus() != 0) {
            throw new TSPException("TSP response error status: " + var1.getStatusString());
         } else {
            TSTInfo var4 = var1.getTimeStampToken().getTimeStampInfo().toASN1Structure();
            if (!var4.getMessageImprint().getHashAlgorithm().equals(this.digCalc.getAlgorithmIdentifier())) {
               throw new ERSException("time stamp imprint for wrong algorithm");
            } else if (!Arrays.areEqual(var4.getMessageImprint().getHashedMessage(), var3)) {
               throw new ERSException("time stamp imprint for wrong root hash");
            } else {
               return var2[0].getValueCount() == 1
                  ? new ERSArchiveTimeStamp(new ArchiveTimeStamp(null, null, var1.getTimeStampToken().toCMSSignedData().toASN1Structure()), this.digCalc)
                  : new ERSArchiveTimeStamp(
                     new ArchiveTimeStamp(this.digCalc.getAlgorithmIdentifier(), var2, var1.getTimeStampToken().toCMSSignedData().toASN1Structure()),
                     this.digCalc
                  );
            }
         }
      }
   }

   public List<ERSArchiveTimeStamp> generateArchiveTimeStamps(TimeStampResponse var1) throws TSPException, ERSException {
      ERSArchiveTimeStampGenerator.IndexedPartialHashtree[] var2 = this.getPartialHashtrees();
      byte[] var3 = this.rootNodeCalculator.computeRootHash(this.digCalc, var2);
      if (var1.getStatus() != 0) {
         throw new TSPException("TSP response error status: " + var1.getStatusString());
      } else {
         TSTInfo var4 = var1.getTimeStampToken().getTimeStampInfo().toASN1Structure();
         if (!var4.getMessageImprint().getHashAlgorithm().equals(this.digCalc.getAlgorithmIdentifier())) {
            throw new ERSException("time stamp imprint for wrong algorithm");
         } else if (!Arrays.areEqual(var4.getMessageImprint().getHashedMessage(), var3)) {
            throw new ERSException("time stamp imprint for wrong root hash");
         } else {
            ContentInfo var5 = var1.getTimeStampToken().toCMSSignedData().toASN1Structure();
            ArrayList var6 = new ArrayList();
            if (var2.length == 1 && var2[0].getValueCount() == 1) {
               var6.add(new ERSArchiveTimeStamp(new ArchiveTimeStamp(null, null, var5), this.digCalc));
            } else {
               ERSArchiveTimeStamp[] var7 = new ERSArchiveTimeStamp[var2.length];

               for (int var8 = 0; var8 != var2.length; var8++) {
                  PartialHashtree[] var9 = this.rootNodeCalculator.computePathToRoot(this.digCalc, var2[var8], var8);
                  var7[var2[var8].order] = new ERSArchiveTimeStamp(new ArchiveTimeStamp(this.digCalc.getAlgorithmIdentifier(), var9, var5), this.digCalc);
               }

               for (int var10 = 0; var10 != var2.length; var10++) {
                  var6.add(var7[var10]);
               }
            }

            return var6;
         }
      }
   }

   private ERSArchiveTimeStampGenerator.IndexedPartialHashtree[] getPartialHashtrees() {
      List var1 = ERSUtil.buildIndexedHashList(this.digCalc, this.dataObjects, this.previousChainHash);
      ERSArchiveTimeStampGenerator.IndexedPartialHashtree[] var2 = new ERSArchiveTimeStampGenerator.IndexedPartialHashtree[var1.size()];
      HashSet var3 = new HashSet();

      for (int var4 = 0; var4 != this.dataObjects.size(); var4++) {
         if (this.dataObjects.get(var4) instanceof ERSDataGroup) {
            var3.add((ERSDataGroup)this.dataObjects.get(var4));
         }
      }

      for (int var9 = 0; var9 != var1.size(); var9++) {
         byte[] var5 = ((IndexedHash)var1.get(var9)).digest;
         ERSData var6 = this.dataObjects.get(((IndexedHash)var1.get(var9)).order);
         if (var6 instanceof ERSDataGroup) {
            ERSDataGroup var7 = (ERSDataGroup)var6;
            List var8 = var7.getHashes(this.digCalc, this.previousChainHash);
            var2[var9] = new ERSArchiveTimeStampGenerator.IndexedPartialHashtree(((IndexedHash)var1.get(var9)).order, var8.toArray(new byte[var8.size()][]));
         } else {
            var2[var9] = new ERSArchiveTimeStampGenerator.IndexedPartialHashtree(((IndexedHash)var1.get(var9)).order, var5);
         }
      }

      return var2;
   }

   private static class IndexedPartialHashtree extends PartialHashtree {
      final int order;

      private IndexedPartialHashtree(int var1, byte[] var2) {
         super(var2);
         this.order = var1;
      }

      private IndexedPartialHashtree(int var1, byte[][] var2) {
         super(var2);
         this.order = var1;
      }
   }
}
