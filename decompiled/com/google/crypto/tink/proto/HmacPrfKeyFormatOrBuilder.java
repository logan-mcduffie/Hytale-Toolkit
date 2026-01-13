package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface HmacPrfKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   HmacPrfParams getParams();

   HmacPrfParamsOrBuilder getParamsOrBuilder();

   int getKeySize();

   int getVersion();
}
