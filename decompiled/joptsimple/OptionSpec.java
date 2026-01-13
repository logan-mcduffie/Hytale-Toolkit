package joptsimple;

import java.util.List;

public interface OptionSpec<V> {
   List<V> values(OptionSet var1);

   V value(OptionSet var1);

   List<String> options();

   boolean isForHelp();
}
