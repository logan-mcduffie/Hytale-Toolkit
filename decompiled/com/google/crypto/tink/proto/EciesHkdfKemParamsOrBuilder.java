package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EciesHkdfKemParamsOrBuilder extends MessageOrBuilder {
   int getCurveTypeValue();

   EllipticCurveType getCurveType();

   int getHkdfHashTypeValue();

   HashType getHkdfHashType();

   ByteString getHkdfSalt();
}
