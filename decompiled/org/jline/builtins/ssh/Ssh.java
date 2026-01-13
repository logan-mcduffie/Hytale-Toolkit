package org.jline.builtins.ssh;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.NamedResource;
import org.apache.sshd.common.channel.PtyMode;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.common.util.io.input.NoCloseInputStream;
import org.apache.sshd.common.util.io.output.NoCloseOutputStream;
import org.apache.sshd.scp.server.ScpCommandFactory.Builder;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.jline.builtins.Options;
import org.jline.reader.LineReader;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;

public class Ssh {
   public static final String[] functions = new String[]{"ssh", "sshd"};
   private static final int defaultPort = 2022;
   private final Consumer<Ssh.ShellParams> shell;
   private final Consumer<Ssh.ExecuteParams> execute;
   private final Supplier<SshServer> serverBuilder;
   private final Supplier<SshClient> clientBuilder;
   private SshServer server;
   private int port;
   private String ip;

   public Ssh(Consumer<Ssh.ShellParams> shell, Consumer<Ssh.ExecuteParams> execute, Supplier<SshServer> serverBuilder, Supplier<SshClient> clientBuilder) {
      this.shell = shell;
      this.execute = execute;
      this.serverBuilder = serverBuilder;
      this.clientBuilder = clientBuilder;
   }

   public void ssh(Terminal terminal, LineReader reader, String user, InputStream stdin, PrintStream stdout, PrintStream stderr, String[] argv) throws Exception {
      String[] usage = new String[]{"ssh - connect to a server using ssh", "Usage: ssh [user@]hostname [command]", "  -? --help                show help"};
      Options opt = Options.compile(usage).parse(argv, true);
      List<String> args = opt.args();
      if (!opt.isSet("help") && !args.isEmpty()) {
         String username = user;
         String hostname = args.remove(0);
         int port = this.port;
         String command = null;
         int idx = hostname.indexOf(64);
         if (idx >= 0) {
            username = hostname.substring(0, idx);
            hostname = hostname.substring(idx + 1);
         }

         idx = hostname.indexOf(58);
         if (idx >= 0) {
            port = Integer.parseInt(hostname.substring(idx + 1));
            hostname = hostname.substring(0, idx);
         }

         if (!args.isEmpty()) {
            command = String.join(" ", args);
         }

         SshClient client = this.clientBuilder.get();

         try {
            Ssh.JLineUserInteraction ui = new Ssh.JLineUserInteraction(terminal, reader, stderr);
            client.setFilePasswordProvider(ui);
            client.setUserInteraction(ui);
            client.start();
            ClientSession sshSession = this.connectWithRetries(terminal.writer(), client, username, hostname, port, 3);

            try {
               sshSession.auth().verify();
               if (command != null) {
                  ClientChannel channel = sshSession.createChannel("exec", command + "\n");
                  channel.setIn(new ByteArrayInputStream(new byte[0]));
                  channel.setOut(new NoCloseOutputStream(stdout));
                  channel.setErr(new NoCloseOutputStream(stderr));
                  channel.open().verify();
                  channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
               } else {
                  ChannelShell channel = sshSession.createShellChannel();
                  Attributes attributes = terminal.enterRawMode();

                  try {
                     Map<PtyMode, Integer> modes = new HashMap<>();
                     setMode(modes, PtyMode.VINTR, attributes.getControlChar(Attributes.ControlChar.VINTR));
                     setMode(modes, PtyMode.VQUIT, attributes.getControlChar(Attributes.ControlChar.VQUIT));
                     setMode(modes, PtyMode.VERASE, attributes.getControlChar(Attributes.ControlChar.VERASE));
                     setMode(modes, PtyMode.VKILL, attributes.getControlChar(Attributes.ControlChar.VKILL));
                     setMode(modes, PtyMode.VEOF, attributes.getControlChar(Attributes.ControlChar.VEOF));
                     setMode(modes, PtyMode.VEOL, attributes.getControlChar(Attributes.ControlChar.VEOL));
                     setMode(modes, PtyMode.VEOL2, attributes.getControlChar(Attributes.ControlChar.VEOL2));
                     setMode(modes, PtyMode.VSTART, attributes.getControlChar(Attributes.ControlChar.VSTART));
                     setMode(modes, PtyMode.VSTOP, attributes.getControlChar(Attributes.ControlChar.VSTOP));
                     setMode(modes, PtyMode.VSUSP, attributes.getControlChar(Attributes.ControlChar.VSUSP));
                     setMode(modes, PtyMode.VDSUSP, attributes.getControlChar(Attributes.ControlChar.VDSUSP));
                     setMode(modes, PtyMode.VREPRINT, attributes.getControlChar(Attributes.ControlChar.VREPRINT));
                     setMode(modes, PtyMode.VWERASE, attributes.getControlChar(Attributes.ControlChar.VWERASE));
                     setMode(modes, PtyMode.VLNEXT, attributes.getControlChar(Attributes.ControlChar.VLNEXT));
                     setMode(modes, PtyMode.VSTATUS, attributes.getControlChar(Attributes.ControlChar.VSTATUS));
                     setMode(modes, PtyMode.VDISCARD, attributes.getControlChar(Attributes.ControlChar.VDISCARD));
                     setMode(modes, PtyMode.IGNPAR, getFlag(attributes, Attributes.InputFlag.IGNPAR));
                     setMode(modes, PtyMode.PARMRK, getFlag(attributes, Attributes.InputFlag.PARMRK));
                     setMode(modes, PtyMode.INPCK, getFlag(attributes, Attributes.InputFlag.INPCK));
                     setMode(modes, PtyMode.ISTRIP, getFlag(attributes, Attributes.InputFlag.ISTRIP));
                     setMode(modes, PtyMode.INLCR, getFlag(attributes, Attributes.InputFlag.INLCR));
                     setMode(modes, PtyMode.IGNCR, getFlag(attributes, Attributes.InputFlag.IGNCR));
                     setMode(modes, PtyMode.ICRNL, getFlag(attributes, Attributes.InputFlag.ICRNL));
                     setMode(modes, PtyMode.IXON, getFlag(attributes, Attributes.InputFlag.IXON));
                     setMode(modes, PtyMode.IXANY, getFlag(attributes, Attributes.InputFlag.IXANY));
                     setMode(modes, PtyMode.IXOFF, getFlag(attributes, Attributes.InputFlag.IXOFF));
                     setMode(modes, PtyMode.ISIG, getFlag(attributes, Attributes.LocalFlag.ISIG));
                     setMode(modes, PtyMode.ICANON, getFlag(attributes, Attributes.LocalFlag.ICANON));
                     setMode(modes, PtyMode.ECHO, getFlag(attributes, Attributes.LocalFlag.ECHO));
                     setMode(modes, PtyMode.ECHOE, getFlag(attributes, Attributes.LocalFlag.ECHOE));
                     setMode(modes, PtyMode.ECHOK, getFlag(attributes, Attributes.LocalFlag.ECHOK));
                     setMode(modes, PtyMode.ECHONL, getFlag(attributes, Attributes.LocalFlag.ECHONL));
                     setMode(modes, PtyMode.NOFLSH, getFlag(attributes, Attributes.LocalFlag.NOFLSH));
                     setMode(modes, PtyMode.TOSTOP, getFlag(attributes, Attributes.LocalFlag.TOSTOP));
                     setMode(modes, PtyMode.IEXTEN, getFlag(attributes, Attributes.LocalFlag.IEXTEN));
                     setMode(modes, PtyMode.OPOST, getFlag(attributes, Attributes.OutputFlag.OPOST));
                     setMode(modes, PtyMode.ONLCR, getFlag(attributes, Attributes.OutputFlag.ONLCR));
                     setMode(modes, PtyMode.OCRNL, getFlag(attributes, Attributes.OutputFlag.OCRNL));
                     setMode(modes, PtyMode.ONOCR, getFlag(attributes, Attributes.OutputFlag.ONOCR));
                     setMode(modes, PtyMode.ONLRET, getFlag(attributes, Attributes.OutputFlag.ONLRET));
                     channel.setPtyModes(modes);
                     channel.setPtyColumns(terminal.getWidth());
                     channel.setPtyLines(terminal.getHeight());
                     channel.setAgentForwarding(true);
                     channel.setEnv("TERM", terminal.getType());
                     channel.setIn(new NoCloseInputStream(stdin));
                     channel.setOut(new NoCloseOutputStream(stdout));
                     channel.setErr(new NoCloseOutputStream(stderr));
                     channel.open().verify();
                     Terminal.SignalHandler prevWinchHandler = terminal.handle(Terminal.Signal.WINCH, signal -> {
                        try {
                           Size size = terminal.getSize();
                           channel.sendWindowChange(size.getColumns(), size.getRows());
                        } catch (IOException var4x) {
                        }
                     });
                     Terminal.SignalHandler prevQuitHandler = terminal.handle(Terminal.Signal.QUIT, signal -> {
                        try {
                           channel.getInvertedIn().write(attributes.getControlChar(Attributes.ControlChar.VQUIT));
                           channel.getInvertedIn().flush();
                        } catch (IOException var4x) {
                        }
                     });
                     Terminal.SignalHandler prevIntHandler = terminal.handle(Terminal.Signal.INT, signal -> {
                        try {
                           channel.getInvertedIn().write(attributes.getControlChar(Attributes.ControlChar.VINTR));
                           channel.getInvertedIn().flush();
                        } catch (IOException var4x) {
                        }
                     });
                     Terminal.SignalHandler prevStopHandler = terminal.handle(Terminal.Signal.TSTP, signal -> {
                        try {
                           channel.getInvertedIn().write(attributes.getControlChar(Attributes.ControlChar.VDSUSP));
                           channel.getInvertedIn().flush();
                        } catch (IOException var4x) {
                        }
                     });

                     try {
                        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
                     } finally {
                        terminal.handle(Terminal.Signal.WINCH, prevWinchHandler);
                        terminal.handle(Terminal.Signal.INT, prevIntHandler);
                        terminal.handle(Terminal.Signal.TSTP, prevStopHandler);
                        terminal.handle(Terminal.Signal.QUIT, prevQuitHandler);
                     }
                  } finally {
                     terminal.setAttributes(attributes);
                  }
               }
            } catch (Throwable var44) {
               if (sshSession != null) {
                  try {
                     sshSession.close();
                  } catch (Throwable var41) {
                     var44.addSuppressed(var41);
                  }
               }

               throw var44;
            }

            if (sshSession != null) {
               sshSession.close();
            }
         } catch (Throwable var45) {
            if (client != null) {
               try {
                  client.close();
               } catch (Throwable var40) {
                  var45.addSuppressed(var40);
               }
            }

            throw var45;
         }

         if (client != null) {
            client.close();
         }
      } else {
         throw new Options.HelpException(opt.usage());
      }
   }

   private static void setMode(Map<PtyMode, Integer> modes, PtyMode vintr, int attributes) {
      if (attributes >= 0) {
         modes.put(vintr, attributes);
      }
   }

   private static int getFlag(Attributes attributes, Attributes.InputFlag flag) {
      return attributes.getInputFlag(flag) ? 1 : 0;
   }

   private static int getFlag(Attributes attributes, Attributes.OutputFlag flag) {
      return attributes.getOutputFlag(flag) ? 1 : 0;
   }

   private static int getFlag(Attributes attributes, Attributes.LocalFlag flag) {
      return attributes.getLocalFlag(flag) ? 1 : 0;
   }

   private ClientSession connectWithRetries(PrintWriter stdout, SshClient client, String username, String host, int port, int maxAttempts) throws Exception {
      ClientSession session = null;
      int retries = 0;

      do {
         ConnectFuture future = client.connect(username, host, port);
         future.await();

         try {
            session = (ClientSession)future.getSession();
         } catch (Exception var11) {
            if (retries++ >= maxAttempts) {
               throw var11;
            }

            Thread.sleep(2000L);
            stdout.println("retrying (attempt " + retries + ") ...");
         }
      } while (session == null);

      return session;
   }

   public void sshd(PrintStream stdout, PrintStream stderr, String[] argv) throws Exception {
      String[] usage = new String[]{
         "sshd - start an ssh server",
         "Usage: sshd [-i ip] [-p port] start | stop | status",
         "  -i --ip=INTERFACE        listen interface (default=127.0.0.1)",
         "  -p --port=PORT           listen port (default=2022)",
         "  -? --help                show help"
      };
      Options opt = Options.compile(usage).parse(argv, true);
      List<String> args = opt.args();
      if (!opt.isSet("help") && !args.isEmpty()) {
         String command = args.get(0);
         if ("start".equals(command)) {
            if (this.server != null) {
               throw new IllegalStateException("sshd is already running on port " + this.port);
            }

            this.ip = opt.get("ip");
            this.port = opt.getNumber("port");
            this.start();
            this.status(stdout);
         } else if ("stop".equals(command)) {
            if (this.server == null) {
               throw new IllegalStateException("sshd is not running.");
            }

            this.stop();
         } else {
            if (!"status".equals(command)) {
               throw opt.usageError("bad command: " + command);
            }

            this.status(stdout);
         }
      } else {
         throw new Options.HelpException(opt.usage());
      }
   }

   private void status(PrintStream stdout) {
      if (this.server != null) {
         stdout.println("sshd is running on " + this.ip + ":" + this.port);
      } else {
         stdout.println("sshd is not running.");
      }
   }

   private void start() throws IOException {
      this.server = this.serverBuilder.get();
      this.server.setPort(this.port);
      this.server.setHost(this.ip);
      this.server.setShellFactory(new ShellFactoryImpl(this.shell));
      this.server.setCommandFactory(new Builder().withDelegate((channel, command) -> new ShellCommand(this.execute, command)).build());
      this.server.setSubsystemFactories(Collections.singletonList(new org.apache.sshd.sftp.server.SftpSubsystemFactory.Builder().build()));
      this.server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
      this.server.start();
   }

   private void stop() throws IOException {
      try {
         this.server.stop();
      } finally {
         this.server = null;
      }
   }

   public static class ExecuteParams {
      private final String command;
      private final Map<String, String> env;
      private final ServerSession session;
      private final InputStream in;
      private final OutputStream out;
      private final OutputStream err;

      public ExecuteParams(String command, Map<String, String> env, ServerSession session, InputStream in, OutputStream out, OutputStream err) {
         this.command = command;
         this.session = session;
         this.env = env;
         this.in = in;
         this.out = out;
         this.err = err;
      }

      public String getCommand() {
         return this.command;
      }

      public Map<String, String> getEnv() {
         return this.env;
      }

      public ServerSession getSession() {
         return this.session;
      }

      public InputStream getIn() {
         return this.in;
      }

      public OutputStream getOut() {
         return this.out;
      }

      public OutputStream getErr() {
         return this.err;
      }
   }

   private static class JLineUserInteraction implements UserInteraction, FilePasswordProvider {
      private final Terminal terminal;
      private final LineReader reader;
      private final PrintStream stderr;

      public JLineUserInteraction(Terminal terminal, LineReader reader, PrintStream stderr) {
         this.terminal = terminal;
         this.reader = reader;
         this.stderr = stderr;
      }

      public String getPassword(SessionContext session, NamedResource resourceKey, int retryIndex) throws IOException {
         return this.readLine("Enter password for " + resourceKey + ":", false);
      }

      public void welcome(ClientSession session, String banner, String lang) {
         this.terminal.writer().println(banner);
      }

      public String[] interactive(ClientSession s, String name, String instruction, String lang, String[] prompt, boolean[] echo) {
         String[] answers = new String[prompt.length];

         try {
            for (int i = 0; i < prompt.length; i++) {
               answers[i] = this.readLine(prompt[i], echo[i]);
            }
         } catch (Exception var9) {
            this.stderr.append(var9.getClass().getSimpleName()).append(" while read prompts: ").println(var9.getMessage());
         }

         return answers;
      }

      public boolean isInteractionAllowed(ClientSession session) {
         return true;
      }

      public void serverVersionInfo(ClientSession session, List<String> lines) {
         for (String l : lines) {
            this.terminal.writer().append('\t').println(l);
         }
      }

      public String getUpdatedPassword(ClientSession session, String prompt, String lang) {
         try {
            return this.readLine(prompt, false);
         } catch (Exception var5) {
            this.stderr.append(var5.getClass().getSimpleName()).append(" while reading password: ").println(var5.getMessage());
            return null;
         }
      }

      private String readLine(String prompt, boolean echo) {
         return this.reader.readLine(prompt + " ", echo ? null : '\u0000');
      }
   }

   public static class ShellParams {
      private final Map<String, String> env;
      private final Terminal terminal;
      private final Runnable closer;
      private final ServerSession session;

      public ShellParams(Map<String, String> env, ServerSession session, Terminal terminal, Runnable closer) {
         this.env = env;
         this.session = session;
         this.terminal = terminal;
         this.closer = closer;
      }

      public Map<String, String> getEnv() {
         return this.env;
      }

      public ServerSession getSession() {
         return this.session;
      }

      public Terminal getTerminal() {
         return this.terminal;
      }

      public Runnable getCloser() {
         return this.closer;
      }
   }
}
