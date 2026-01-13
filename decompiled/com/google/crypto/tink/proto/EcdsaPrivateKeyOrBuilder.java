package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EcdsaPrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasPublicKey();

   EcdsaPublicKey getPublicKey();

   EcdsaPublicKeyOrBuilder getPublicKeyOrBuilder();

   ByteString getKeyValue();
}
