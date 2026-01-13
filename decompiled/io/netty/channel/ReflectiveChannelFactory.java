package io.netty.channel;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.lang.reflect.Constructor;

public class ReflectiveChannelFactory<T extends Channel> implements ChannelFactory<T> {
   private final Constructor<? extends T> constructor;

   public ReflectiveChannelFactory(Class<? extends T> clazz) {
      ObjectUtil.checkNotNull(clazz, "clazz");

      try {
         this.constructor = clazz.getConstructor();
      } catch (NoSuchMethodException var3) {
         throw new IllegalArgumentException("Class " + StringUtil.simpleClassName(clazz) + " does not have a public non-arg constructor", var3);
      }
   }

   @Override
   public T newChannel() {
      try {
         return (T)this.constructor.newInstance();
      } catch (Throwable var2) {
         throw new ChannelException("Unable to create Channel from class " + this.constructor.getDeclaringClass(), var2);
      }
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(ReflectiveChannelFactory.class) + '(' + StringUtil.simpleClassName(this.constructor.getDeclaringClass()) + ".class)";
   }
}
