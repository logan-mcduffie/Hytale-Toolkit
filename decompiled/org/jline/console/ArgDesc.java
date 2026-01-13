package org.jline.console;

import java.util.ArrayList;
import java.util.List;
import org.jline.utils.AttributedString;

public class ArgDesc {
   private final String name;
   private final List<AttributedString> description;

   public ArgDesc(String name) {
      this(name, new ArrayList<>());
   }

   public ArgDesc(String name, List<AttributedString> description) {
      if (!name.contains("\t") && !name.contains(" ")) {
         this.name = name;
         this.description = new ArrayList<>(description);
      } else {
         throw new IllegalArgumentException("Bad argument name: " + name);
      }
   }

   public String getName() {
      return this.name;
   }

   public List<AttributedString> getDescription() {
      return this.description;
   }

   public static List<ArgDesc> doArgNames(List<String> names) {
      List<ArgDesc> out = new ArrayList<>();

      for (String n : names) {
         out.add(new ArgDesc(n));
      }

      return out;
   }
}
