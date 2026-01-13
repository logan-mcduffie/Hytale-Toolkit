package org.jline.style;

import java.util.Objects;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class StyleExpression {
   private final StyleResolver resolver;

   public StyleExpression() {
      this(new StyleResolver(new NopStyleSource(), ""));
   }

   public StyleExpression(StyleResolver resolver) {
      this.resolver = Objects.requireNonNull(resolver);
   }

   public void evaluate(AttributedStringBuilder buff, String expression) {
      String translated = InterpolationHelper.substVars(expression, this::style, false);
      buff.appendAnsi(translated);
   }

   private String style(String key) {
      int idx = key.indexOf(32);
      if (idx > 0) {
         String spec = key.substring(0, idx);
         String value = key.substring(idx + 1);
         AttributedStyle style = this.resolver.resolve(spec);
         return new AttributedStringBuilder().style(style).ansiAppend(value).toAnsi();
      } else {
         return null;
      }
   }

   public AttributedString evaluate(String expression) {
      AttributedStringBuilder buff = new AttributedStringBuilder();
      this.evaluate(buff, expression);
      return buff.toAttributedString();
   }
}
