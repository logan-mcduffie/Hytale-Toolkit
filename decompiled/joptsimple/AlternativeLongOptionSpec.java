package joptsimple;

import java.util.Collections;
import java.util.Locale;
import joptsimple.internal.Messages;

class AlternativeLongOptionSpec extends ArgumentAcceptingOptionSpec<String> {
   AlternativeLongOptionSpec() {
      super(
         Collections.singletonList("W"),
         true,
         Messages.message(Locale.getDefault(), "joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, "description")
      );
      this.describedAs(Messages.message(Locale.getDefault(), "joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, "arg.description"));
   }

   @Override
   protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
      if (!arguments.hasMore()) {
         throw new OptionMissingRequiredArgumentException(this);
      } else {
         arguments.treatNextAsLongOption();
      }
   }
}
