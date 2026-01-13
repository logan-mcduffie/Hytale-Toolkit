package joptsimple;

import java.util.List;

public interface OptionDeclarer {
   OptionSpecBuilder accepts(String var1);

   OptionSpecBuilder accepts(String var1, String var2);

   OptionSpecBuilder acceptsAll(List<String> var1);

   OptionSpecBuilder acceptsAll(List<String> var1, String var2);

   NonOptionArgumentSpec<String> nonOptions();

   NonOptionArgumentSpec<String> nonOptions(String var1);

   void posixlyCorrect(boolean var1);

   void allowsUnrecognizedOptions();

   void recognizeAlternativeLongOptions(boolean var1);
}
