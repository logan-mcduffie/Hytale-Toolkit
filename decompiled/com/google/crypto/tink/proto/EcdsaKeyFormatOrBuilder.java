package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface EcdsaKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   EcdsaParams getParams();

   EcdsaParamsOrBuilder getParamsOrBuilder();

   int getVersion();
}
