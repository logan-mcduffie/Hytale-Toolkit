package io.netty.channel.unix;

import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.net.SocketAddress;

public class DomainSocketAddress extends SocketAddress {
   private static final long serialVersionUID = -6934618000832236893L;
   private final String socketPath;

   public DomainSocketAddress(String socketPath) {
      this.socketPath = ObjectUtil.checkNotNull(socketPath, "socketPath");
   }

   public DomainSocketAddress(File file) {
      this(file.getPath());
   }

   public String path() {
      return this.socketPath;
   }

   @Override
   public String toString() {
      return this.path();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof DomainSocketAddress) ? false : ((DomainSocketAddress)o).socketPath.equals(this.socketPath);
      }
   }

   @Override
   public int hashCode() {
      return this.socketPath.hashCode();
   }
}
