package org.jline.builtins.telnet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortListener implements Runnable {
   private static final Logger LOG = Logger.getLogger(PortListener.class.getName());
   private static final String logmsg = "Listening to Port {0,number,integer} with a connectivity queue size of {1,number,integer}.";
   private String name;
   private String ip;
   private int port;
   private int floodProtection;
   private ServerSocket serverSocket = null;
   private Thread thread;
   private ConnectionManager connectionManager;
   private boolean stopping = false;
   private boolean available;

   public PortListener(String name, String ip, int port, int floodprot) {
      this.name = name;
      this.available = false;
      this.ip = ip;
      this.port = port;
      this.floodProtection = floodprot;
   }

   public String getName() {
      return this.name;
   }

   public boolean isAvailable() {
      return this.available;
   }

   public void setAvailable(boolean b) {
      this.available = b;
   }

   public void start() {
      LOG.log(Level.FINE, "start()");
      this.thread = new Thread(this);
      this.thread.start();
      this.available = true;
   }

   public void stop() {
      LOG.log(Level.FINE, "stop()::" + this.toString());
      this.stopping = true;
      this.available = false;
      this.connectionManager.stop();

      try {
         this.serverSocket.close();
      } catch (IOException var3) {
         LOG.log(Level.SEVERE, "stop()", (Throwable)var3);
      }

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         LOG.log(Level.SEVERE, "stop()", (Throwable)var2);
      }

      LOG.info("stop()::Stopped " + this.toString());
   }

   @Override
   public void run() {
      try {
         this.serverSocket = new ServerSocket(this.port, this.floodProtection, this.ip != null ? InetAddress.getByName(this.ip) : null);
         LOG.info(
            MessageFormat.format("Listening to Port {0,number,integer} with a connectivity queue size of {1,number,integer}.", this.port, this.floodProtection)
         );

         do {
            try {
               Socket s = this.serverSocket.accept();
               if (this.available) {
                  this.connectionManager.makeConnection(s);
               } else {
                  s.close();
               }
            } catch (SocketException var2) {
               if (this.stopping) {
                  LOG.log(Level.FINE, "run(): ServerSocket closed by stop()");
               } else {
                  LOG.log(Level.SEVERE, "run()", (Throwable)var2);
               }
            }
         } while (!this.stopping);
      } catch (IOException var3) {
         LOG.log(Level.SEVERE, "run()", (Throwable)var3);
      }

      LOG.log(Level.FINE, "run(): returning.");
   }

   public ConnectionManager getConnectionManager() {
      return this.connectionManager;
   }

   public void setConnectionManager(ConnectionManager connectionManager) {
      this.connectionManager = connectionManager;
   }
}
