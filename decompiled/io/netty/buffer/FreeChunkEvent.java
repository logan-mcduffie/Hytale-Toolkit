package io.netty.buffer;

import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label("Chunk Free")
@Name("io.netty.FreeChunk")
@Description("Triggered when a memory chunk is freed from an allocator")
final class FreeChunkEvent extends AbstractChunkEvent {
   static final String NAME = "io.netty.FreeChunk";
   private static final FreeChunkEvent INSTANCE = new FreeChunkEvent();
   @Description("Was this chunk pooled, or was it a one-off allocation for a single buffer?")
   public boolean pooled;

   public static boolean isEventEnabled() {
      return INSTANCE.isEnabled();
   }
}
