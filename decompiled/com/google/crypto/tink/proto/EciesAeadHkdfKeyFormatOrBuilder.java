package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface EciesAeadHkdfKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   EciesAeadHkdfParams getParams();

   EciesAeadHkdfParamsOrBuilder getParamsOrBuilder();
}
