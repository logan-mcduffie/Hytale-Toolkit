package com.google.protobuf;

@CheckReturnValue
final class OneofInfo {
   private final int id;
   private final java.lang.reflect.Field caseField;
   private final java.lang.reflect.Field valueField;

   public OneofInfo(int id, java.lang.reflect.Field caseField, java.lang.reflect.Field valueField) {
      this.id = id;
      this.caseField = caseField;
      this.valueField = valueField;
   }

   public int getId() {
      return this.id;
   }

   public java.lang.reflect.Field getCaseField() {
      return this.caseField;
   }

   public java.lang.reflect.Field getValueField() {
      return this.valueField;
   }
}
