package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPkcs1PublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   RsaSsaPkcs1Params getParams();

   RsaSsaPkcs1ParamsOrBuilder getParamsOrBuilder();

   ByteString getN();

   ByteString getE();
}
