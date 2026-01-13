package org.jline.reader.impl;

import java.util.regex.Pattern;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.WCWidth;

public class DefaultHighlighter implements Highlighter {
   protected Pattern errorPattern;
   protected int errorIndex = -1;

   @Override
   public void setErrorPattern(Pattern errorPattern) {
      this.errorPattern = errorPattern;
   }

   @Override
   public void setErrorIndex(int errorIndex) {
      this.errorIndex = errorIndex;
   }

   @Override
   public AttributedString highlight(LineReader reader, String buffer) {
      int underlineStart = -1;
      int underlineEnd = -1;
      int negativeStart = -1;
      int negativeEnd = -1;
      boolean first = true;
      String search = reader.getSearchTerm();
      if (search != null && search.length() > 0) {
         underlineStart = buffer.indexOf(search);
         if (underlineStart >= 0) {
            underlineEnd = underlineStart + search.length() - 1;
         }
      }

      if (reader.getRegionActive() != LineReader.RegionType.NONE) {
         negativeStart = reader.getRegionMark();
         negativeEnd = reader.getBuffer().cursor();
         if (negativeStart > negativeEnd) {
            int x = negativeEnd;
            negativeEnd = negativeStart;
            negativeStart = x;
         }

         if (reader.getRegionActive() == LineReader.RegionType.LINE) {
            while (negativeStart > 0 && reader.getBuffer().atChar(negativeStart - 1) != 10) {
               negativeStart--;
            }

            while (negativeEnd < reader.getBuffer().length() - 1 && reader.getBuffer().atChar(negativeEnd + 1) != 10) {
               negativeEnd++;
            }
         }
      }

      AttributedStringBuilder sb = new AttributedStringBuilder();
      this.commandStyle(reader, sb, true);

      for (int i = 0; i < buffer.length(); i++) {
         if (i == underlineStart) {
            sb.style(AttributedStyle::underline);
         }

         if (i == negativeStart) {
            sb.style(AttributedStyle::inverse);
         }

         if (i == this.errorIndex) {
            sb.style(AttributedStyle::inverse);
         }

         char c = buffer.charAt(i);
         if (first && Character.isSpaceChar(c)) {
            first = false;
            this.commandStyle(reader, sb, false);
         }

         if (c == '\t' || c == '\n') {
            sb.append(c);
         } else if (c < ' ') {
            sb.style(AttributedStyle::inverseNeg).append('^').append((char)(c + '@')).style(AttributedStyle::inverseNeg);
         } else {
            int w = WCWidth.wcwidth(c);
            if (w > 0) {
               sb.append(c);
            }
         }

         if (i == underlineEnd) {
            sb.style(AttributedStyle::underlineOff);
         }

         if (i == negativeEnd) {
            sb.style(AttributedStyle::inverseOff);
         }

         if (i == this.errorIndex) {
            sb.style(AttributedStyle::inverseOff);
         }
      }

      if (this.errorPattern != null) {
         sb.styleMatches(this.errorPattern, AttributedStyle.INVERSE);
      }

      return sb.toAttributedString();
   }

   protected void commandStyle(LineReader reader, AttributedStringBuilder sb, boolean enable) {
   }
}
