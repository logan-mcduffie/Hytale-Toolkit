package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPkcs1KeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   RsaSsaPkcs1Params getParams();

   RsaSsaPkcs1ParamsOrBuilder getParamsOrBuilder();

   int getModulusSizeInBits();

   ByteString getPublicExponent();
}
