package com.google.protobuf;

import java.util.List;

public interface FieldMaskOrBuilder extends MessageOrBuilder {
   List<String> getPathsList();

   int getPathsCount();

   String getPaths(int index);

   ByteString getPathsBytes(int index);
}
