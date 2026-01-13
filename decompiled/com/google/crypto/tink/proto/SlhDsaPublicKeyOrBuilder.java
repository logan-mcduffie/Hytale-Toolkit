package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaPublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   ByteString getKeyValue();

   boolean hasParams();

   SlhDsaParams getParams();

   SlhDsaParamsOrBuilder getParamsOrBuilder();
}
