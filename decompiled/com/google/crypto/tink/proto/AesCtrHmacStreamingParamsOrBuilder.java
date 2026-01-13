package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesCtrHmacStreamingParamsOrBuilder extends MessageOrBuilder {
   int getCiphertextSegmentSize();

   int getDerivedKeySize();

   int getHkdfHashTypeValue();

   HashType getHkdfHashType();

   boolean hasHmacParams();

   HmacParams getHmacParams();

   HmacParamsOrBuilder getHmacParamsOrBuilder();
}
