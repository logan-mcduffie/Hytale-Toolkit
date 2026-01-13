package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPssParamsOrBuilder extends MessageOrBuilder {
   int getSigHashValue();

   HashType getSigHash();

   int getMgf1HashValue();

   HashType getMgf1Hash();

   int getSaltLength();
}
