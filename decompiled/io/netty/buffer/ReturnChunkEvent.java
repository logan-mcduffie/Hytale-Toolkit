package io.netty.buffer;

import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label("Chunk Return")
@Name("io.netty.ReturnChunk")
@Description("Triggered when a memory chunk is prepared for re-use by an allocator")
final class ReturnChunkEvent extends AbstractChunkEvent {
   static final String NAME = "io.netty.ReturnChunk";
   private static final ReturnChunkEvent INSTANCE = new ReturnChunkEvent();
   @Description("Was this chunk returned to its previous magazine?")
   public boolean returnedToMagazine;

   public static boolean isEventEnabled() {
      return INSTANCE.isEnabled();
   }
}
