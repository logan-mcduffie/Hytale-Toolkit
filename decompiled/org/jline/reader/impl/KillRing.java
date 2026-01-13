package org.jline.reader.impl;

public final class KillRing {
   private static final int DEFAULT_SIZE = 60;
   private final String[] slots;
   private int head = 0;
   private boolean lastKill = false;
   private boolean lastYank = false;

   public KillRing(int size) {
      this.slots = new String[size];
   }

   public KillRing() {
      this(60);
   }

   public void resetLastYank() {
      this.lastYank = false;
   }

   public void resetLastKill() {
      this.lastKill = false;
   }

   public boolean lastYank() {
      return this.lastYank;
   }

   public void add(String str) {
      this.lastYank = false;
      if (this.lastKill && this.slots[this.head] != null) {
         this.slots[this.head] = this.slots[this.head] + str;
      } else {
         this.lastKill = true;
         this.next();
         this.slots[this.head] = str;
      }
   }

   public void addBackwards(String str) {
      this.lastYank = false;
      if (this.lastKill && this.slots[this.head] != null) {
         this.slots[this.head] = str + this.slots[this.head];
      } else {
         this.lastKill = true;
         this.next();
         this.slots[this.head] = str;
      }
   }

   public String yank() {
      this.lastKill = false;
      this.lastYank = true;
      return this.slots[this.head];
   }

   public String yankPop() {
      this.lastKill = false;
      if (this.lastYank) {
         this.prev();
         return this.slots[this.head];
      } else {
         return null;
      }
   }

   private void next() {
      if (this.head != 0 || this.slots[0] != null) {
         this.head++;
         if (this.head == this.slots.length) {
            this.head = 0;
         }
      }
   }

   private void prev() {
      this.head--;
      if (this.head == -1) {
         int x = this.slots.length - 1;

         while (x >= 0 && this.slots[x] == null) {
            x--;
         }

         this.head = x;
      }
   }
}
