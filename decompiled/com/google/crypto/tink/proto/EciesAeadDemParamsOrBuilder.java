package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface EciesAeadDemParamsOrBuilder extends MessageOrBuilder {
   boolean hasAeadDem();

   KeyTemplate getAeadDem();

   KeyTemplateOrBuilder getAeadDemOrBuilder();
}
