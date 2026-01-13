package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtEcdsaPrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasPublicKey();

   JwtEcdsaPublicKey getPublicKey();

   JwtEcdsaPublicKeyOrBuilder getPublicKeyOrBuilder();

   ByteString getKeyValue();
}
