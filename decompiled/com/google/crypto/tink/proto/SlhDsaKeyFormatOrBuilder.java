package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaKeyFormatOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   SlhDsaParams getParams();

   SlhDsaParamsOrBuilder getParamsOrBuilder();
}
