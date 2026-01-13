package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import joptsimple.internal.Reflection;
import joptsimple.internal.ReflectionException;

public abstract class AbstractOptionSpec<V> implements OptionSpec<V>, OptionDescriptor {
   private final List<String> options = new ArrayList<>();
   private final String description;
   private boolean forHelp;

   AbstractOptionSpec(String option) {
      this(Collections.singletonList(option), "");
   }

   AbstractOptionSpec(List<String> options, String description) {
      this.arrangeOptions(options);
      this.description = description;
   }

   @Override
   public final List<String> options() {
      return Collections.unmodifiableList(this.options);
   }

   @Override
   public final List<V> values(OptionSet detectedOptions) {
      return detectedOptions.valuesOf(this);
   }

   @Override
   public final V value(OptionSet detectedOptions) {
      return detectedOptions.valueOf(this);
   }

   @Override
   public String description() {
      return this.description;
   }

   public final AbstractOptionSpec<V> forHelp() {
      this.forHelp = true;
      return this;
   }

   @Override
   public final boolean isForHelp() {
      return this.forHelp;
   }

   @Override
   public boolean representsNonOptions() {
      return false;
   }

   protected abstract V convert(String var1);

   protected V convertWith(ValueConverter<V> converter, String argument) {
      try {
         return Reflection.convertWith(converter, argument);
      } catch (ValueConversionException | ReflectionException var4) {
         throw new OptionArgumentConversionException(this, argument, var4);
      }
   }

   protected String argumentTypeIndicatorFrom(ValueConverter<V> converter) {
      if (converter == null) {
         return null;
      } else {
         String pattern = converter.valuePattern();
         return pattern == null ? converter.valueType().getName() : pattern;
      }
   }

   abstract void handleOption(OptionParser var1, ArgumentList var2, OptionSet var3, String var4);

   private void arrangeOptions(List<String> unarranged) {
      if (unarranged.size() == 1) {
         this.options.addAll(unarranged);
      } else {
         List<String> shortOptions = new ArrayList<>();
         List<String> longOptions = new ArrayList<>();

         for (String each : unarranged) {
            if (each.length() == 1) {
               shortOptions.add(each);
            } else {
               longOptions.add(each);
            }
         }

         Collections.sort(shortOptions);
         Collections.sort(longOptions);
         this.options.addAll(shortOptions);
         this.options.addAll(longOptions);
      }
   }

   @Override
   public boolean equals(Object that) {
      if (!(that instanceof AbstractOptionSpec)) {
         return false;
      } else {
         AbstractOptionSpec<?> other = (AbstractOptionSpec<?>)that;
         return this.options.equals(other.options);
      }
   }

   @Override
   public int hashCode() {
      return this.options.hashCode();
   }

   @Override
   public String toString() {
      return this.options.toString();
   }
}
