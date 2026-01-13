package io.netty.handler.codec.compression;

final class Bzip2HuffmanAllocator {
   private static int first(int[] array, int i, int nodesToMove) {
      int length = array.length;
      int limit = i;

      int k;
      for (k = array.length - 2; i >= nodesToMove && array[i] % length > limit; i -= limit - i + 1) {
         k = i;
      }

      i = Math.max(nodesToMove - 1, i);

      while (k > i + 1) {
         int temp = i + k >>> 1;
         if (array[temp] % length > limit) {
            k = temp;
         } else {
            i = temp;
         }
      }

      return k;
   }

   private static void setExtendedParentPointers(int[] array) {
      int length = array.length;
      array[0] += array[1];
      int headNode = 0;
      int tailNode = 1;

      for (int topNode = 2; tailNode < length - 1; tailNode++) {
         int temp;
         if (topNode < length && array[headNode] >= array[topNode]) {
            temp = array[topNode++];
         } else {
            temp = array[headNode];
            array[headNode++] = tailNode;
         }

         if (topNode < length && (headNode >= tailNode || array[headNode] >= array[topNode])) {
            temp += array[topNode++];
         } else {
            temp += array[headNode];
            array[headNode++] = tailNode + length;
         }

         array[tailNode] = temp;
      }
   }

   private static int findNodesToRelocate(int[] array, int maximumLength) {
      int currentNode = array.length - 2;

      for (int currentDepth = 1; currentDepth < maximumLength - 1 && currentNode > 1; currentDepth++) {
         currentNode = first(array, currentNode - 1, 0);
      }

      return currentNode;
   }

   private static void allocateNodeLengths(int[] array) {
      int firstNode = array.length - 2;
      int nextNode = array.length - 1;
      int currentDepth = 1;

      for (int availableNodes = 2; availableNodes > 0; currentDepth++) {
         int lastNode = firstNode;
         firstNode = first(array, firstNode - 1, 0);

         for (int i = availableNodes - (lastNode - firstNode); i > 0; i--) {
            array[nextNode--] = currentDepth;
         }

         availableNodes = lastNode - firstNode << 1;
      }
   }

   private static void allocateNodeLengthsWithRelocation(int[] array, int nodesToMove, int insertDepth) {
      int firstNode = array.length - 2;
      int nextNode = array.length - 1;
      int currentDepth = insertDepth == 1 ? 2 : 1;
      int nodesLeftToMove = insertDepth == 1 ? nodesToMove - 2 : nodesToMove;

      for (int availableNodes = currentDepth << 1; availableNodes > 0; currentDepth++) {
         int lastNode = firstNode;
         firstNode = firstNode <= nodesToMove ? firstNode : first(array, firstNode - 1, nodesToMove);
         int offset = 0;
         if (currentDepth >= insertDepth) {
            offset = Math.min(nodesLeftToMove, 1 << currentDepth - insertDepth);
         } else if (currentDepth == insertDepth - 1) {
            offset = 1;
            if (array[firstNode] == lastNode) {
               firstNode++;
            }
         }

         for (int i = availableNodes - (lastNode - firstNode + offset); i > 0; i--) {
            array[nextNode--] = currentDepth;
         }

         nodesLeftToMove -= offset;
         availableNodes = lastNode - firstNode + offset << 1;
      }
   }

   static void allocateHuffmanCodeLengths(int[] array, int maximumLength) {
      switch (array.length) {
         case 2:
            array[1] = 1;
         case 1:
            array[0] = 1;
            return;
         default:
            setExtendedParentPointers(array);
            int nodesToRelocate = findNodesToRelocate(array, maximumLength);
            if (array[0] % array.length >= nodesToRelocate) {
               allocateNodeLengths(array);
            } else {
               int insertDepth = maximumLength - (32 - Integer.numberOfLeadingZeros(nodesToRelocate - 1));
               allocateNodeLengthsWithRelocation(array, nodesToRelocate, insertDepth);
            }
      }
   }

   private Bzip2HuffmanAllocator() {
   }
}
