package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesGcmHkdfStreamingKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   AesGcmHkdfStreamingParams getParams();

   AesGcmHkdfStreamingParamsOrBuilder getParamsOrBuilder();

   ByteString getKeyValue();
}
