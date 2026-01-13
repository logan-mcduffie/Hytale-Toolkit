package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface MlDsaPrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   ByteString getKeyValue();

   boolean hasPublicKey();

   MlDsaPublicKey getPublicKey();

   MlDsaPublicKeyOrBuilder getPublicKeyOrBuilder();
}
