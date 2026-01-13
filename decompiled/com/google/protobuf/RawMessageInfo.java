package com.google.protobuf;

@CheckReturnValue
final class RawMessageInfo implements MessageInfo {
   private static final int IS_PROTO2_BIT = 1;
   private static final int IS_EDITION_BIT = 4;
   private final MessageLite defaultInstance;
   private final String info;
   private final Object[] objects;
   private final int flags;

   RawMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
      this.defaultInstance = defaultInstance;
      this.info = info;
      this.objects = objects;
      int position = 0;
      int value = info.charAt(position++);
      if (value < 55296) {
         this.flags = value;
      } else {
         int result = value & 8191;

         int shift;
         for (shift = 13; (var9 = info.charAt(position++)) >= '\ud800'; shift += 13) {
            result |= (var9 & 8191) << shift;
         }

         this.flags = result | var9 << shift;
      }
   }

   String getStringInfo() {
      return this.info;
   }

   Object[] getObjects() {
      return this.objects;
   }

   @Override
   public MessageLite getDefaultInstance() {
      return this.defaultInstance;
   }

   @Override
   public ProtoSyntax getSyntax() {
      if ((this.flags & 1) != 0) {
         return ProtoSyntax.PROTO2;
      } else {
         return (this.flags & 4) == 4 ? ProtoSyntax.EDITIONS : ProtoSyntax.PROTO3;
      }
   }

   @Override
   public boolean isMessageSetWireFormat() {
      return (this.flags & 2) == 2;
   }
}
