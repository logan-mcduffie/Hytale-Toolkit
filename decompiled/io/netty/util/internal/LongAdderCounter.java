package io.netty.util.internal;

import java.util.concurrent.atomic.LongAdder;

@Deprecated
final class LongAdderCounter extends LongAdder implements LongCounter {
   @Override
   public long value() {
      return this.longValue();
   }
}
