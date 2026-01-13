package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;
import java.util.List;

public interface KeysetInfoOrBuilder extends MessageOrBuilder {
   int getPrimaryKeyId();

   List<KeysetInfo.KeyInfo> getKeyInfoList();

   KeysetInfo.KeyInfo getKeyInfo(int index);

   int getKeyInfoCount();

   List<? extends KeysetInfo.KeyInfoOrBuilder> getKeyInfoOrBuilderList();

   KeysetInfo.KeyInfoOrBuilder getKeyInfoOrBuilder(int index);
}
