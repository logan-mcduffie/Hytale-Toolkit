package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import java.util.List;

@Deprecated
public interface RegistryConfigOrBuilder extends MessageOrBuilder {
   String getConfigName();

   ByteString getConfigNameBytes();

   List<KeyTypeEntry> getEntryList();

   KeyTypeEntry getEntry(int index);

   int getEntryCount();

   List<? extends KeyTypeEntryOrBuilder> getEntryOrBuilderList();

   KeyTypeEntryOrBuilder getEntryOrBuilder(int index);
}
