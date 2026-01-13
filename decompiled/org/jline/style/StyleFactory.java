package org.jline.style;

import java.util.Objects;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

public class StyleFactory {
   private final StyleResolver resolver;

   public StyleFactory(StyleResolver resolver) {
      this.resolver = Objects.requireNonNull(resolver);
   }

   public AttributedString style(String style, String value) {
      Objects.requireNonNull(value);
      AttributedStyle astyle = this.resolver.resolve(style);
      return new AttributedString(value, astyle);
   }

   public AttributedString style(String style, String format, Object... params) {
      Objects.requireNonNull(format);
      Objects.requireNonNull(params);
      return this.style(style, String.format(format, params));
   }

   public AttributedString evaluate(String expression) {
      Objects.requireNonNull(expression);
      return new StyleExpression(this.resolver).evaluate(expression);
   }

   public AttributedString evaluate(String format, Object... params) {
      Objects.requireNonNull(format);
      Objects.requireNonNull(params);
      return this.evaluate(String.format(format, params));
   }
}
