package org.bson.json;

class JsonStringBuffer implements JsonBuffer {
   private final String buffer;
   private int position;
   private boolean eof;

   JsonStringBuffer(String buffer) {
      this.buffer = buffer;
   }

   @Override
   public int getPosition() {
      return this.position;
   }

   @Override
   public int read() {
      if (this.eof) {
         throw new JsonParseException("Trying to read past EOF.");
      } else if (this.position >= this.buffer.length()) {
         this.eof = true;
         return -1;
      } else {
         return this.buffer.charAt(this.position++);
      }
   }

   @Override
   public void unread(int c) {
      this.eof = false;
      if (c != -1 && this.buffer.charAt(this.position - 1) == c) {
         this.position--;
      }
   }

   @Override
   public int mark() {
      return this.position;
   }

   @Override
   public void reset(int markPos) {
      if (markPos > this.position) {
         throw new IllegalStateException("mark cannot reset ahead of position, only back");
      } else {
         this.position = markPos;
      }
   }

   @Override
   public void discard(int markPos) {
   }
}
