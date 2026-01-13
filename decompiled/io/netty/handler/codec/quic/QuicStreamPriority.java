package io.netty.handler.codec.quic;

import io.netty.util.internal.ObjectUtil;
import java.util.Objects;

public final class QuicStreamPriority {
   private final int urgency;
   private final boolean incremental;

   public QuicStreamPriority(int urgency, boolean incremental) {
      this.urgency = ObjectUtil.checkInRange(urgency, 0, 127, "urgency");
      this.incremental = incremental;
   }

   public int urgency() {
      return this.urgency;
   }

   public boolean isIncremental() {
      return this.incremental;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         QuicStreamPriority that = (QuicStreamPriority)o;
         return this.urgency == that.urgency && this.incremental == that.incremental;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.urgency, this.incremental);
   }

   @Override
   public String toString() {
      return "QuicStreamPriority{urgency=" + this.urgency + ", incremental=" + this.incremental + '}';
   }
}
