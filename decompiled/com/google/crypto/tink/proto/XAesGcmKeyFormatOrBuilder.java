package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface XAesGcmKeyFormatOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   XAesGcmParams getParams();

   XAesGcmParamsOrBuilder getParamsOrBuilder();
}
