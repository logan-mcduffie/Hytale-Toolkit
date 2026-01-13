package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpConstants;
import java.io.IOException;
import java.nio.charset.Charset;

public class MixedAttribute extends AbstractMixedHttpData<Attribute> implements Attribute {
   public MixedAttribute(String name, long limitSize) {
      this(name, limitSize, HttpConstants.DEFAULT_CHARSET);
   }

   public MixedAttribute(String name, long definedSize, long limitSize) {
      this(name, definedSize, limitSize, HttpConstants.DEFAULT_CHARSET);
   }

   public MixedAttribute(String name, long limitSize, Charset charset) {
      this(name, limitSize, charset, DiskAttribute.baseDirectory, DiskAttribute.deleteOnExitTemporaryFile);
   }

   public MixedAttribute(String name, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
      this(name, 0L, limitSize, charset, baseDir, deleteOnExit);
   }

   public MixedAttribute(String name, long definedSize, long limitSize, Charset charset) {
      this(name, definedSize, limitSize, charset, DiskAttribute.baseDirectory, DiskAttribute.deleteOnExitTemporaryFile);
   }

   public MixedAttribute(String name, long definedSize, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
      super(limitSize, baseDir, deleteOnExit, new MemoryAttribute(name, definedSize, charset));
   }

   public MixedAttribute(String name, String value, long limitSize) {
      this(name, value, limitSize, HttpConstants.DEFAULT_CHARSET, DiskAttribute.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
   }

   public MixedAttribute(String name, String value, long limitSize, Charset charset) {
      this(name, value, limitSize, charset, DiskAttribute.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
   }

   private static Attribute makeInitialAttributeFromValue(String name, String value, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
      if (value.length() > limitSize) {
         try {
            return new DiskAttribute(name, value, charset, baseDir, deleteOnExit);
         } catch (IOException var10) {
            try {
               return new MemoryAttribute(name, value, charset);
            } catch (IOException var9) {
               throw new IllegalArgumentException(var10);
            }
         }
      } else {
         try {
            return new MemoryAttribute(name, value, charset);
         } catch (IOException var11) {
            throw new IllegalArgumentException(var11);
         }
      }
   }

   public MixedAttribute(String name, String value, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
      super(limitSize, baseDir, deleteOnExit, makeInitialAttributeFromValue(name, value, limitSize, charset, baseDir, deleteOnExit));
   }

   @Override
   public String getValue() throws IOException {
      return this.wrapped.getValue();
   }

   @Override
   public void setValue(String value) throws IOException {
      this.wrapped.setValue(value);
   }

   Attribute makeDiskData() {
      DiskAttribute diskAttribute = new DiskAttribute(this.getName(), this.definedLength(), this.baseDir, this.deleteOnExit);
      diskAttribute.setMaxSize(this.getMaxSize());
      return diskAttribute;
   }

   @Override
   public Attribute copy() {
      return (Attribute)super.copy();
   }

   @Override
   public Attribute duplicate() {
      return (Attribute)super.duplicate();
   }

   @Override
   public Attribute replace(ByteBuf content) {
      return (Attribute)super.replace(content);
   }

   @Override
   public Attribute retain() {
      return (Attribute)super.retain();
   }

   @Override
   public Attribute retain(int increment) {
      return (Attribute)super.retain(increment);
   }

   @Override
   public Attribute retainedDuplicate() {
      return (Attribute)super.retainedDuplicate();
   }

   @Override
   public Attribute touch() {
      return (Attribute)super.touch();
   }

   @Override
   public Attribute touch(Object hint) {
      return (Attribute)super.touch(hint);
   }
}
