package io.netty.handler.codec.http.multipart;

import io.netty.util.ReferenceCounted;

public interface InterfaceHttpData extends Comparable<InterfaceHttpData>, ReferenceCounted {
   String getName();

   InterfaceHttpData.HttpDataType getHttpDataType();

   InterfaceHttpData retain();

   InterfaceHttpData retain(int var1);

   InterfaceHttpData touch();

   InterfaceHttpData touch(Object var1);

   public static enum HttpDataType {
      Attribute,
      FileUpload,
      InternalAttribute;
   }
}
