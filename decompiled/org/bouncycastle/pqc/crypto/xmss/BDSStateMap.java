package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.util.Integers;

public class BDSStateMap implements Serializable {
   private static final long serialVersionUID = -3464451825208522308L;
   private final Map<Integer, BDS> bdsState = new TreeMap<>();
   private transient long maxIndex;

   BDSStateMap(long var1) {
      this.maxIndex = var1;
   }

   BDSStateMap(BDSStateMap var1, long var2) {
      for (Integer var5 : var1.bdsState.keySet()) {
         this.bdsState.put(var5, new BDS(var1.bdsState.get(var5)));
      }

      this.maxIndex = var2;
   }

   BDSStateMap(XMSSMTParameters var1, long var2, byte[] var4, byte[] var5) {
      this.maxIndex = (1L << var1.getHeight()) - 1L;

      for (long var6 = 0L; var6 < var2; var6++) {
         this.updateState(var1, var6, var4, var5);
      }
   }

   public long getMaxIndex() {
      return this.maxIndex;
   }

   void updateState(XMSSMTParameters var1, long var2, byte[] var4, byte[] var5) {
      XMSSParameters var6 = var1.getXMSSParameters();
      int var7 = var6.getHeight();
      long var8 = XMSSUtil.getTreeIndex(var2, var7);
      int var10 = XMSSUtil.getLeafIndex(var2, var7);
      OTSHashAddress var11 = (OTSHashAddress)new OTSHashAddress.Builder().withTreeAddress(var8).withOTSAddress(var10).build();
      if (var10 < (1 << var7) - 1) {
         if (this.get(0) == null || var10 == 0) {
            this.put(0, new BDS(var6, var4, var5, var11));
         }

         this.update(0, var4, var5, var11);
      }

      for (int var12 = 1; var12 < var1.getLayers(); var12++) {
         var10 = XMSSUtil.getLeafIndex(var8, var7);
         var8 = XMSSUtil.getTreeIndex(var8, var7);
         var11 = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(var12).withTreeAddress(var8).withOTSAddress(var10).build();
         if (this.bdsState.get(var12) == null || XMSSUtil.isNewBDSInitNeeded(var2, var7, var12)) {
            this.bdsState.put(var12, new BDS(var6, var4, var5, var11));
         }

         if (var10 < (1 << var7) - 1 && XMSSUtil.isNewAuthenticationPathNeeded(var2, var7, var12)) {
            this.update(var12, var4, var5, var11);
         }
      }
   }

   public boolean isEmpty() {
      return this.bdsState.isEmpty();
   }

   BDS get(int var1) {
      return this.bdsState.get(Integers.valueOf(var1));
   }

   BDS update(int var1, byte[] var2, byte[] var3, OTSHashAddress var4) {
      return this.bdsState.put(Integers.valueOf(var1), this.bdsState.get(Integers.valueOf(var1)).getNextState(var2, var3, var4));
   }

   void put(int var1, BDS var2) {
      this.bdsState.put(Integers.valueOf(var1), var2);
   }

   public BDSStateMap withWOTSDigest(ASN1ObjectIdentifier var1) {
      BDSStateMap var2 = new BDSStateMap(this.maxIndex);

      for (Integer var4 : this.bdsState.keySet()) {
         var2.bdsState.put(var4, this.bdsState.get(var4).withWOTSDigest(var1));
      }

      return var2;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (var1.available() != 0) {
         this.maxIndex = var1.readLong();
      } else {
         this.maxIndex = 0L;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeLong(this.maxIndex);
   }
}
