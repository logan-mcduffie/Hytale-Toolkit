package org.bson.types;

import java.io.Serializable;
import java.util.Date;

public final class BSONTimestamp implements Comparable<BSONTimestamp>, Serializable {
   private static final long serialVersionUID = -3268482672267936464L;
   private final int inc;
   private final Date time;

   public BSONTimestamp() {
      this.inc = 0;
      this.time = null;
   }

   public BSONTimestamp(int time, int increment) {
      this.time = new Date(time * 1000L);
      this.inc = increment;
   }

   public int getTime() {
      return this.time == null ? 0 : (int)(this.time.getTime() / 1000L);
   }

   public int getInc() {
      return this.inc;
   }

   @Override
   public String toString() {
      return "TS time:" + this.time + " inc:" + this.inc;
   }

   public int compareTo(BSONTimestamp ts) {
      return this.getTime() != ts.getTime() ? this.getTime() - ts.getTime() : this.getInc() - ts.getInc();
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = prime * result + this.inc;
      return prime * result + this.getTime();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof BSONTimestamp)) {
         return false;
      } else {
         BSONTimestamp t2 = (BSONTimestamp)obj;
         return this.getTime() == t2.getTime() && this.getInc() == t2.getInc();
      }
   }
}
