package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface HpkeParamsOrBuilder extends MessageOrBuilder {
   int getKemValue();

   HpkeKem getKem();

   int getKdfValue();

   HpkeKdf getKdf();

   int getAeadValue();

   HpkeAead getAead();
}
