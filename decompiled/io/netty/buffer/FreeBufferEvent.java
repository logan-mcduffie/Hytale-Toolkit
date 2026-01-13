package io.netty.buffer;

import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label("Buffer Deallocation")
@Name("io.netty.FreeBuffer")
@Description("Triggered when a buffer is freed from an allocator")
final class FreeBufferEvent extends AbstractBufferEvent {
   private static final FreeBufferEvent INSTANCE = new FreeBufferEvent();
   static final String NAME = "io.netty.FreeBuffer";

   public static boolean isEventEnabled() {
      return INSTANCE.isEnabled();
   }
}
