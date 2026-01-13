package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface KmsAeadKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   KmsAeadKeyFormat getParams();

   KmsAeadKeyFormatOrBuilder getParamsOrBuilder();
}
