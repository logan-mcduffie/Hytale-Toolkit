package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface KmsEnvelopeAeadKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   KmsEnvelopeAeadKeyFormat getParams();

   KmsEnvelopeAeadKeyFormatOrBuilder getParamsOrBuilder();
}
