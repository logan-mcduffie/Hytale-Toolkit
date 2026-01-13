package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtHmacKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   int getAlgorithmValue();

   JwtHmacAlgorithm getAlgorithm();

   ByteString getKeyValue();

   boolean hasCustomKid();

   JwtHmacKey.CustomKid getCustomKid();

   JwtHmacKey.CustomKidOrBuilder getCustomKidOrBuilder();
}
