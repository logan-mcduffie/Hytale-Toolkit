package com.google.protobuf;

public final class DiscardUnknownFieldsParser {
   public static final <T extends Message> Parser<T> wrap(final Parser<T> parser) {
      return new AbstractParser<T>() {
         public T parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            Message var3;
            try {
               input.discardUnknownFields();
               var3 = parser.parsePartialFrom(input, extensionRegistry);
            } finally {
               input.unsetDiscardUnknownFields();
            }

            return (T)var3;
         }
      };
   }

   private DiscardUnknownFieldsParser() {
   }
}
