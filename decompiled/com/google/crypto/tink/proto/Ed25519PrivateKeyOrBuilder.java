package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface Ed25519PrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   ByteString getKeyValue();

   boolean hasPublicKey();

   Ed25519PublicKey getPublicKey();

   Ed25519PublicKeyOrBuilder getPublicKeyOrBuilder();
}
