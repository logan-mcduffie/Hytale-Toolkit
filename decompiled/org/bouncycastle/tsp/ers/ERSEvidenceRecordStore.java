package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public class ERSEvidenceRecordStore implements Store<ERSEvidenceRecord> {
   private Map<ERSEvidenceRecordStore.HashNode, List<ERSEvidenceRecord>> recordMap = new HashMap<>();
   private DigestCalculator digCalc = null;

   public ERSEvidenceRecordStore(Collection<ERSEvidenceRecord> var1) throws OperatorCreationException {
      for (ERSEvidenceRecord var3 : var1) {
         ArchiveTimeStamp var4 = var3.getArchiveTimeStamps()[0];
         if (this.digCalc == null) {
            DigestCalculatorProvider var5 = var3.getDigestAlgorithmProvider();
            this.digCalc = var5.get(var4.getDigestAlgorithmIdentifier());
         }

         PartialHashtree var8 = var4.getHashTreeLeaf();
         if (var8 != null) {
            byte[][] var6 = var8.getValues();
            if (var6.length > 1) {
               for (int var7 = 0; var7 != var6.length; var7++) {
                  this.addRecord(new ERSEvidenceRecordStore.HashNode(var6[var7]), var3);
               }

               this.addRecord(new ERSEvidenceRecordStore.HashNode(ERSUtil.computeNodeHash(this.digCalc, var8)), var3);
            } else {
               this.addRecord(new ERSEvidenceRecordStore.HashNode(var6[0]), var3);
            }
         } else {
            this.addRecord(new ERSEvidenceRecordStore.HashNode(var4.getTimeStampDigestValue()), var3);
         }
      }
   }

   private void addRecord(ERSEvidenceRecordStore.HashNode var1, ERSEvidenceRecord var2) {
      List var3 = this.recordMap.get(var1);
      if (var3 != null) {
         ArrayList var4 = new ArrayList(var3.size() + 1);
         var4.addAll(var3);
         var4.add(var2);
         this.recordMap.put(var1, var4);
      } else {
         this.recordMap.put(var1, Collections.singletonList(var2));
      }
   }

   @Override
   public Collection<ERSEvidenceRecord> getMatches(Selector<ERSEvidenceRecord> var1) throws StoreException {
      if (var1 instanceof ERSEvidenceRecordSelector) {
         ERSEvidenceRecordStore.HashNode var8 = new ERSEvidenceRecordStore.HashNode(((ERSEvidenceRecordSelector)var1).getData().getHash(this.digCalc, null));
         List var10 = this.recordMap.get(var8);
         if (var10 != null) {
            ArrayList var11 = new ArrayList(var10.size());

            for (int var12 = 0; var12 != var10.size(); var12++) {
               ERSEvidenceRecord var6 = (ERSEvidenceRecord)var10.get(var12);
               if (var1.match(var6)) {
                  var11.add(var6);
               }
            }

            return Collections.unmodifiableList(var11);
         } else {
            return Collections.emptyList();
         }
      } else if (var1 == null) {
         HashSet var7 = new HashSet(this.recordMap.size());
         Iterator var9 = this.recordMap.values().iterator();

         while (var9.hasNext()) {
            var7.addAll((List)var9.next());
         }

         return Collections.unmodifiableList(new ArrayList<>(var7));
      } else {
         HashSet var2 = new HashSet();

         for (List var4 : this.recordMap.values()) {
            for (int var5 = 0; var5 != var4.size(); var5++) {
               if (var1.match((ERSEvidenceRecord)var4.get(var5))) {
                  var2.add((ERSEvidenceRecord)var4.get(var5));
               }
            }
         }

         return Collections.unmodifiableList(new ArrayList<>(var2));
      }
   }

   private static class HashNode {
      private final byte[] dataHash;
      private final int hashCode;

      public HashNode(byte[] var1) {
         this.dataHash = var1;
         this.hashCode = Arrays.hashCode(var1);
      }

      @Override
      public int hashCode() {
         return this.hashCode;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof ERSEvidenceRecordStore.HashNode ? Arrays.areEqual(this.dataHash, ((ERSEvidenceRecordStore.HashNode)var1).dataHash) : false;
      }
   }
}
