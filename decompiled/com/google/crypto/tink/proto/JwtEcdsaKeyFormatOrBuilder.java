package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface JwtEcdsaKeyFormatOrBuilder extends MessageOrBuilder {
   int getVersion();

   int getAlgorithmValue();

   JwtEcdsaAlgorithm getAlgorithm();
}
