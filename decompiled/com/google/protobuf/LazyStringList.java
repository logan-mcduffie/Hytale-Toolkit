package com.google.protobuf;

import java.util.Collection;
import java.util.List;

public interface LazyStringList extends ProtocolStringList {
   ByteString getByteString(int index);

   Object getRaw(int index);

   byte[] getByteArray(int index);

   void add(ByteString element);

   void add(byte[] element);

   void set(int index, ByteString element);

   void set(int index, byte[] element);

   boolean addAllByteString(Collection<? extends ByteString> c);

   boolean addAllByteArray(Collection<byte[]> c);

   List<?> getUnderlyingElements();

   void mergeFrom(LazyStringList other);

   List<byte[]> asByteArrayList();

   LazyStringList getUnmodifiableView();
}
