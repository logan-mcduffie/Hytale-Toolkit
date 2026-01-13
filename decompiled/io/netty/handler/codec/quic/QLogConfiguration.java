package io.netty.handler.codec.quic;

import java.util.Objects;

public final class QLogConfiguration {
   private final String path;
   private final String logTitle;
   private final String logDescription;

   public QLogConfiguration(String path, String logTitle, String logDescription) {
      this.path = Objects.requireNonNull(path, "path");
      this.logTitle = Objects.requireNonNull(logTitle, "logTitle");
      this.logDescription = Objects.requireNonNull(logDescription, "logDescription");
   }

   public String path() {
      return this.path;
   }

   public String logTitle() {
      return this.logTitle;
   }

   public String logDescription() {
      return this.logDescription;
   }
}
