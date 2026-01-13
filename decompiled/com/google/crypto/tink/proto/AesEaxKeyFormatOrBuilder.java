package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesEaxKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   AesEaxParams getParams();

   AesEaxParamsOrBuilder getParamsOrBuilder();

   int getKeySize();
}
