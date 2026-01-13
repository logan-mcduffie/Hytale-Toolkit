package joptsimple;

import java.util.Collections;

class MultipleArgumentsForOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   MultipleArgumentsForOptionException(OptionSpec<?> options) {
      super(Collections.singleton(options));
   }

   @Override
   Object[] messageArguments() {
      return new Object[]{this.singleOptionString()};
   }
}
