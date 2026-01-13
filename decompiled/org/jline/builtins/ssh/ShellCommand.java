package org.jline.builtins.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.session.ServerSession;

public class ShellCommand implements Command {
   private static final Logger LOGGER = Logger.getLogger(ShellCommand.class.getName());
   private final Consumer<Ssh.ExecuteParams> execute;
   private final String command;
   private InputStream in;
   private OutputStream out;
   private OutputStream err;
   private ExitCallback callback;
   private ServerSession session;
   private Environment env;

   public ShellCommand(Consumer<Ssh.ExecuteParams> execute, String command) {
      this.execute = execute;
      this.command = command;
   }

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

   public void start(ChannelSession channel, Environment env) throws IOException {
      this.session = channel.getSession();
      this.env = env;
      new Thread(this::run).start();
   }

   private void run() {
      int exitStatus = 0;

      try {
         this.execute.accept(new Ssh.ExecuteParams(this.command, this.env.getEnv(), this.session, this.in, this.out, this.err));
      } catch (RuntimeException var9) {
         RuntimeException e = var9;
         exitStatus = 1;
         LOGGER.log(Level.SEVERE, "Unable to start shell", (Throwable)var9);

         try {
            Throwable t = (Throwable)(e.getCause() != null ? e.getCause() : e);
            this.err.write(t.toString().getBytes());
            this.err.flush();
         } catch (IOException var8) {
         }
      } finally {
         ShellFactoryImpl.close(this.in, this.out, this.err);
         this.callback.onExit(exitStatus);
      }
   }

   public void destroy(ChannelSession channel) {
   }
}
