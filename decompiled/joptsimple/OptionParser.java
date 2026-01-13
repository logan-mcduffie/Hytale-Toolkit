package joptsimple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import joptsimple.internal.AbbreviationMap;
import joptsimple.internal.OptionNameMap;
import joptsimple.internal.SimpleOptionNameMap;
import joptsimple.util.KeyValuePair;

public class OptionParser implements OptionDeclarer {
   private final OptionNameMap<AbstractOptionSpec<?>> recognizedOptions;
   private final ArrayList<AbstractOptionSpec<?>> trainingOrder;
   private final Map<List<String>, Set<OptionSpec<?>>> requiredIf;
   private final Map<List<String>, Set<OptionSpec<?>>> requiredUnless;
   private final Map<List<String>, Set<OptionSpec<?>>> availableIf;
   private final Map<List<String>, Set<OptionSpec<?>>> availableUnless;
   private OptionParserState state;
   private boolean posixlyCorrect;
   private boolean allowsUnrecognizedOptions;
   private HelpFormatter helpFormatter = new BuiltinHelpFormatter();

   public OptionParser() {
      this(true);
   }

   public OptionParser(boolean allowAbbreviations) {
      this.trainingOrder = new ArrayList<>();
      this.requiredIf = new HashMap<>();
      this.requiredUnless = new HashMap<>();
      this.availableIf = new HashMap<>();
      this.availableUnless = new HashMap<>();
      this.state = OptionParserState.moreOptions(false);
      this.recognizedOptions = (OptionNameMap<AbstractOptionSpec<?>>)(allowAbbreviations ? new AbbreviationMap<>() : new SimpleOptionNameMap<>());
      this.recognize(new NonOptionArgumentSpec());
   }

   public OptionParser(String optionSpecification) {
      this();
      new OptionSpecTokenizer(optionSpecification).configure(this);
   }

   @Override
   public OptionSpecBuilder accepts(String option) {
      return this.acceptsAll(Collections.singletonList(option));
   }

   @Override
   public OptionSpecBuilder accepts(String option, String description) {
      return this.acceptsAll(Collections.singletonList(option), description);
   }

   @Override
   public OptionSpecBuilder acceptsAll(List<String> options) {
      return this.acceptsAll(options, "");
   }

   @Override
   public OptionSpecBuilder acceptsAll(List<String> options, String description) {
      if (options.isEmpty()) {
         throw new IllegalArgumentException("need at least one option");
      } else {
         ParserRules.ensureLegalOptions(options);
         return new OptionSpecBuilder(this, options, description);
      }
   }

   @Override
   public NonOptionArgumentSpec<String> nonOptions() {
      NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<>();
      this.recognize(spec);
      return spec;
   }

   @Override
   public NonOptionArgumentSpec<String> nonOptions(String description) {
      NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<>(description);
      this.recognize(spec);
      return spec;
   }

   @Override
   public void posixlyCorrect(boolean setting) {
      this.posixlyCorrect = setting;
      this.state = OptionParserState.moreOptions(setting);
   }

   boolean posixlyCorrect() {
      return this.posixlyCorrect;
   }

   @Override
   public void allowsUnrecognizedOptions() {
      this.allowsUnrecognizedOptions = true;
   }

   boolean doesAllowsUnrecognizedOptions() {
      return this.allowsUnrecognizedOptions;
   }

   @Override
   public void recognizeAlternativeLongOptions(boolean recognize) {
      if (recognize) {
         this.recognize(new AlternativeLongOptionSpec());
      } else {
         this.recognizedOptions.remove(String.valueOf("W"));
      }
   }

   void recognize(AbstractOptionSpec<?> spec) {
      this.recognizedOptions.putAll(spec.options(), spec);
      this.trainingOrder.add(spec);
   }

   public void printHelpOn(OutputStream sink) throws IOException {
      this.printHelpOn(new OutputStreamWriter(sink));
   }

   public void printHelpOn(Writer sink) throws IOException {
      sink.write(this.helpFormatter.format(this._recognizedOptions()));
      sink.flush();
   }

   public void formatHelpWith(HelpFormatter formatter) {
      if (formatter == null) {
         throw new NullPointerException();
      } else {
         this.helpFormatter = formatter;
      }
   }

   public Map<String, OptionSpec<?>> recognizedOptions() {
      return new LinkedHashMap<>(this._recognizedOptions());
   }

   private Map<String, AbstractOptionSpec<?>> _recognizedOptions() {
      Map<String, AbstractOptionSpec<?>> options = new LinkedHashMap<>();

      for (AbstractOptionSpec<?> spec : this.trainingOrder) {
         for (String option : spec.options()) {
            options.put(option, spec);
         }
      }

      return options;
   }

   public OptionSet parse(String... arguments) {
      ArgumentList argumentList = new ArgumentList(arguments);
      OptionSet detected = new OptionSet(this.recognizedOptions.toJavaUtilMap());
      detected.add(this.recognizedOptions.get("[arguments]"));

      while (argumentList.hasMore()) {
         this.state.handleArgument(this, argumentList, detected);
      }

      this.reset();
      this.ensureRequiredOptions(detected);
      this.ensureAllowedOptions(detected);
      return detected;
   }

   public void mutuallyExclusive(OptionSpecBuilder... specs) {
      for (int i = 0; i < specs.length; i++) {
         for (int j = 0; j < specs.length; j++) {
            if (i != j) {
               specs[i].availableUnless(specs[j]);
            }
         }
      }
   }

   private void ensureRequiredOptions(OptionSet options) {
      List<AbstractOptionSpec<?>> missingRequiredOptions = this.missingRequiredOptions(options);
      boolean helpOptionPresent = this.isHelpOptionPresent(options);
      if (!missingRequiredOptions.isEmpty() && !helpOptionPresent) {
         throw new MissingRequiredOptionsException(missingRequiredOptions);
      }
   }

   private void ensureAllowedOptions(OptionSet options) {
      List<AbstractOptionSpec<?>> forbiddenOptions = this.unavailableOptions(options);
      boolean helpOptionPresent = this.isHelpOptionPresent(options);
      if (!forbiddenOptions.isEmpty() && !helpOptionPresent) {
         throw new UnavailableOptionException(forbiddenOptions);
      }
   }

   private List<AbstractOptionSpec<?>> missingRequiredOptions(OptionSet options) {
      List<AbstractOptionSpec<?>> missingRequiredOptions = new ArrayList<>();

      for (AbstractOptionSpec<?> each : this.recognizedOptions.toJavaUtilMap().values()) {
         if (each.isRequired() && !options.has(each)) {
            missingRequiredOptions.add(each);
         }
      }

      for (Entry<List<String>, Set<OptionSpec<?>>> eachx : this.requiredIf.entrySet()) {
         AbstractOptionSpec<?> required = this.specFor(eachx.getKey().iterator().next());
         if (this.optionsHasAnyOf(options, eachx.getValue()) && !options.has(required)) {
            missingRequiredOptions.add(required);
         }
      }

      for (Entry<List<String>, Set<OptionSpec<?>>> eachxx : this.requiredUnless.entrySet()) {
         AbstractOptionSpec<?> required = this.specFor(eachxx.getKey().iterator().next());
         if (!this.optionsHasAnyOf(options, eachxx.getValue()) && !options.has(required)) {
            missingRequiredOptions.add(required);
         }
      }

      return missingRequiredOptions;
   }

   private List<AbstractOptionSpec<?>> unavailableOptions(OptionSet options) {
      List<AbstractOptionSpec<?>> unavailableOptions = new ArrayList<>();

      for (Entry<List<String>, Set<OptionSpec<?>>> eachEntry : this.availableIf.entrySet()) {
         AbstractOptionSpec<?> forbidden = this.specFor(eachEntry.getKey().iterator().next());
         if (!this.optionsHasAnyOf(options, eachEntry.getValue()) && options.has(forbidden)) {
            unavailableOptions.add(forbidden);
         }
      }

      for (Entry<List<String>, Set<OptionSpec<?>>> eachEntryx : this.availableUnless.entrySet()) {
         AbstractOptionSpec<?> forbidden = this.specFor(eachEntryx.getKey().iterator().next());
         if (this.optionsHasAnyOf(options, eachEntryx.getValue()) && options.has(forbidden)) {
            unavailableOptions.add(forbidden);
         }
      }

      return unavailableOptions;
   }

   private boolean optionsHasAnyOf(OptionSet options, Collection<OptionSpec<?>> specs) {
      for (OptionSpec<?> each : specs) {
         if (options.has(each)) {
            return true;
         }
      }

      return false;
   }

   private boolean isHelpOptionPresent(OptionSet options) {
      boolean helpOptionPresent = false;

      for (AbstractOptionSpec<?> each : this.recognizedOptions.toJavaUtilMap().values()) {
         if (each.isForHelp() && options.has(each)) {
            helpOptionPresent = true;
            break;
         }
      }

      return helpOptionPresent;
   }

   void handleLongOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
      KeyValuePair optionAndArgument = parseLongOptionWithArgument(candidate);
      if (!this.isRecognized(optionAndArgument.key)) {
         throw OptionException.unrecognizedOption(optionAndArgument.key);
      } else {
         AbstractOptionSpec<?> optionSpec = this.specFor(optionAndArgument.key);
         optionSpec.handleOption(this, arguments, detected, optionAndArgument.value);
      }
   }

   void handleShortOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
      KeyValuePair optionAndArgument = parseShortOptionWithArgument(candidate);
      if (this.isRecognized(optionAndArgument.key)) {
         this.specFor(optionAndArgument.key).handleOption(this, arguments, detected, optionAndArgument.value);
      } else {
         this.handleShortOptionCluster(candidate, arguments, detected);
      }
   }

   private void handleShortOptionCluster(String candidate, ArgumentList arguments, OptionSet detected) {
      char[] options = extractShortOptionsFrom(candidate);
      this.validateOptionCharacters(options);

      for (int i = 0; i < options.length; i++) {
         AbstractOptionSpec<?> optionSpec = this.specFor(options[i]);
         if (optionSpec.acceptsArguments() && options.length > i + 1) {
            String detectedArgument = String.valueOf(options, i + 1, options.length - 1 - i);
            optionSpec.handleOption(this, arguments, detected, detectedArgument);
            break;
         }

         optionSpec.handleOption(this, arguments, detected, null);
      }
   }

   void handleNonOptionArgument(String candidate, ArgumentList arguments, OptionSet detectedOptions) {
      this.specFor("[arguments]").handleOption(this, arguments, detectedOptions, candidate);
   }

   void noMoreOptions() {
      this.state = OptionParserState.noMoreOptions();
   }

   boolean looksLikeAnOption(String argument) {
      return ParserRules.isShortOptionToken(argument) || ParserRules.isLongOptionToken(argument);
   }

   boolean isRecognized(String option) {
      return this.recognizedOptions.contains(option);
   }

   void requiredIf(List<String> precedentSynonyms, String required) {
      this.requiredIf(precedentSynonyms, this.specFor(required));
   }

   void requiredIf(List<String> precedentSynonyms, OptionSpec<?> required) {
      this.putDependentOption(precedentSynonyms, required, this.requiredIf);
   }

   void requiredUnless(List<String> precedentSynonyms, String required) {
      this.requiredUnless(precedentSynonyms, this.specFor(required));
   }

   void requiredUnless(List<String> precedentSynonyms, OptionSpec<?> required) {
      this.putDependentOption(precedentSynonyms, required, this.requiredUnless);
   }

   void availableIf(List<String> precedentSynonyms, String available) {
      this.availableIf(precedentSynonyms, this.specFor(available));
   }

   void availableIf(List<String> precedentSynonyms, OptionSpec<?> available) {
      this.putDependentOption(precedentSynonyms, available, this.availableIf);
   }

   void availableUnless(List<String> precedentSynonyms, String available) {
      this.availableUnless(precedentSynonyms, this.specFor(available));
   }

   void availableUnless(List<String> precedentSynonyms, OptionSpec<?> available) {
      this.putDependentOption(precedentSynonyms, available, this.availableUnless);
   }

   private void putDependentOption(List<String> precedentSynonyms, OptionSpec<?> required, Map<List<String>, Set<OptionSpec<?>>> target) {
      for (String each : precedentSynonyms) {
         AbstractOptionSpec<?> spec = this.specFor(each);
         if (spec == null) {
            throw new UnconfiguredOptionException(precedentSynonyms);
         }
      }

      Set<OptionSpec<?>> associated = target.get(precedentSynonyms);
      if (associated == null) {
         associated = new HashSet<>();
         target.put(precedentSynonyms, associated);
      }

      associated.add(required);
   }

   private AbstractOptionSpec<?> specFor(char option) {
      return this.specFor(String.valueOf(option));
   }

   private AbstractOptionSpec<?> specFor(String option) {
      return this.recognizedOptions.get(option);
   }

   private void reset() {
      this.state = OptionParserState.moreOptions(this.posixlyCorrect);
   }

   private static char[] extractShortOptionsFrom(String argument) {
      char[] options = new char[argument.length() - 1];
      argument.getChars(1, argument.length(), options, 0);
      return options;
   }

   private void validateOptionCharacters(char[] options) {
      for (char each : options) {
         String option = String.valueOf(each);
         if (!this.isRecognized(option)) {
            throw OptionException.unrecognizedOption(option);
         }

         if (this.specFor(option).acceptsArguments()) {
            return;
         }
      }
   }

   private static KeyValuePair parseLongOptionWithArgument(String argument) {
      return KeyValuePair.valueOf(argument.substring(2));
   }

   private static KeyValuePair parseShortOptionWithArgument(String argument) {
      return KeyValuePair.valueOf(argument.substring(1));
   }
}
