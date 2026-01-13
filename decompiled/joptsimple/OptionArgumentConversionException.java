package joptsimple;

import java.util.Collections;

class OptionArgumentConversionException extends OptionException {
   private static final long serialVersionUID = -1L;
   private final String argument;

   OptionArgumentConversionException(OptionSpec<?> options, String argument, Throwable cause) {
      super(Collections.singleton(options), cause);
      this.argument = argument;
   }

   @Override
   Object[] messageArguments() {
      return new Object[]{this.argument, this.singleOptionString()};
   }
}
