package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HpkePrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasPublicKey();

   HpkePublicKey getPublicKey();

   HpkePublicKeyOrBuilder getPublicKeyOrBuilder();

   ByteString getPrivateKey();
}
