package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface XAesGcmKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   XAesGcmParams getParams();

   XAesGcmParamsOrBuilder getParamsOrBuilder();

   ByteString getKeyValue();
}
