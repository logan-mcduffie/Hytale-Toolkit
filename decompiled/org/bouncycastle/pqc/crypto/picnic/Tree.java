package org.bouncycastle.pqc.crypto.picnic;

import java.util.logging.Logger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class Tree {
   private static final Logger LOG = Logger.getLogger(Tree.class.getName());
   private static final int MAX_SEED_SIZE_BYTES = 32;
   private int depth;
   byte[][] nodes;
   private int dataSize;
   private boolean[] haveNode;
   private boolean[] exists;
   private int numNodes;
   private int numLeaves;
   private PicnicEngine engine;

   protected byte[][] getLeaves() {
      return this.nodes;
   }

   protected int getLeavesOffset() {
      return this.numNodes - this.numLeaves;
   }

   public Tree(PicnicEngine var1, int var2, int var3) {
      this.engine = var1;
      this.depth = Utils.ceil_log2(var2) + 1;
      this.numNodes = (1 << this.depth) - 1 - ((1 << this.depth - 1) - var2);
      this.numLeaves = var2;
      this.dataSize = var3;
      this.nodes = new byte[this.numNodes][var3];

      for (int var4 = 0; var4 < this.numNodes; var4++) {
         this.nodes[var4] = new byte[var3];
      }

      this.haveNode = new boolean[this.numNodes];
      this.exists = new boolean[this.numNodes];
      Arrays.fill(this.exists, this.numNodes - this.numLeaves, this.numNodes, true);

      for (int var5 = this.numNodes - this.numLeaves; var5 > 0; var5--) {
         if (this.exists(2 * var5 + 1) || this.exists(2 * var5 + 2)) {
            this.exists[var5] = true;
         }
      }

      this.exists[0] = true;
   }

   protected void buildMerkleTree(byte[][] var1, byte[] var2) {
      int var3 = this.numNodes - this.numLeaves;

      for (int var4 = 0; var4 < this.numLeaves; var4++) {
         if (var1[var4] != null) {
            System.arraycopy(var1[var4], 0, this.nodes[var3 + var4], 0, this.dataSize);
            this.haveNode[var3 + var4] = true;
         }
      }

      for (int var5 = this.numNodes; var5 > 0; var5--) {
         this.computeParentHash(var5, var2);
      }
   }

   protected int verifyMerkleTree(byte[][] var1, byte[] var2) {
      int var3 = this.numNodes - this.numLeaves;

      for (int var4 = 0; var4 < this.numLeaves; var4++) {
         if (var1[var4] != null) {
            if (this.haveNode[var3 + var4]) {
               return -1;
            }

            if (var1[var4] != null) {
               System.arraycopy(var1[var4], 0, this.nodes[var3 + var4], 0, this.dataSize);
               this.haveNode[var3 + var4] = true;
            }
         }
      }

      for (int var5 = this.numNodes; var5 > 0; var5--) {
         this.computeParentHash(var5, var2);
      }

      return !this.haveNode[0] ? -1 : 0;
   }

   protected int reconstructSeeds(int[] var1, int var2, byte[] var3, int var4, byte[] var5, int var6) {
      byte var7 = 0;
      int var8 = var4;
      int[] var9 = new int[]{0};
      int[] var10 = this.getRevealedNodes(var1, var2, var9);

      for (int var11 = 0; var11 < var9[0]; var11++) {
         var8 -= this.engine.seedSizeBytes;
         if (var8 < 0) {
            return -1;
         }

         System.arraycopy(var3, var11 * this.engine.seedSizeBytes, this.nodes[var10[var11]], 0, this.engine.seedSizeBytes);
         this.haveNode[var10[var11]] = true;
      }

      this.expandSeeds(var5, var6);
      return var7;
   }

   protected byte[] openMerkleTree(int[] var1, int var2, int[] var3) {
      int[] var4 = new int[1];
      int[] var5 = this.getRevealedMerkleNodes(var1, var2, var4);
      var3[0] = var4[0] * this.dataSize;
      byte[] var6 = new byte[var3[0]];

      for (int var8 = 0; var8 < var4[0]; var8++) {
         System.arraycopy(this.nodes[var5[var8]], 0, var6, var8 * this.dataSize, this.dataSize);
      }

      return var6;
   }

   private int[] getRevealedNodes(int[] var1, int var2, int[] var3) {
      int var4 = this.depth - 1;
      int[][] var5 = new int[var4][var2];

      for (int var6 = 0; var6 < var2; var6++) {
         int var7 = 0;
         int var8 = var1[var6] + (this.numNodes - this.numLeaves);
         var5[var7][var6] = var8;
         var7++;

         while ((var8 = this.getParent(var8)) != 0) {
            var5[var7][var6] = var8;
            var7++;
         }
      }

      int[] var11 = new int[this.numLeaves];
      int var13 = 0;

      for (int var14 = 0; var14 < var4; var14++) {
         for (int var9 = 0; var9 < var2; var9++) {
            if (this.hasSibling(var5[var14][var9])) {
               int var10 = this.getSibling(var5[var14][var9]);
               if (!this.contains(var5[var14], var2, var10)) {
                  while (!this.hasRightChild(var10) && !this.isLeafNode(var10)) {
                     var10 = 2 * var10 + 1;
                  }

                  if (!this.contains(var11, var13, var10)) {
                     var11[var13] = var10;
                     var13++;
                  }
               }
            }
         }
      }

      var3[0] = var13;
      return var11;
   }

   private int getSibling(int var1) {
      if (this.isLeftChild(var1)) {
         if (var1 + 1 < this.numNodes) {
            return var1 + 1;
         } else {
            LOG.fine("getSibling: request for node with not sibling");
            return 0;
         }
      } else {
         return var1 - 1;
      }
   }

   private boolean isLeafNode(int var1) {
      return 2 * var1 + 1 >= this.numNodes;
   }

   private boolean hasSibling(int var1) {
      return !this.exists(var1) ? false : !this.isLeftChild(var1) || this.exists(var1 + 1);
   }

   protected int revealSeedsSize(int[] var1, int var2) {
      int[] var3 = new int[]{0};
      this.getRevealedNodes(var1, var2, var3);
      return var3[0] * this.engine.seedSizeBytes;
   }

   protected int revealSeeds(int[] var1, int var2, byte[] var3, int var4) {
      int[] var5 = new int[]{0};
      int var6 = var4;
      int[] var7 = this.getRevealedNodes(var1, var2, var5);

      for (int var8 = 0; var8 < var5[0]; var8++) {
         var6 -= this.engine.seedSizeBytes;
         if (var6 < 0) {
            LOG.fine("Insufficient sized buffer provided to revealSeeds");
            return 0;
         }

         System.arraycopy(this.nodes[var7[var8]], 0, var3, var8 * this.engine.seedSizeBytes, this.engine.seedSizeBytes);
      }

      return var3.length - var6;
   }

   protected int openMerkleTreeSize(int[] var1, int var2) {
      int[] var3 = new int[1];
      this.getRevealedMerkleNodes(var1, var2, var3);
      return var3[0] * this.engine.digestSizeBytes;
   }

   private int[] getRevealedMerkleNodes(int[] var1, int var2, int[] var3) {
      int var4 = this.numNodes - this.numLeaves;
      boolean[] var5 = new boolean[this.numNodes];

      for (int var6 = 0; var6 < var2; var6++) {
         var5[var4 + var1[var6]] = true;
      }

      int var11 = this.getParent(this.numNodes - 1);

      for (int var7 = var11; var7 > 0; var7--) {
         if (this.exists(var7)) {
            if (this.exists(2 * var7 + 2)) {
               if (var5[2 * var7 + 1] && var5[2 * var7 + 2]) {
                  var5[var7] = true;
               }
            } else if (var5[2 * var7 + 1]) {
               var5[var7] = true;
            }
         }
      }

      int[] var12 = new int[this.numLeaves];
      int var8 = 0;

      label43:
      for (int var9 = 0; var9 < var2; var9++) {
         int var10 = var1[var9] + var4;

         while (var5[this.getParent(var10)]) {
            if ((var10 = this.getParent(var10)) == 0) {
               continue label43;
            }
         }

         if (!this.contains(var12, var8, var10)) {
            var12[var8] = var10;
            var8++;
         }
      }

      var3[0] = var8;
      return var12;
   }

   private boolean contains(int[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var2; var4++) {
         if (var1[var4] == var3) {
            return true;
         }
      }

      return false;
   }

   private void computeParentHash(int var1, byte[] var2) {
      if (this.exists(var1)) {
         int var3 = this.getParent(var1);
         if (!this.haveNode[var3]) {
            if (this.haveNode[2 * var3 + 1]) {
               if (!this.exists(2 * var3 + 2) || this.haveNode[2 * var3 + 2]) {
                  this.engine.digest.update((byte)3);
                  this.engine.digest.update(this.nodes[2 * var3 + 1], 0, this.engine.digestSizeBytes);
                  if (this.hasRightChild(var3)) {
                     this.engine.digest.update(this.nodes[2 * var3 + 2], 0, this.engine.digestSizeBytes);
                  }

                  this.engine.digest.update(var2, 0, 32);
                  this.engine.digest.update(Pack.intToLittleEndian(var3), 0, 2);
                  this.engine.digest.doFinal(this.nodes[var3], 0, this.engine.digestSizeBytes);
                  this.haveNode[var3] = true;
               }
            }
         }
      }
   }

   protected byte[] getLeaf(int var1) {
      int var2 = this.numNodes - this.numLeaves;
      return this.nodes[var2 + var1];
   }

   protected int addMerkleNodes(int[] var1, int var2, byte[] var3, int var4) {
      int var5 = var4;
      int[] var6 = new int[]{0};
      int[] var7 = this.getRevealedMerkleNodes(var1, var2, var6);

      for (int var8 = 0; var8 < var6[0]; var8++) {
         var5 -= this.dataSize;
         if (var5 < 0) {
            return -1;
         }

         System.arraycopy(var3, var8 * this.dataSize, this.nodes[var7[var8]], 0, this.dataSize);
         this.haveNode[var7[var8]] = true;
      }

      return var5 != 0 ? -1 : 0;
   }

   protected void generateSeeds(byte[] var1, byte[] var2, int var3) {
      this.nodes[0] = var1;
      this.haveNode[0] = true;
      this.expandSeeds(var2, var3);
   }

   private void expandSeeds(byte[] var1, int var2) {
      byte[] var3 = new byte[64];
      int var4 = this.getParent(this.numNodes - 1);

      for (int var5 = 0; var5 <= var4; var5++) {
         if (this.haveNode[var5]) {
            this.hashSeed(var3, this.nodes[var5], var1, (byte)1, var2, var5);
            if (!this.haveNode[2 * var5 + 1]) {
               System.arraycopy(var3, 0, this.nodes[2 * var5 + 1], 0, this.engine.seedSizeBytes);
               this.haveNode[2 * var5 + 1] = true;
            }

            if (this.exists(2 * var5 + 2) && !this.haveNode[2 * var5 + 2]) {
               System.arraycopy(var3, this.engine.seedSizeBytes, this.nodes[2 * var5 + 2], 0, this.engine.seedSizeBytes);
               this.haveNode[2 * var5 + 2] = true;
            }
         }
      }
   }

   private void hashSeed(byte[] var1, byte[] var2, byte[] var3, byte var4, int var5, int var6) {
      this.engine.digest.update(var4);
      this.engine.digest.update(var2, 0, this.engine.seedSizeBytes);
      this.engine.digest.update(var3, 0, 32);
      this.engine.digest.update(Pack.shortToLittleEndian((short)(var5 & 65535)), 0, 2);
      this.engine.digest.update(Pack.shortToLittleEndian((short)(var6 & 65535)), 0, 2);
      this.engine.digest.doFinal(var1, 0, 2 * this.engine.seedSizeBytes);
   }

   private boolean isLeftChild(int var1) {
      return var1 % 2 == 1;
   }

   private boolean hasRightChild(int var1) {
      return 2 * var1 + 2 < this.numNodes && this.exists(var1);
   }

   boolean hasLeftChild(Tree var1, int var2) {
      return 2 * var2 + 1 < this.numNodes;
   }

   private int getParent(int var1) {
      return this.isLeftChild(var1) ? (var1 - 1) / 2 : (var1 - 2) / 2;
   }

   private boolean exists(int var1) {
      return var1 >= this.numNodes ? false : this.exists[var1];
   }
}
