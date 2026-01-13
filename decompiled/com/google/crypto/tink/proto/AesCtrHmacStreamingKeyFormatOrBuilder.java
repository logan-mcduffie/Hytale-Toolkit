package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesCtrHmacStreamingKeyFormatOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   AesCtrHmacStreamingParams getParams();

   AesCtrHmacStreamingParamsOrBuilder getParamsOrBuilder();

   int getKeySize();
}
