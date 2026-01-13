package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesCtrKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   AesCtrParams getParams();

   AesCtrParamsOrBuilder getParamsOrBuilder();

   int getKeySize();
}
