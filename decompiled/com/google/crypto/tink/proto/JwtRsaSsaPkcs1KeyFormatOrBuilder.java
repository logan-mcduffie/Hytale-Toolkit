package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPkcs1KeyFormatOrBuilder extends MessageOrBuilder {
   int getVersion();

   int getAlgorithmValue();

   JwtRsaSsaPkcs1Algorithm getAlgorithm();

   int getModulusSizeInBits();

   ByteString getPublicExponent();
}
