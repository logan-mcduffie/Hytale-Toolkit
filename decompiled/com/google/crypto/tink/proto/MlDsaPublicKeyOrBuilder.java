package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface MlDsaPublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   ByteString getKeyValue();

   boolean hasParams();

   MlDsaParams getParams();

   MlDsaParamsOrBuilder getParamsOrBuilder();
}
