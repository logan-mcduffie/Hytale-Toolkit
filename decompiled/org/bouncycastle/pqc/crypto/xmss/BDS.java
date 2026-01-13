package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public final class BDS implements Serializable {
   private static final long serialVersionUID = 1L;
   private transient WOTSPlus wotsPlus;
   private final int treeHeight;
   private final List<BDSTreeHash> treeHashInstances;
   private int k;
   private XMSSNode root;
   private List<XMSSNode> authenticationPath;
   private Map<Integer, LinkedList<XMSSNode>> retain;
   private Stack<XMSSNode> stack;
   private Map<Integer, XMSSNode> keep;
   private int index;
   private boolean used;
   private transient int maxIndex;

   BDS(XMSSParameters var1, int var2, int var3) {
      this(var1.getWOTSPlus(), var1.getHeight(), var1.getK(), var3);
      this.maxIndex = var2;
      this.index = var3;
      this.used = true;
   }

   BDS(XMSSParameters var1, byte[] var2, byte[] var3, OTSHashAddress var4) {
      this(var1.getWOTSPlus(), var1.getHeight(), var1.getK(), (1 << var1.getHeight()) - 1);
      this.initialize(var2, var3, var4);
   }

   BDS(XMSSParameters var1, byte[] var2, byte[] var3, OTSHashAddress var4, int var5) {
      this(var1.getWOTSPlus(), var1.getHeight(), var1.getK(), (1 << var1.getHeight()) - 1);
      this.initialize(var2, var3, var4);

      while (this.index < var5) {
         this.nextAuthenticationPath(var2, var3, var4);
         this.used = false;
      }
   }

   private BDS(WOTSPlus var1, int var2, int var3, int var4) {
      this.wotsPlus = var1;
      this.treeHeight = var2;
      this.maxIndex = var4;
      this.k = var3;
      if (var3 <= var2 && var3 >= 2 && (var2 - var3) % 2 == 0) {
         this.authenticationPath = new ArrayList<>();
         this.retain = new TreeMap<>();
         this.stack = new Stack<>();
         this.treeHashInstances = new ArrayList<>();

         for (int var5 = 0; var5 < var2 - var3; var5++) {
            this.treeHashInstances.add(new BDSTreeHash(var5));
         }

         this.keep = new TreeMap<>();
         this.index = 0;
         this.used = false;
      } else {
         throw new IllegalArgumentException("illegal value for BDS parameter k");
      }
   }

   BDS(BDS var1) {
      this.wotsPlus = new WOTSPlus(var1.wotsPlus.getParams());
      this.treeHeight = var1.treeHeight;
      this.k = var1.k;
      this.root = var1.root;
      this.authenticationPath = new ArrayList<>();
      this.authenticationPath.addAll(var1.authenticationPath);
      this.retain = new TreeMap<>();

      for (Integer var3 : var1.retain.keySet()) {
         this.retain.put(var3, (LinkedList<XMSSNode>)var1.retain.get(var3).clone());
      }

      this.stack = new Stack<>();
      this.stack.addAll(var1.stack);
      this.treeHashInstances = new ArrayList<>();
      Iterator var4 = var1.treeHashInstances.iterator();

      while (var4.hasNext()) {
         this.treeHashInstances.add(((BDSTreeHash)var4.next()).clone());
      }

      this.keep = new TreeMap<>(var1.keep);
      this.index = var1.index;
      this.maxIndex = var1.maxIndex;
      this.used = var1.used;
   }

   private BDS(BDS var1, byte[] var2, byte[] var3, OTSHashAddress var4) {
      this.wotsPlus = new WOTSPlus(var1.wotsPlus.getParams());
      this.treeHeight = var1.treeHeight;
      this.k = var1.k;
      this.root = var1.root;
      this.authenticationPath = new ArrayList<>();
      this.authenticationPath.addAll(var1.authenticationPath);
      this.retain = new TreeMap<>();

      for (Integer var6 : var1.retain.keySet()) {
         this.retain.put(var6, (LinkedList<XMSSNode>)var1.retain.get(var6).clone());
      }

      this.stack = new Stack<>();
      this.stack.addAll(var1.stack);
      this.treeHashInstances = new ArrayList<>();
      Iterator var7 = var1.treeHashInstances.iterator();

      while (var7.hasNext()) {
         this.treeHashInstances.add(((BDSTreeHash)var7.next()).clone());
      }

      this.keep = new TreeMap<>(var1.keep);
      this.index = var1.index;
      this.maxIndex = var1.maxIndex;
      this.used = false;
      this.nextAuthenticationPath(var2, var3, var4);
   }

   private BDS(BDS var1, ASN1ObjectIdentifier var2) {
      this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(var2));
      this.treeHeight = var1.treeHeight;
      this.k = var1.k;
      this.root = var1.root;
      this.authenticationPath = new ArrayList<>();
      this.authenticationPath.addAll(var1.authenticationPath);
      this.retain = new TreeMap<>();

      for (Integer var4 : var1.retain.keySet()) {
         this.retain.put(var4, (LinkedList<XMSSNode>)var1.retain.get(var4).clone());
      }

      this.stack = new Stack<>();
      this.stack.addAll(var1.stack);
      this.treeHashInstances = new ArrayList<>();
      Iterator var5 = var1.treeHashInstances.iterator();

      while (var5.hasNext()) {
         this.treeHashInstances.add(((BDSTreeHash)var5.next()).clone());
      }

      this.keep = new TreeMap<>(var1.keep);
      this.index = var1.index;
      this.maxIndex = var1.maxIndex;
      this.used = var1.used;
      this.validate();
   }

   private BDS(BDS var1, int var2, ASN1ObjectIdentifier var3) {
      this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(var3));
      this.treeHeight = var1.treeHeight;
      this.k = var1.k;
      this.root = var1.root;
      this.authenticationPath = new ArrayList<>();
      this.authenticationPath.addAll(var1.authenticationPath);
      this.retain = new TreeMap<>();

      for (Integer var5 : var1.retain.keySet()) {
         this.retain.put(var5, (LinkedList<XMSSNode>)var1.retain.get(var5).clone());
      }

      this.stack = new Stack<>();
      this.stack.addAll(var1.stack);
      this.treeHashInstances = new ArrayList<>();
      Iterator var6 = var1.treeHashInstances.iterator();

      while (var6.hasNext()) {
         this.treeHashInstances.add(((BDSTreeHash)var6.next()).clone());
      }

      this.keep = new TreeMap<>(var1.keep);
      this.index = var1.index;
      this.maxIndex = var2;
      this.used = var1.used;
      this.validate();
   }

   public BDS getNextState(byte[] var1, byte[] var2, OTSHashAddress var3) {
      return new BDS(this, var1, var2, var3);
   }

   private void initialize(byte[] var1, byte[] var2, OTSHashAddress var3) {
      if (var3 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else {
         LTreeAddress var4 = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(var3.getLayerAddress()).withTreeAddress(var3.getTreeAddress()).build();
         HashTreeAddress var5 = (HashTreeAddress)new HashTreeAddress.Builder()
            .withLayerAddress(var3.getLayerAddress())
            .withTreeAddress(var3.getTreeAddress())
            .build();

         for (int var6 = 0; var6 < 1 << this.treeHeight; var6++) {
            var3 = (OTSHashAddress)new OTSHashAddress.Builder()
               .withLayerAddress(var3.getLayerAddress())
               .withTreeAddress(var3.getTreeAddress())
               .withOTSAddress(var6)
               .withChainAddress(var3.getChainAddress())
               .withHashAddress(var3.getHashAddress())
               .withKeyAndMask(var3.getKeyAndMask())
               .build();
            this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(var2, var3), var1);
            WOTSPlusPublicKeyParameters var7 = this.wotsPlus.getPublicKey(var3);
            var4 = (LTreeAddress)new LTreeAddress.Builder()
               .withLayerAddress(var4.getLayerAddress())
               .withTreeAddress(var4.getTreeAddress())
               .withLTreeAddress(var6)
               .withTreeHeight(var4.getTreeHeight())
               .withTreeIndex(var4.getTreeIndex())
               .withKeyAndMask(var4.getKeyAndMask())
               .build();
            XMSSNode var8 = XMSSNodeUtil.lTree(this.wotsPlus, var7, var4);
            var5 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var5.getLayerAddress())
               .withTreeAddress(var5.getTreeAddress())
               .withTreeIndex(var6)
               .withKeyAndMask(var5.getKeyAndMask())
               .build();

            while (!this.stack.isEmpty() && this.stack.peek().getHeight() == var8.getHeight()) {
               int var9 = var6 / (1 << var8.getHeight());
               if (var9 == 1) {
                  this.authenticationPath.add(var8);
               }

               if (var9 == 3 && var8.getHeight() < this.treeHeight - this.k) {
                  this.treeHashInstances.get(var8.getHeight()).setNode(var8);
               }

               if (var9 >= 3 && (var9 & 1) == 1 && var8.getHeight() >= this.treeHeight - this.k && var8.getHeight() <= this.treeHeight - 2) {
                  if (this.retain.get(var8.getHeight()) == null) {
                     LinkedList var10 = new LinkedList();
                     var10.add(var8);
                     this.retain.put(var8.getHeight(), var10);
                  } else {
                     this.retain.get(var8.getHeight()).add(var8);
                  }
               }

               var5 = (HashTreeAddress)new HashTreeAddress.Builder()
                  .withLayerAddress(var5.getLayerAddress())
                  .withTreeAddress(var5.getTreeAddress())
                  .withTreeHeight(var5.getTreeHeight())
                  .withTreeIndex((var5.getTreeIndex() - 1) / 2)
                  .withKeyAndMask(var5.getKeyAndMask())
                  .build();
               var8 = XMSSNodeUtil.randomizeHash(this.wotsPlus, this.stack.pop(), var8, var5);
               var8 = new XMSSNode(var8.getHeight() + 1, var8.getValue());
               var5 = (HashTreeAddress)new HashTreeAddress.Builder()
                  .withLayerAddress(var5.getLayerAddress())
                  .withTreeAddress(var5.getTreeAddress())
                  .withTreeHeight(var5.getTreeHeight() + 1)
                  .withTreeIndex(var5.getTreeIndex())
                  .withKeyAndMask(var5.getKeyAndMask())
                  .build();
            }

            this.stack.push(var8);
         }

         this.root = this.stack.pop();
      }
   }

   private void nextAuthenticationPath(byte[] var1, byte[] var2, OTSHashAddress var3) {
      if (var3 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else if (this.used) {
         throw new IllegalStateException("index already used");
      } else if (this.index > this.maxIndex - 1) {
         throw new IllegalStateException("index out of bounds");
      } else {
         int var4 = XMSSUtil.calculateTau(this.index, this.treeHeight);
         if ((this.index >> var4 + 1 & 1) == 0 && var4 < this.treeHeight - 1) {
            this.keep.put(var4, this.authenticationPath.get(var4));
         }

         LTreeAddress var5 = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(var3.getLayerAddress()).withTreeAddress(var3.getTreeAddress()).build();
         HashTreeAddress var6 = (HashTreeAddress)new HashTreeAddress.Builder()
            .withLayerAddress(var3.getLayerAddress())
            .withTreeAddress(var3.getTreeAddress())
            .build();
         if (var4 == 0) {
            var3 = (OTSHashAddress)new OTSHashAddress.Builder()
               .withLayerAddress(var3.getLayerAddress())
               .withTreeAddress(var3.getTreeAddress())
               .withOTSAddress(this.index)
               .withChainAddress(var3.getChainAddress())
               .withHashAddress(var3.getHashAddress())
               .withKeyAndMask(var3.getKeyAndMask())
               .build();
            this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(var2, var3), var1);
            WOTSPlusPublicKeyParameters var7 = this.wotsPlus.getPublicKey(var3);
            var5 = (LTreeAddress)new LTreeAddress.Builder()
               .withLayerAddress(var5.getLayerAddress())
               .withTreeAddress(var5.getTreeAddress())
               .withLTreeAddress(this.index)
               .withTreeHeight(var5.getTreeHeight())
               .withTreeIndex(var5.getTreeIndex())
               .withKeyAndMask(var5.getKeyAndMask())
               .build();
            XMSSNode var8 = XMSSNodeUtil.lTree(this.wotsPlus, var7, var5);
            this.authenticationPath.set(0, var8);
         } else {
            var6 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var6.getLayerAddress())
               .withTreeAddress(var6.getTreeAddress())
               .withTreeHeight(var4 - 1)
               .withTreeIndex(this.index >> var4)
               .withKeyAndMask(var6.getKeyAndMask())
               .build();
            this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(var2, var3), var1);
            XMSSNode var13 = XMSSNodeUtil.randomizeHash(this.wotsPlus, this.authenticationPath.get(var4 - 1), this.keep.get(var4 - 1), var6);
            var13 = new XMSSNode(var13.getHeight() + 1, var13.getValue());
            this.authenticationPath.set(var4, var13);
            this.keep.remove(var4 - 1);

            for (int var16 = 0; var16 < var4; var16++) {
               if (var16 < this.treeHeight - this.k) {
                  this.authenticationPath.set(var16, this.treeHashInstances.get(var16).getTailNode());
               } else {
                  this.authenticationPath.set(var16, this.retain.get(var16).removeFirst());
               }
            }

            int var17 = Math.min(var4, this.treeHeight - this.k);

            for (int var9 = 0; var9 < var17; var9++) {
               int var10 = this.index + 1 + 3 * (1 << var9);
               if (var10 < 1 << this.treeHeight) {
                  this.treeHashInstances.get(var9).initialize(var10);
               }
            }
         }

         for (int var15 = 0; var15 < this.treeHeight - this.k >> 1; var15++) {
            BDSTreeHash var18 = this.getBDSTreeHashInstanceForUpdate();
            if (var18 != null) {
               var18.update(this.stack, this.wotsPlus, var1, var2, var3);
            }
         }

         this.index++;
      }
   }

   boolean isUsed() {
      return this.used;
   }

   void markUsed() {
      this.used = true;
   }

   private BDSTreeHash getBDSTreeHashInstanceForUpdate() {
      BDSTreeHash var1 = null;

      for (BDSTreeHash var3 : this.treeHashInstances) {
         if (!var3.isFinished() && var3.isInitialized()) {
            if (var1 == null) {
               var1 = var3;
            } else if (var3.getHeight() < var1.getHeight()) {
               var1 = var3;
            } else if (var3.getHeight() == var1.getHeight() && var3.getIndexLeaf() < var1.getIndexLeaf()) {
               var1 = var3;
            }
         }
      }

      return var1;
   }

   private void validate() {
      if (this.authenticationPath == null) {
         throw new IllegalStateException("authenticationPath == null");
      } else if (this.retain == null) {
         throw new IllegalStateException("retain == null");
      } else if (this.stack == null) {
         throw new IllegalStateException("stack == null");
      } else if (this.treeHashInstances == null) {
         throw new IllegalStateException("treeHashInstances == null");
      } else if (this.keep == null) {
         throw new IllegalStateException("keep == null");
      } else if (!XMSSUtil.isIndexValid(this.treeHeight, this.index)) {
         throw new IllegalStateException("index in BDS state out of bounds");
      }
   }

   protected int getTreeHeight() {
      return this.treeHeight;
   }

   protected XMSSNode getRoot() {
      return this.root;
   }

   protected List<XMSSNode> getAuthenticationPath() {
      ArrayList var1 = new ArrayList();

      for (XMSSNode var3 : this.authenticationPath) {
         var1.add(var3);
      }

      return var1;
   }

   protected int getIndex() {
      return this.index;
   }

   public int getMaxIndex() {
      return this.maxIndex;
   }

   public BDS withWOTSDigest(ASN1ObjectIdentifier var1) {
      return new BDS(this, var1);
   }

   public BDS withMaxIndex(int var1, ASN1ObjectIdentifier var2) {
      return new BDS(this, var1, var2);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (var1.available() != 0) {
         this.maxIndex = var1.readInt();
      } else {
         this.maxIndex = (1 << this.treeHeight) - 1;
      }

      if (this.maxIndex > (1 << this.treeHeight) - 1 || this.index > this.maxIndex + 1 || var1.available() != 0) {
         throw new IOException("inconsistent BDS data detected");
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.maxIndex);
   }
}
