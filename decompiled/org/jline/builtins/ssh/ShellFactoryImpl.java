package org.jline.builtins.ssh;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.apache.sshd.common.channel.PtyMode;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.Signal;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellFactoryImpl implements ShellFactory {
   private static final Logger LOGGER = LoggerFactory.getLogger(ShellFactoryImpl.class);
   private final Consumer<Ssh.ShellParams> shell;

   public ShellFactoryImpl(Consumer<Ssh.ShellParams> shell) {
      this.shell = shell;
   }

   public Command createShell(ChannelSession session) {
      return new ShellFactoryImpl.ShellImpl();
   }

   static void flush(OutputStream... streams) {
      for (OutputStream s : streams) {
         try {
            s.flush();
         } catch (IOException var6) {
            LOGGER.debug("Error flushing " + s, var6);
         }
      }
   }

   static void close(Closeable... closeables) {
      for (Closeable c : closeables) {
         try {
            c.close();
         } catch (IOException var6) {
            LOGGER.debug("Error closing " + c, var6);
         }
      }
   }

   public class ShellImpl implements Command {
      private InputStream in;
      private OutputStream out;
      private OutputStream err;
      private ExitCallback callback;
      private boolean closed;

      public void setInputStream(InputStream in) {
         this.in = in;
      }

      public void setOutputStream(OutputStream out) {
         this.out = out;
      }

      public void setErrorStream(OutputStream err) {
         this.err = err;
      }

      public void setExitCallback(ExitCallback callback) {
         this.callback = callback;
      }

      public void start(ChannelSession session, Environment env) throws IOException {
         try {
            new Thread(() -> this.run(session, env)).start();
         } catch (Exception var4) {
            throw new IOException("Unable to start shell", var4);
         }
      }

      public void run(ChannelSession session, Environment env) {
         try {
            Attributes attributes = new Attributes();

            for (Entry<PtyMode, Integer> e : env.getPtyModes().entrySet()) {
               switch ((PtyMode)e.getKey()) {
                  case VINTR:
                     attributes.setControlChar(Attributes.ControlChar.VINTR, e.getValue());
                     break;
                  case VQUIT:
                     attributes.setControlChar(Attributes.ControlChar.VQUIT, e.getValue());
                     break;
                  case VERASE:
                     attributes.setControlChar(Attributes.ControlChar.VERASE, e.getValue());
                     break;
                  case VKILL:
                     attributes.setControlChar(Attributes.ControlChar.VKILL, e.getValue());
                     break;
                  case VEOF:
                     attributes.setControlChar(Attributes.ControlChar.VEOF, e.getValue());
                     break;
                  case VEOL:
                     attributes.setControlChar(Attributes.ControlChar.VEOL, e.getValue());
                     break;
                  case VEOL2:
                     attributes.setControlChar(Attributes.ControlChar.VEOL2, e.getValue());
                     break;
                  case VSTART:
                     attributes.setControlChar(Attributes.ControlChar.VSTART, e.getValue());
                     break;
                  case VSTOP:
                     attributes.setControlChar(Attributes.ControlChar.VSTOP, e.getValue());
                     break;
                  case VSUSP:
                     attributes.setControlChar(Attributes.ControlChar.VSUSP, e.getValue());
                     break;
                  case VDSUSP:
                     attributes.setControlChar(Attributes.ControlChar.VDSUSP, e.getValue());
                     break;
                  case VREPRINT:
                     attributes.setControlChar(Attributes.ControlChar.VREPRINT, e.getValue());
                     break;
                  case VWERASE:
                     attributes.setControlChar(Attributes.ControlChar.VWERASE, e.getValue());
                     break;
                  case VLNEXT:
                     attributes.setControlChar(Attributes.ControlChar.VLNEXT, e.getValue());
                     break;
                  case VSTATUS:
                     attributes.setControlChar(Attributes.ControlChar.VSTATUS, e.getValue());
                     break;
                  case VDISCARD:
                     attributes.setControlChar(Attributes.ControlChar.VDISCARD, e.getValue());
                     break;
                  case ECHO:
                     attributes.setLocalFlag(Attributes.LocalFlag.ECHO, e.getValue() != 0);
                     break;
                  case ICANON:
                     attributes.setLocalFlag(Attributes.LocalFlag.ICANON, e.getValue() != 0);
                     break;
                  case ISIG:
                     attributes.setLocalFlag(Attributes.LocalFlag.ISIG, e.getValue() != 0);
                     break;
                  case ICRNL:
                     attributes.setInputFlag(Attributes.InputFlag.ICRNL, e.getValue() != 0);
                     break;
                  case INLCR:
                     attributes.setInputFlag(Attributes.InputFlag.INLCR, e.getValue() != 0);
                     break;
                  case IGNCR:
                     attributes.setInputFlag(Attributes.InputFlag.IGNCR, e.getValue() != 0);
                     break;
                  case OCRNL:
                     attributes.setOutputFlag(Attributes.OutputFlag.OCRNL, e.getValue() != 0);
                     break;
                  case ONLCR:
                     attributes.setOutputFlag(Attributes.OutputFlag.ONLCR, e.getValue() != 0);
                     break;
                  case ONLRET:
                     attributes.setOutputFlag(Attributes.OutputFlag.ONLRET, e.getValue() != 0);
                     break;
                  case OPOST:
                     attributes.setOutputFlag(Attributes.OutputFlag.OPOST, e.getValue() != 0);
               }
            }

            Terminal terminal = TerminalBuilder.builder()
               .name("JLine SSH")
               .type((String)env.getEnv().get("TERM"))
               .system(false)
               .streams(this.in, this.out)
               .attributes(attributes)
               .size(new Size(Integer.parseInt((String)env.getEnv().get("COLUMNS")), Integer.parseInt((String)env.getEnv().get("LINES"))))
               .build();
            env.addSignalListener((channel, signals) -> {
               terminal.setSize(new Size(Integer.parseInt((String)env.getEnv().get("COLUMNS")), Integer.parseInt((String)env.getEnv().get("LINES"))));
               terminal.raise(Terminal.Signal.WINCH);
            }, new Signal[]{Signal.WINCH});
            ShellFactoryImpl.this.shell.accept(new Ssh.ShellParams(env.getEnv(), session.getSession(), terminal, () -> this.destroy(session)));
         } catch (Throwable var6) {
            if (!this.closed) {
               ShellFactoryImpl.LOGGER.error("Error occured while executing shell", var6);
            }
         }
      }

      public void destroy(ChannelSession session) {
         if (!this.closed) {
            this.closed = true;
            ShellFactoryImpl.flush(this.out, this.err);
            ShellFactoryImpl.close(this.in, this.out, this.err);
            this.callback.onExit(0);
         }
      }
   }
}
