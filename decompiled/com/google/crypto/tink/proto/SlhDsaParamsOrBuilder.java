package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaParamsOrBuilder extends MessageOrBuilder {
   int getKeySize();

   int getHashTypeValue();

   SlhDsaHashType getHashType();

   int getSigTypeValue();

   SlhDsaSignatureType getSigType();
}
