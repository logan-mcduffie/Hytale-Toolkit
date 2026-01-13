package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesGcmHkdfStreamingKeyFormatOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   AesGcmHkdfStreamingParams getParams();

   AesGcmHkdfStreamingParamsOrBuilder getParamsOrBuilder();

   int getKeySize();
}
