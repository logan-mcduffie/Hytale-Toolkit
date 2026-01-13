package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface HkdfPrfKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   HkdfPrfParams getParams();

   HkdfPrfParamsOrBuilder getParamsOrBuilder();

   int getKeySize();

   int getVersion();
}
