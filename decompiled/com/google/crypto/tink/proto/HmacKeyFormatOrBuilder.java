package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface HmacKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   HmacParams getParams();

   HmacParamsOrBuilder getParamsOrBuilder();

   int getKeySize();

   int getVersion();
}
