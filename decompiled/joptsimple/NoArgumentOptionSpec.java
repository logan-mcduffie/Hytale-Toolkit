package joptsimple;

import java.util.Collections;
import java.util.List;

class NoArgumentOptionSpec extends AbstractOptionSpec<Void> {
   NoArgumentOptionSpec(String option) {
      this(Collections.singletonList(option), "");
   }

   NoArgumentOptionSpec(List<String> options, String description) {
      super(options, description);
   }

   @Override
   void handleOption(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions, String detectedArgument) {
      detectedOptions.add(this);
   }

   @Override
   public boolean acceptsArguments() {
      return false;
   }

   @Override
   public boolean requiresArgument() {
      return false;
   }

   @Override
   public boolean isRequired() {
      return false;
   }

   @Override
   public String argumentDescription() {
      return "";
   }

   @Override
   public String argumentTypeIndicator() {
      return "";
   }

   protected Void convert(String argument) {
      return null;
   }

   @Override
   public List<Void> defaultValues() {
      return Collections.emptyList();
   }
}
