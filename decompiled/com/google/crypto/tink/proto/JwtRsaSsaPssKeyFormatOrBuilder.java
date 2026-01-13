package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPssKeyFormatOrBuilder extends MessageOrBuilder {
   int getVersion();

   int getAlgorithmValue();

   JwtRsaSsaPssAlgorithm getAlgorithm();

   int getModulusSizeInBits();

   ByteString getPublicExponent();
}
