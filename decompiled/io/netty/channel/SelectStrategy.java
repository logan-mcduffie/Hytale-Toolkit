package io.netty.channel;

import io.netty.util.IntSupplier;

public interface SelectStrategy {
   int SELECT = -1;
   int CONTINUE = -2;
   int BUSY_WAIT = -3;

   int calculateStrategy(IntSupplier var1, boolean var2) throws Exception;
}
