package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPssKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   RsaSsaPssParams getParams();

   RsaSsaPssParamsOrBuilder getParamsOrBuilder();

   int getModulusSizeInBits();

   ByteString getPublicExponent();
}
