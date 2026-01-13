package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class ByteOutput {
   public abstract void write(byte value) throws IOException;

   public abstract void write(byte[] value, int offset, int length) throws IOException;

   public abstract void writeLazy(byte[] value, int offset, int length) throws IOException;

   public abstract void write(ByteBuffer value) throws IOException;

   public abstract void writeLazy(ByteBuffer value) throws IOException;
}
