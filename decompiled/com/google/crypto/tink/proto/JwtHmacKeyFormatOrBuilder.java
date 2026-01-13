package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface JwtHmacKeyFormatOrBuilder extends MessageOrBuilder {
   int getVersion();

   int getAlgorithmValue();

   JwtHmacAlgorithm getAlgorithm();

   int getKeySize();
}
