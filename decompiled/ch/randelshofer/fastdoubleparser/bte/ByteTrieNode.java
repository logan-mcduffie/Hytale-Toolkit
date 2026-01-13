package ch.randelshofer.fastdoubleparser.bte;

import java.util.Arrays;

final class ByteTrieNode {
   private byte[] chars = new byte[0];
   private ByteTrieNode[] children = new ByteTrieNode[0];
   private boolean isEnd;

   public ByteTrieNode() {
   }

   public ByteTrieNode insert(byte ch) {
      int index = this.indexOf(ch);
      if (index < 0) {
         index = this.chars.length;
         this.chars = Arrays.copyOf(this.chars, this.chars.length + 1);
         this.children = Arrays.copyOf(this.children, this.children.length + 1);
         this.chars[index] = ch;
         this.children[index] = new ByteTrieNode();
      }

      return this.children[index];
   }

   public ByteTrieNode insert(byte ch, ByteTrieNode forcedNode) {
      int index = this.indexOf(ch);
      if (index < 0) {
         index = this.chars.length;
         this.chars = Arrays.copyOf(this.chars, this.chars.length + 1);
         this.children = Arrays.copyOf(this.children, this.children.length + 1);
         this.chars[index] = ch;
         this.children[index] = forcedNode;
      }

      if (this.children[index] != forcedNode) {
         throw new AssertionError("trie is corrupt");
      } else {
         return this.children[index];
      }
   }

   public ByteTrieNode get(byte ch) {
      int index = this.indexOf(ch);
      return index < 0 ? null : this.children[index];
   }

   private int indexOf(byte ch) {
      int index = -1;

      for (int i = 0; i < this.chars.length; i++) {
         if (this.chars[i] == ch) {
            index = i;
         }
      }

      return index;
   }

   public void setEnd() {
      this.isEnd = true;
   }

   public boolean isEnd() {
      return this.isEnd;
   }
}
