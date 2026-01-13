package ch.randelshofer.fastdoubleparser.chr;

public class FormatCharSet implements CharSet {
   @Override
   public boolean containsKey(char ch) {
      return Character.getType(ch) == 16;
   }
}
