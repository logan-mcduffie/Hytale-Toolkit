package org.jline.style;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;

public class StyledWriter extends PrintWriter {
   private final Terminal terminal;
   private final StyleExpression expression;

   public StyledWriter(Writer out, Terminal terminal, StyleResolver resolver, boolean autoFlush) {
      super(out, autoFlush);
      this.terminal = Objects.requireNonNull(terminal);
      this.expression = new StyleExpression(resolver);
   }

   public StyledWriter(OutputStream out, Terminal terminal, StyleResolver resolver, boolean autoFlush) {
      super(out, autoFlush);
      this.terminal = Objects.requireNonNull(terminal);
      this.expression = new StyleExpression(resolver);
   }

   @Override
   public void write(@Nonnull String value) {
      AttributedString result = this.expression.evaluate(value);
      super.write(result.toAnsi(this.terminal));
   }

   @Override
   public PrintWriter format(@Nonnull String format, Object... args) {
      this.print(String.format(format, args));
      return this;
   }

   @Override
   public PrintWriter format(Locale locale, @Nonnull String format, Object... args) {
      this.print(String.format(locale, format, args));
      return this;
   }
}
