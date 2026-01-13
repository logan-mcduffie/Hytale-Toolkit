package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPssPublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   RsaSsaPssParams getParams();

   RsaSsaPssParamsOrBuilder getParamsOrBuilder();

   ByteString getN();

   ByteString getE();
}
