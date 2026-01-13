package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaPrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   ByteString getKeyValue();

   boolean hasPublicKey();

   SlhDsaPublicKey getPublicKey();

   SlhDsaPublicKeyOrBuilder getPublicKeyOrBuilder();
}
