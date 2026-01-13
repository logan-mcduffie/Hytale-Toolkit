package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HpkePublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   HpkeParams getParams();

   HpkeParamsOrBuilder getParamsOrBuilder();

   ByteString getPublicKey();
}
