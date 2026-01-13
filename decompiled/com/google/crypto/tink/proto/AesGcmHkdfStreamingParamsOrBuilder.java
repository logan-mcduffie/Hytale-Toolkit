package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesGcmHkdfStreamingParamsOrBuilder extends MessageOrBuilder {
   int getCiphertextSegmentSize();

   int getDerivedKeySize();

   int getHkdfHashTypeValue();

   HashType getHkdfHashType();
}
