package com.nimbusds.jose.util;

import com.nimbusds.jose.JOSEException;

public class IntegerOverflowException extends JOSEException {
   public IntegerOverflowException() {
      super("Integer overflow");
   }
}
