package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface MlDsaKeyFormatOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   MlDsaParams getParams();

   MlDsaParamsOrBuilder getParamsOrBuilder();
}
