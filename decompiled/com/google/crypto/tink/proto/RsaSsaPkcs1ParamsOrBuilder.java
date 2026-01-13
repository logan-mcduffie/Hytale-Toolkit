package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPkcs1ParamsOrBuilder extends MessageOrBuilder {
   int getHashTypeValue();

   HashType getHashType();
}
