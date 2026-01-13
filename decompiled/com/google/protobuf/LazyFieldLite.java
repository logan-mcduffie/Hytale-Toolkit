package com.google.protobuf;

import java.io.IOException;

public class LazyFieldLite {
   private ByteString delayedBytes;
   private ExtensionRegistryLite extensionRegistry;
   protected volatile MessageLite value;
   private volatile ByteString memoizedBytes;
   private volatile boolean corrupted;

   public LazyFieldLite(ExtensionRegistryLite extensionRegistry, ByteString bytes) {
      checkArguments(extensionRegistry, bytes);
      this.extensionRegistry = extensionRegistry;
      this.delayedBytes = bytes;
   }

   public LazyFieldLite() {
   }

   public static LazyFieldLite fromValue(MessageLite value) {
      LazyFieldLite lf = new LazyFieldLite();
      lf.setValue(value);
      return lf;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof LazyFieldLite)) {
         return false;
      } else {
         LazyFieldLite other = (LazyFieldLite)o;
         MessageLite value1 = this.value;
         MessageLite value2 = other.value;
         if (value1 == null && value2 == null) {
            return this.toByteString().equals(other.toByteString());
         } else if (value1 != null && value2 != null) {
            return value1.equals(value2);
         } else {
            return value1 != null
               ? value1.equals(other.getValue(value1.getDefaultInstanceForType()))
               : this.getValue(value2.getDefaultInstanceForType()).equals(value2);
         }
      }
   }

   @Override
   public int hashCode() {
      return 1;
   }

   public boolean containsDefaultInstance() {
      return this.memoizedBytes == ByteString.EMPTY || this.value == null && (this.delayedBytes == null || this.delayedBytes == ByteString.EMPTY);
   }

   public void clear() {
      this.delayedBytes = null;
      this.value = null;
      this.memoizedBytes = null;
   }

   public void set(LazyFieldLite other) {
      this.delayedBytes = other.delayedBytes;
      this.value = other.value;
      this.memoizedBytes = other.memoizedBytes;
      if (other.extensionRegistry != null) {
         this.extensionRegistry = other.extensionRegistry;
      }
   }

   public MessageLite getValue(MessageLite defaultInstance) {
      this.ensureInitialized(defaultInstance);
      return this.value;
   }

   public MessageLite setValue(MessageLite value) {
      MessageLite originalValue = this.value;
      this.delayedBytes = null;
      this.memoizedBytes = null;
      this.value = value;
      return originalValue;
   }

   public void merge(LazyFieldLite other) {
      if (!other.containsDefaultInstance()) {
         if (this.containsDefaultInstance()) {
            this.set(other);
         } else {
            if (this.extensionRegistry == null) {
               this.extensionRegistry = other.extensionRegistry;
            }

            if (this.delayedBytes != null && other.delayedBytes != null) {
               this.delayedBytes = this.delayedBytes.concat(other.delayedBytes);
            } else if (this.value == null && other.value != null) {
               this.setValue(mergeValueAndBytes(other.value, this.delayedBytes, this.extensionRegistry));
            } else if (this.value != null && other.value == null) {
               this.setValue(mergeValueAndBytes(this.value, other.delayedBytes, other.extensionRegistry));
            } else {
               this.setValue(this.value.toBuilder().mergeFrom(other.value).build());
            }
         }
      }
   }

   public void mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      if (this.containsDefaultInstance()) {
         this.setByteString(input.readBytes(), extensionRegistry);
      } else {
         if (this.extensionRegistry == null) {
            this.extensionRegistry = extensionRegistry;
         }

         if (this.delayedBytes != null) {
            this.setByteString(this.delayedBytes.concat(input.readBytes()), this.extensionRegistry);
         } else {
            try {
               this.setValue(this.value.toBuilder().mergeFrom(input, extensionRegistry).build());
            } catch (InvalidProtocolBufferException var4) {
            }
         }
      }
   }

   private static MessageLite mergeValueAndBytes(MessageLite value, ByteString otherBytes, ExtensionRegistryLite extensionRegistry) {
      try {
         return value.toBuilder().mergeFrom(otherBytes, extensionRegistry).build();
      } catch (InvalidProtocolBufferException var4) {
         return value;
      }
   }

   public void setByteString(ByteString bytes, ExtensionRegistryLite extensionRegistry) {
      checkArguments(extensionRegistry, bytes);
      this.delayedBytes = bytes;
      this.extensionRegistry = extensionRegistry;
      this.value = null;
      this.memoizedBytes = null;
   }

   public int getSerializedSize() {
      if (this.memoizedBytes != null) {
         return this.memoizedBytes.size();
      } else if (this.delayedBytes != null) {
         return this.delayedBytes.size();
      } else {
         return this.value != null ? this.value.getSerializedSize() : 0;
      }
   }

   public ByteString toByteString() {
      if (this.memoizedBytes != null) {
         return this.memoizedBytes;
      } else if (this.delayedBytes != null) {
         return this.delayedBytes;
      } else {
         synchronized (this) {
            if (this.memoizedBytes != null) {
               return this.memoizedBytes;
            } else {
               if (this.value == null) {
                  this.memoizedBytes = ByteString.EMPTY;
               } else {
                  this.memoizedBytes = this.value.toByteString();
               }

               return this.memoizedBytes;
            }
         }
      }
   }

   public int computeSizeNoTag() {
      return CodedOutputStream.computeLengthDelimitedFieldSize(this.getSerializedSize());
   }

   public int computeSize(final int fieldNumber) {
      return CodedOutputStream.computeTagSize(fieldNumber) + this.computeSizeNoTag();
   }

   public int computeMessageSetExtensionSize(final int fieldNumber) {
      return CodedOutputStream.computeTagSize(1) * 2 + CodedOutputStream.computeUInt32Size(2, fieldNumber) + this.computeSize(3);
   }

   void writeTo(Writer writer, int fieldNumber) throws IOException {
      if (this.memoizedBytes != null) {
         writer.writeBytes(fieldNumber, this.memoizedBytes);
      } else if (this.delayedBytes != null) {
         writer.writeBytes(fieldNumber, this.delayedBytes);
      } else if (this.value != null) {
         writer.writeMessage(fieldNumber, this.value);
      } else {
         writer.writeBytes(fieldNumber, ByteString.EMPTY);
      }
   }

   protected void ensureInitialized(MessageLite defaultInstance) {
      if (this.value == null) {
         synchronized (this) {
            if (this.value == null) {
               try {
                  if (this.delayedBytes != null) {
                     MessageLite parsedValue = defaultInstance.getParserForType().parseFrom(this.delayedBytes, this.extensionRegistry);
                     this.value = parsedValue;
                     this.memoizedBytes = this.delayedBytes;
                  } else {
                     this.value = defaultInstance;
                     this.memoizedBytes = ByteString.EMPTY;
                  }
               } catch (InvalidProtocolBufferException var5) {
                  this.corrupted = true;
                  this.value = defaultInstance;
                  this.memoizedBytes = ByteString.EMPTY;
               }
            }
         }
      }
   }

   private static void checkArguments(ExtensionRegistryLite extensionRegistry, ByteString bytes) {
      if (extensionRegistry == null) {
         throw new NullPointerException("found null ExtensionRegistry");
      } else if (bytes == null) {
         throw new NullPointerException("found null ByteString");
      }
   }

   boolean isCorrupted() {
      return this.corrupted;
   }
}
