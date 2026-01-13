package com.nimbusds.jose;

class HeaderValidation {
   static void ensureDisjoint(Header header, UnprotectedHeader unprotectedHeader) throws IllegalHeaderException {
      if (header != null && unprotectedHeader != null) {
         for (String unprotectedParamName : unprotectedHeader.getIncludedParams()) {
            if (header.getIncludedParams().contains(unprotectedParamName)) {
               throw new IllegalHeaderException("The parameters in the protected header and the unprotected header must be disjoint");
            }
         }
      }
   }

   private HeaderValidation() {
   }
}
