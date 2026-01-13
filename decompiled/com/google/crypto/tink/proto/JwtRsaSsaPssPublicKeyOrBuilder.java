package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPssPublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   int getAlgorithmValue();

   JwtRsaSsaPssAlgorithm getAlgorithm();

   ByteString getN();

   ByteString getE();

   boolean hasCustomKid();

   JwtRsaSsaPssPublicKey.CustomKid getCustomKid();

   JwtRsaSsaPssPublicKey.CustomKidOrBuilder getCustomKidOrBuilder();
}
