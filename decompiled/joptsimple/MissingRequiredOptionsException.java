package joptsimple;

import java.util.List;

class MissingRequiredOptionsException extends OptionException {
   private static final long serialVersionUID = -1L;

   protected MissingRequiredOptionsException(List<? extends OptionSpec<?>> missingRequiredOptions) {
      super(missingRequiredOptions);
   }

   @Override
   Object[] messageArguments() {
      return new Object[]{this.multipleOptionString()};
   }
}
