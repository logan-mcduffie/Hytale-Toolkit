package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface EciesAeadHkdfParamsOrBuilder extends MessageOrBuilder {
   boolean hasKemParams();

   EciesHkdfKemParams getKemParams();

   EciesHkdfKemParamsOrBuilder getKemParamsOrBuilder();

   boolean hasDemParams();

   EciesAeadDemParams getDemParams();

   EciesAeadDemParamsOrBuilder getDemParamsOrBuilder();

   int getEcPointFormatValue();

   EcPointFormat getEcPointFormat();
}
