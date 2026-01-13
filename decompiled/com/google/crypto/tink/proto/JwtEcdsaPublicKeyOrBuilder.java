package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtEcdsaPublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   int getAlgorithmValue();

   JwtEcdsaAlgorithm getAlgorithm();

   ByteString getX();

   ByteString getY();

   boolean hasCustomKid();

   JwtEcdsaPublicKey.CustomKid getCustomKid();

   JwtEcdsaPublicKey.CustomKidOrBuilder getCustomKidOrBuilder();
}
