package org.fusesource.jansi;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.fusesource.jansi.io.AnsiOutputStream;

public class AnsiPrintStream extends PrintStream {
   public AnsiPrintStream(AnsiOutputStream out, boolean autoFlush) {
      super(out, autoFlush);
   }

   public AnsiPrintStream(AnsiOutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
      super(out, autoFlush, encoding);
   }

   protected AnsiOutputStream getOut() {
      return (AnsiOutputStream)this.out;
   }

   public AnsiType getType() {
      return this.getOut().getType();
   }

   public AnsiColors getColors() {
      return this.getOut().getColors();
   }

   public AnsiMode getMode() {
      return this.getOut().getMode();
   }

   public void setMode(AnsiMode ansiMode) {
      this.getOut().setMode(ansiMode);
   }

   public boolean isResetAtUninstall() {
      return this.getOut().isResetAtUninstall();
   }

   public void setResetAtUninstall(boolean resetAtClose) {
      this.getOut().setResetAtUninstall(resetAtClose);
   }

   public int getTerminalWidth() {
      return this.getOut().getTerminalWidth();
   }

   public void install() throws IOException {
      this.getOut().install();
   }

   public void uninstall() throws IOException {
      AnsiOutputStream out = this.getOut();
      if (out != null) {
         out.uninstall();
      }
   }

   @Override
   public String toString() {
      return "AnsiPrintStream{type="
         + this.getType()
         + ", colors="
         + this.getColors()
         + ", mode="
         + this.getMode()
         + ", resetAtUninstall="
         + this.isResetAtUninstall()
         + "}";
   }
}
