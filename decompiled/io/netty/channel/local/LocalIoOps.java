package io.netty.channel.local;

import io.netty.channel.IoOps;

public final class LocalIoOps implements IoOps {
   public static final LocalIoOps DEFAULT = new LocalIoOps();

   private LocalIoOps() {
   }
}
