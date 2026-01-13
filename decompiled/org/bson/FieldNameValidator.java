package org.bson;

public interface FieldNameValidator {
   boolean validate(String var1);

   FieldNameValidator getValidatorForField(String var1);

   default void start() {
   }

   default void end() {
   }
}
