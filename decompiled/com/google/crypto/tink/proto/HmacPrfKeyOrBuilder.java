package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HmacPrfKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   HmacPrfParams getParams();

   HmacPrfParamsOrBuilder getParamsOrBuilder();

   ByteString getKeyValue();
}
