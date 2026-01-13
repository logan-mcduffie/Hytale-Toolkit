package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface HmacParamsOrBuilder extends MessageOrBuilder {
   int getHashValue();

   HashType getHash();

   int getTagSize();
}
