package org.bson;

class NoOpFieldNameValidator implements FieldNameValidator {
   @Override
   public boolean validate(String fieldName) {
      return true;
   }

   @Override
   public FieldNameValidator getValidatorForField(String fieldName) {
      return this;
   }
}
