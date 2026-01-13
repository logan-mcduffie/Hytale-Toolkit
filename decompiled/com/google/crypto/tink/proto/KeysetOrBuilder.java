package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;
import java.util.List;

public interface KeysetOrBuilder extends MessageOrBuilder {
   int getPrimaryKeyId();

   List<Keyset.Key> getKeyList();

   Keyset.Key getKey(int index);

   int getKeyCount();

   List<? extends Keyset.KeyOrBuilder> getKeyOrBuilderList();

   Keyset.KeyOrBuilder getKeyOrBuilder(int index);
}
