package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface EcdsaParamsOrBuilder extends MessageOrBuilder {
   int getHashTypeValue();

   HashType getHashType();

   int getCurveValue();

   EllipticCurveType getCurve();

   int getEncodingValue();

   EcdsaSignatureEncoding getEncoding();
}
