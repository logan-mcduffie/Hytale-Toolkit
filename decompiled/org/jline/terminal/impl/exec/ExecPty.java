package org.jline.terminal.impl.exec;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.impl.AbstractPty;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.ExecHelper;
import org.jline.utils.OSUtils;

public class ExecPty extends AbstractPty implements Pty {
   private final String name;

   public static Pty current(TerminalProvider provider, SystemStream systemStream) throws IOException {
      try {
         String result = ExecHelper.exec(true, OSUtils.TTY_COMMAND);
         if (systemStream != SystemStream.Output && systemStream != SystemStream.Error) {
            throw new IllegalArgumentException("systemStream should be Output or Error: " + systemStream);
         } else {
            return new ExecPty(provider, systemStream, result.trim());
         }
      } catch (IOException var3) {
         throw new IOException("Not a tty", var3);
      }
   }

   protected ExecPty(TerminalProvider provider, SystemStream systemStream, String name) {
      super(provider, systemStream);
      this.name = name;
   }

   @Override
   public void close() throws IOException {
   }

   public String getName() {
      return this.name;
   }

   @Override
   public InputStream getMasterInput() {
      throw new UnsupportedOperationException();
   }

   @Override
   public OutputStream getMasterOutput() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected InputStream doGetSlaveInput() throws IOException {
      return this.systemStream != null ? new FileInputStream(FileDescriptor.in) : new FileInputStream(this.getName());
   }

   @Override
   public OutputStream getSlaveOutput() throws IOException {
      return this.systemStream == SystemStream.Output
         ? new FileOutputStream(FileDescriptor.out)
         : (this.systemStream == SystemStream.Error ? new FileOutputStream(FileDescriptor.err) : new FileOutputStream(this.getName()));
   }

   @Override
   public Attributes getAttr() throws IOException {
      String cfg = this.doGetConfig();
      return doGetAttr(cfg);
   }

   @Override
   protected void doSetAttr(Attributes attr) throws IOException {
      List<String> commands = this.getFlagsToSet(attr, this.getAttr());
      if (!commands.isEmpty()) {
         commands.add(0, OSUtils.STTY_COMMAND);
         if (this.systemStream == null) {
            commands.add(1, OSUtils.STTY_F_OPTION);
            commands.add(2, this.getName());
         }

         try {
            ExecHelper.exec(this.systemStream != null, commands.toArray(new String[0]));
         } catch (IOException var4) {
            if (!var4.toString().contains("unable to perform all requested operations")) {
               throw var4;
            }

            commands = this.getFlagsToSet(attr, this.getAttr());
            if (!commands.isEmpty()) {
               throw new IOException("Could not set the following flags: " + String.join(", ", commands), var4);
            }
         }
      }
   }

   protected List<String> getFlagsToSet(Attributes attr, Attributes current) {
      List<String> commands = new ArrayList<>();

      for (Attributes.InputFlag flag : Attributes.InputFlag.values()) {
         if (attr.getInputFlag(flag) != current.getInputFlag(flag) && flag != Attributes.InputFlag.INORMEOL) {
            commands.add((attr.getInputFlag(flag) ? flag.name() : "-" + flag.name()).toLowerCase());
         }
      }

      for (Attributes.OutputFlag flagx : Attributes.OutputFlag.values()) {
         if (attr.getOutputFlag(flagx) != current.getOutputFlag(flagx)) {
            commands.add((attr.getOutputFlag(flagx) ? flagx.name() : "-" + flagx.name()).toLowerCase());
         }
      }

      for (Attributes.ControlFlag flagxx : Attributes.ControlFlag.values()) {
         if (attr.getControlFlag(flagxx) != current.getControlFlag(flagxx)) {
            commands.add((attr.getControlFlag(flagxx) ? flagxx.name() : "-" + flagxx.name()).toLowerCase());
         }
      }

      for (Attributes.LocalFlag flagxxx : Attributes.LocalFlag.values()) {
         if (attr.getLocalFlag(flagxxx) != current.getLocalFlag(flagxxx)) {
            commands.add((attr.getLocalFlag(flagxxx) ? flagxxx.name() : "-" + flagxxx.name()).toLowerCase());
         }
      }

      String undef = System.getProperty("os.name").toLowerCase().startsWith("hp") ? "^-" : "undef";

      for (Attributes.ControlChar cchar : Attributes.ControlChar.values()) {
         int v = attr.getControlChar(cchar);
         if (v >= 0 && v != current.getControlChar(cchar)) {
            String str = "";
            commands.add(cchar.name().toLowerCase().substring(1));
            if (cchar == Attributes.ControlChar.VMIN || cchar == Attributes.ControlChar.VTIME) {
               commands.add(Integer.toString(v));
            } else if (v == 0) {
               commands.add(undef);
            } else {
               if (v >= 128) {
                  v -= 128;
                  str = str + "M-";
               }

               if (v < 32 || v == 127) {
                  v ^= 64;
                  str = str + "^";
               }

               str = str + (char)v;
               commands.add(str);
            }
         }
      }

      return commands;
   }

   @Override
   public Size getSize() throws IOException {
      String cfg = this.doGetConfig();
      return doGetSize(cfg);
   }

   protected String doGetConfig() throws IOException {
      return this.systemStream != null
         ? ExecHelper.exec(true, OSUtils.STTY_COMMAND, "-a")
         : ExecHelper.exec(false, OSUtils.STTY_COMMAND, OSUtils.STTY_F_OPTION, this.getName(), "-a");
   }

   public static Attributes doGetAttr(String cfg) throws IOException {
      Attributes attributes = new Attributes();

      for (Attributes.InputFlag flag : Attributes.InputFlag.values()) {
         Boolean value = doGetFlag(cfg, flag);
         if (value != null) {
            attributes.setInputFlag(flag, value);
         }
      }

      for (Attributes.OutputFlag flagx : Attributes.OutputFlag.values()) {
         Boolean value = doGetFlag(cfg, flagx);
         if (value != null) {
            attributes.setOutputFlag(flagx, value);
         }
      }

      for (Attributes.ControlFlag flagxx : Attributes.ControlFlag.values()) {
         Boolean value = doGetFlag(cfg, flagxx);
         if (value != null) {
            attributes.setControlFlag(flagxx, value);
         }
      }

      for (Attributes.LocalFlag flagxxx : Attributes.LocalFlag.values()) {
         Boolean value = doGetFlag(cfg, flagxxx);
         if (value != null) {
            attributes.setLocalFlag(flagxxx, value);
         }
      }

      for (Attributes.ControlChar cchar : Attributes.ControlChar.values()) {
         String name = cchar.name().toLowerCase().substring(1);
         if ("reprint".endsWith(name)) {
            name = "(?:reprint|rprnt)";
         }

         Matcher matcher = Pattern.compile("[\\s;]" + name + "\\s*=\\s*(.+?)[\\s;]").matcher(cfg);
         if (matcher.find()) {
            attributes.setControlChar(cchar, parseControlChar(matcher.group(1).toUpperCase()));
         }
      }

      return attributes;
   }

   private static Boolean doGetFlag(String cfg, Enum<?> flag) {
      Matcher matcher = Pattern.compile("(?:^|[\\s;])(\\-?" + flag.name().toLowerCase() + ")(?:[\\s;]|$)").matcher(cfg);
      return matcher.find() ? !matcher.group(1).startsWith("-") : null;
   }

   static int parseControlChar(String str) {
      if ("<UNDEF>".equals(str)) {
         return -1;
      } else if ("DEL".equalsIgnoreCase(str)) {
         return 127;
      } else if (str.charAt(0) == '0') {
         return Integer.parseInt(str, 8);
      } else if (str.charAt(0) >= '1' && str.charAt(0) <= '9') {
         return Integer.parseInt(str, 10);
      } else if (str.charAt(0) == '^') {
         return str.charAt(1) == 63 ? 127 : str.charAt(1) - 64;
      } else if (str.charAt(0) != 'M' || str.charAt(1) != '-') {
         return str.charAt(0);
      } else if (str.charAt(2) != '^') {
         return str.charAt(2) + 128;
      } else {
         return str.charAt(3) == 63 ? 255 : str.charAt(3) - 64 + 128;
      }
   }

   static Size doGetSize(String cfg) throws IOException {
      return new Size(doGetInt("columns", cfg), doGetInt("rows", cfg));
   }

   static int doGetInt(String name, String cfg) throws IOException {
      String[] patterns = new String[]{"\\b([0-9]+)\\s+" + name + "\\b", "\\b" + name + "\\s+([0-9]+)\\b", "\\b" + name + "\\s*=\\s*([0-9]+)\\b"};

      for (String pattern : patterns) {
         Matcher matcher = Pattern.compile(pattern).matcher(cfg);
         if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
         }
      }

      return 0;
   }

   @Override
   public void setSize(Size size) throws IOException {
      if (this.systemStream != null) {
         ExecHelper.exec(true, OSUtils.STTY_COMMAND, "columns", Integer.toString(size.getColumns()), "rows", Integer.toString(size.getRows()));
      } else {
         ExecHelper.exec(
            false,
            OSUtils.STTY_COMMAND,
            OSUtils.STTY_F_OPTION,
            this.getName(),
            "columns",
            Integer.toString(size.getColumns()),
            "rows",
            Integer.toString(size.getRows())
         );
      }
   }

   @Override
   public String toString() {
      return "ExecPty[" + this.getName() + (this.systemStream != null ? ", system]" : "]");
   }
}
