package com.google.protobuf;

import java.util.List;

public abstract class MapFieldReflectionAccessor {
   abstract List<Message> getList();

   abstract List<Message> getMutableList();

   abstract Message getMapEntryMessageDefaultInstance();
}
