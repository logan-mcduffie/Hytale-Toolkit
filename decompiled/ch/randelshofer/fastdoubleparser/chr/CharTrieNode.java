package ch.randelshofer.fastdoubleparser.chr;

import java.util.Arrays;

final class CharTrieNode {
   private char[] chars = new char[0];
   private CharTrieNode[] children = new CharTrieNode[0];
   private boolean isEnd;

   public CharTrieNode() {
   }

   public CharTrieNode insert(char ch) {
      int index = this.indexOf(ch);
      if (index < 0) {
         index = this.chars.length;
         this.chars = Arrays.copyOf(this.chars, this.chars.length + 1);
         this.children = Arrays.copyOf(this.children, this.children.length + 1);
         this.chars[index] = ch;
         this.children[index] = new CharTrieNode();
      }

      return this.children[index];
   }

   public CharTrieNode get(char ch) {
      int index = this.indexOf(ch);
      return index < 0 ? null : this.children[index];
   }

   private int indexOf(char ch) {
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

   public CharTrieNode insert(char ch, CharTrieNode forcedNode) {
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
}
