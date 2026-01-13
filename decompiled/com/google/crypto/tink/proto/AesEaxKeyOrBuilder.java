package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesEaxKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   AesEaxParams getParams();

   AesEaxParamsOrBuilder getParamsOrBuilder();

   ByteString getKeyValue();
}
