package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesCtrHmacStreamingKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   AesCtrHmacStreamingParams getParams();

   AesCtrHmacStreamingParamsOrBuilder getParamsOrBuilder();

   ByteString getKeyValue();
}
