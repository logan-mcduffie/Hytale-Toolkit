package io.netty.util;

@Deprecated
public interface ResourceLeak {
   void record();

   void record(Object var1);

   boolean close();
}
