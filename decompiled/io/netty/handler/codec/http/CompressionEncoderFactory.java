package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

interface CompressionEncoderFactory {
   MessageToByteEncoder<ByteBuf> createEncoder();
}
