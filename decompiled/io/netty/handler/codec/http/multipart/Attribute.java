package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

public interface Attribute extends HttpData {
   String getValue() throws IOException;

   void setValue(String var1) throws IOException;

   Attribute copy();

   Attribute duplicate();

   Attribute retainedDuplicate();

   Attribute replace(ByteBuf var1);

   Attribute retain();

   Attribute retain(int var1);

   Attribute touch();

   Attribute touch(Object var1);
}
