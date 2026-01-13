package io.netty.channel;

public interface IoRegistration {
   <T> T attachment();

   long submit(IoOps var1);

   boolean isValid();

   boolean cancel();
}
