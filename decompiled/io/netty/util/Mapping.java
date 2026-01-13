package io.netty.util;

public interface Mapping<IN, OUT> {
   OUT map(IN var1);
}
