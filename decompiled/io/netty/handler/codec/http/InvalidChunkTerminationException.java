package io.netty.handler.codec.http;

import io.netty.handler.codec.CorruptedFrameException;

public final class InvalidChunkTerminationException extends CorruptedFrameException {
   private static final long serialVersionUID = 536224937231200736L;

   public InvalidChunkTerminationException() {
      super("Chunk data sections must be terminated by a CR LF octet pair");
   }

   public InvalidChunkTerminationException(String message, Throwable cause) {
      super(message, cause);
   }

   public InvalidChunkTerminationException(String message) {
      super(message);
   }

   public InvalidChunkTerminationException(Throwable cause) {
      super(cause);
   }
}
