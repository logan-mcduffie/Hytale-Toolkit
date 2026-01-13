package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesCtrKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   AesCtrParams getParams();

   AesCtrParamsOrBuilder getParamsOrBuilder();

   ByteString getKeyValue();
}
