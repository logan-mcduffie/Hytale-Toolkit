package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface HmacPrfParamsOrBuilder extends MessageOrBuilder {
   int getHashValue();

   HashType getHash();
}
