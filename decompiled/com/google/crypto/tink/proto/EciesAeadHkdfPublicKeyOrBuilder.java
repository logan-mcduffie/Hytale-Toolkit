package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EciesAeadHkdfPublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   EciesAeadHkdfParams getParams();

   EciesAeadHkdfParamsOrBuilder getParamsOrBuilder();

   ByteString getX();

   ByteString getY();
}
