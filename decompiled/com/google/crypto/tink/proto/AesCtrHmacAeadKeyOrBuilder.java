package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesCtrHmacAeadKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasAesCtrKey();

   AesCtrKey getAesCtrKey();

   AesCtrKeyOrBuilder getAesCtrKeyOrBuilder();

   boolean hasHmacKey();

   HmacKey getHmacKey();

   HmacKeyOrBuilder getHmacKeyOrBuilder();
}
