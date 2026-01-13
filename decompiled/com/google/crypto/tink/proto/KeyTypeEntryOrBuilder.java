package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

@Deprecated
public interface KeyTypeEntryOrBuilder extends MessageOrBuilder {
   String getPrimitiveName();

   ByteString getPrimitiveNameBytes();

   String getTypeUrl();

   ByteString getTypeUrlBytes();

   int getKeyManagerVersion();

   boolean getNewKeyAllowed();

   String getCatalogueName();

   ByteString getCatalogueNameBytes();
}
