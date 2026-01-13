package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface Ed25519PublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   ByteString getKeyValue();
}
