package io.netty.handler.codec.http3;

@FunctionalInterface
interface Http3FrameTypeValidator {
   Http3FrameTypeValidator NO_VALIDATION = (type, first) -> {};

   void validate(long var1, boolean var3) throws Http3Exception;
}
