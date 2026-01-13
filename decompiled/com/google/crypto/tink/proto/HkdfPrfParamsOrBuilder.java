package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HkdfPrfParamsOrBuilder extends MessageOrBuilder {
   int getHashValue();

   HashType getHash();

   ByteString getSalt();
}
