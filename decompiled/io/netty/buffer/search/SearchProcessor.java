package io.netty.buffer.search;

import io.netty.util.ByteProcessor;

public interface SearchProcessor extends ByteProcessor {
   void reset();
}
