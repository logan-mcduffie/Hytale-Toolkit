package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPkcs1PublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   int getAlgorithmValue();

   JwtRsaSsaPkcs1Algorithm getAlgorithm();

   ByteString getN();

   ByteString getE();

   boolean hasCustomKid();

   JwtRsaSsaPkcs1PublicKey.CustomKid getCustomKid();

   JwtRsaSsaPkcs1PublicKey.CustomKidOrBuilder getCustomKidOrBuilder();
}
