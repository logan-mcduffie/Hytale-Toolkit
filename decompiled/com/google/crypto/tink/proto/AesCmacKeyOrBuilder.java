package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesCmacKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   ByteString getKeyValue();

   boolean hasParams();

   AesCmacParams getParams();

   AesCmacParamsOrBuilder getParamsOrBuilder();
}
