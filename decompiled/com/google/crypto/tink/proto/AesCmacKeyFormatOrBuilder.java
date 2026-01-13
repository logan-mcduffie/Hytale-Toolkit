package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesCmacKeyFormatOrBuilder extends MessageOrBuilder {
   int getKeySize();

   boolean hasParams();

   AesCmacParams getParams();

   AesCmacParamsOrBuilder getParamsOrBuilder();
}
