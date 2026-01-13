package ch.randelshofer.fastdoubleparser.bte;

import java.util.LinkedHashSet;
import java.util.Set;

public interface ByteSet {
   boolean containsKey(byte var1);

   static ByteSet copyOf(Set<Character> set, boolean ignoreCase) {
      set = applyIgnoreCase(set, ignoreCase);
      switch (set.size()) {
         case 0:
            return new ByteSetOfNone();
         case 1:
            return new ByteSetOfOne(set);
         default:
            return (ByteSet)(set.size() < 5 ? new ByteSetOfFew(set) : new ByteToIntMap(set));
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
