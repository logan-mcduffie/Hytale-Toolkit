package ch.randelshofer.fastdoubleparser.chr;

import java.util.LinkedHashSet;
import java.util.Set;

public interface CharSet {
   boolean containsKey(char var1);

   static CharSet copyOf(Set<Character> set, boolean ignoreCase) {
      set = applyIgnoreCase(set, ignoreCase);
      switch (set.size()) {
         case 0:
            return new CharSetOfNone();
         case 1:
            return new CharSetOfOne(set);
         default:
            return (CharSet)(set.size() < 5 ? new CharSetOfFew(set) : new CharToIntMap(set));
      }
   }

   static Set<Character> applyIgnoreCase(Set<Character> set, boolean ignoreCase) {
      if (ignoreCase) {
         LinkedHashSet<Character> convertedSet = new LinkedHashSet<>();

         for (Character ch : set) {
            convertedSet.add(ch);
            char lc = Character.toLowerCase(ch);
            char uc = Character.toUpperCase(ch);
            char uclc = Character.toLowerCase(uc);
            convertedSet.add(lc);
            convertedSet.add(uc);
            convertedSet.add(uclc);
         }

         set = convertedSet;
      }

      return set;
   }
}
