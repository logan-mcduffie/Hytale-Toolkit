package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public class OptionSet {
   private final List<OptionSpec<?>> detectedSpecs = new ArrayList<>();
   private final Map<String, AbstractOptionSpec<?>> detectedOptions = new HashMap<>();
   private final Map<AbstractOptionSpec<?>, List<String>> optionsToArguments = new IdentityHashMap<>();
   private final Map<String, AbstractOptionSpec<?>> recognizedSpecs;
   private final Map<String, List<?>> defaultValues;

   OptionSet(Map<String, AbstractOptionSpec<?>> recognizedSpecs) {
      this.defaultValues = defaultValues(recognizedSpecs);
      this.recognizedSpecs = recognizedSpecs;
   }

   public boolean hasOptions() {
      return this.detectedOptions.size() != 1 || !this.detectedOptions.values().iterator().next().representsNonOptions();
   }

   public boolean has(String option) {
      return this.detectedOptions.containsKey(option);
   }

   public boolean has(OptionSpec<?> option) {
      return this.optionsToArguments.containsKey(option);
   }

   public boolean hasArgument(String option) {
      AbstractOptionSpec<?> spec = this.detectedOptions.get(option);
      return spec != null && this.hasArgument(spec);
   }

   public boolean hasArgument(OptionSpec<?> option) {
      Objects.requireNonNull(option);
      List<String> values = this.optionsToArguments.get(option);
      return values != null && !values.isEmpty();
   }

   public Object valueOf(String option) {
      Objects.requireNonNull(option);
      AbstractOptionSpec<?> spec = this.detectedOptions.get(option);
      if (spec == null) {
         List<?> defaults = this.defaultValuesFor(option);
         return defaults.isEmpty() ? null : defaults.get(0);
      } else {
         return this.valueOf(spec);
      }
   }

   public <V> V valueOf(OptionSpec<V> option) {
      Objects.requireNonNull(option);
      List<V> values = this.valuesOf(option);
      switch (values.size()) {
         case 0:
            return null;
         case 1:
            return values.get(0);
         default:
            throw new MultipleArgumentsForOptionException(option);
      }
   }

   public List<?> valuesOf(String option) {
      Objects.requireNonNull(option);
      AbstractOptionSpec<?> spec = this.detectedOptions.get(option);
      return spec == null ? this.defaultValuesFor(option) : this.valuesOf(spec);
   }

   public <V> List<V> valuesOf(OptionSpec<V> option) {
      Objects.requireNonNull(option);
      List<String> values = this.optionsToArguments.get(option);
      if (values != null && !values.isEmpty()) {
         AbstractOptionSpec<V> spec = (AbstractOptionSpec<V>)option;
         List<V> convertedValues = new ArrayList<>();

         for (String each : values) {
            convertedValues.add(spec.convert(each));
         }

         return Collections.unmodifiableList(convertedValues);
      } else {
         return this.defaultValueFor(option);
      }
   }

   public List<OptionSpec<?>> specs() {
      List<OptionSpec<?>> specs = this.detectedSpecs;
      specs.removeAll(Collections.singletonList(this.detectedOptions.get("[arguments]")));
      return Collections.unmodifiableList(specs);
   }

   public Map<OptionSpec<?>, List<?>> asMap() {
      Map<OptionSpec<?>, List<?>> map = new HashMap<>();

      for (AbstractOptionSpec<?> spec : this.recognizedSpecs.values()) {
         if (!spec.representsNonOptions()) {
            map.put(spec, this.valuesOf(spec));
         }
      }

      return Collections.unmodifiableMap(map);
   }

   public List<?> nonOptionArguments() {
      AbstractOptionSpec<?> spec = this.detectedOptions.get("[arguments]");
      return this.valuesOf(spec);
   }

   void add(AbstractOptionSpec<?> spec) {
      this.addWithArgument(spec, null);
   }

   void addWithArgument(AbstractOptionSpec<?> spec, String argument) {
      this.detectedSpecs.add(spec);

      for (String each : spec.options()) {
         this.detectedOptions.put(each, spec);
      }

      List<String> optionArguments = this.optionsToArguments.get(spec);
      if (optionArguments == null) {
         optionArguments = new ArrayList<>();
         this.optionsToArguments.put(spec, optionArguments);
      }

      if (argument != null) {
         optionArguments.add(argument);
      }
   }

   @Override
   public boolean equals(Object that) {
      if (this == that) {
         return true;
      } else if (that != null && this.getClass().equals(that.getClass())) {
         OptionSet other = (OptionSet)that;
         Map<AbstractOptionSpec<?>, List<String>> thisOptionsToArguments = new HashMap<>(this.optionsToArguments);
         Map<AbstractOptionSpec<?>, List<String>> otherOptionsToArguments = new HashMap<>(other.optionsToArguments);
         return this.detectedOptions.equals(other.detectedOptions) && thisOptionsToArguments.equals(otherOptionsToArguments);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      Map<AbstractOptionSpec<?>, List<String>> thisOptionsToArguments = new HashMap<>(this.optionsToArguments);
      return this.detectedOptions.hashCode() ^ thisOptionsToArguments.hashCode();
   }

   private <V> List<V> defaultValuesFor(String option) {
      return this.defaultValues.containsKey(option) ? Collections.unmodifiableList((List<? extends V>)this.defaultValues.get(option)) : Collections.emptyList();
   }

   private <V> List<V> defaultValueFor(OptionSpec<V> option) {
      return this.defaultValuesFor(option.options().iterator().next());
   }

   private static Map<String, List<?>> defaultValues(Map<String, AbstractOptionSpec<?>> recognizedSpecs) {
      Map<String, List<?>> defaults = new HashMap<>();

      for (Entry<String, AbstractOptionSpec<?>> each : recognizedSpecs.entrySet()) {
         defaults.put(each.getKey(), each.getValue().defaultValues());
      }

      return defaults;
   }
}
