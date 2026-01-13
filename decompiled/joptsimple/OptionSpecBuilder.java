package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionSpecBuilder extends NoArgumentOptionSpec {
   private final OptionParser parser;

   OptionSpecBuilder(OptionParser parser, List<String> options, String description) {
      super(options, description);
      this.parser = parser;
      this.attachToParser();
   }

   private void attachToParser() {
      this.parser.recognize(this);
   }

   public ArgumentAcceptingOptionSpec<String> withRequiredArg() {
      ArgumentAcceptingOptionSpec<String> newSpec = new RequiredArgumentOptionSpec<>(this.options(), this.description());
      this.parser.recognize(newSpec);
      return newSpec;
   }

   public ArgumentAcceptingOptionSpec<String> withOptionalArg() {
      ArgumentAcceptingOptionSpec<String> newSpec = new OptionalArgumentOptionSpec<>(this.options(), this.description());
      this.parser.recognize(newSpec);
      return newSpec;
   }

   public OptionSpecBuilder requiredIf(String dependent, String... otherDependents) {
      for (String each : this.validatedDependents(dependent, otherDependents)) {
         this.parser.requiredIf(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder requiredIf(OptionSpec<?> dependent, OptionSpec<?>... otherDependents) {
      this.parser.requiredIf(this.options(), dependent);

      for (OptionSpec<?> each : otherDependents) {
         this.parser.requiredIf(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder requiredUnless(String dependent, String... otherDependents) {
      for (String each : this.validatedDependents(dependent, otherDependents)) {
         this.parser.requiredUnless(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder requiredUnless(OptionSpec<?> dependent, OptionSpec<?>... otherDependents) {
      this.parser.requiredUnless(this.options(), dependent);

      for (OptionSpec<?> each : otherDependents) {
         this.parser.requiredUnless(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder availableIf(String dependent, String... otherDependents) {
      for (String each : this.validatedDependents(dependent, otherDependents)) {
         this.parser.availableIf(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder availableIf(OptionSpec<?> dependent, OptionSpec<?>... otherDependents) {
      this.parser.availableIf(this.options(), dependent);

      for (OptionSpec<?> each : otherDependents) {
         this.parser.availableIf(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder availableUnless(String dependent, String... otherDependents) {
      for (String each : this.validatedDependents(dependent, otherDependents)) {
         this.parser.availableUnless(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder availableUnless(OptionSpec<?> dependent, OptionSpec<?>... otherDependents) {
      this.parser.availableUnless(this.options(), dependent);

      for (OptionSpec<?> each : otherDependents) {
         this.parser.availableUnless(this.options(), each);
      }

      return this;
   }

   private List<String> validatedDependents(String dependent, String... otherDependents) {
      List<String> dependents = new ArrayList<>();
      dependents.add(dependent);
      Collections.addAll(dependents, otherDependents);

      for (String each : dependents) {
         if (!this.parser.isRecognized(each)) {
            throw new UnconfiguredOptionException(each);
         }
      }

      return dependents;
   }
}
