package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EciesAeadHkdfPrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasPublicKey();

   EciesAeadHkdfPublicKey getPublicKey();

   EciesAeadHkdfPublicKeyOrBuilder getPublicKeyOrBuilder();

   ByteString getKeyValue();
}
