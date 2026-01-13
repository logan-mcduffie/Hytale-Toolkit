package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HkdfPrfKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   HkdfPrfParams getParams();

   HkdfPrfParamsOrBuilder getParamsOrBuilder();

   ByteString getKeyValue();
}
