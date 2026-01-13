package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HmacKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   HmacParams getParams();

   HmacParamsOrBuilder getParamsOrBuilder();

   ByteString getKeyValue();
}
