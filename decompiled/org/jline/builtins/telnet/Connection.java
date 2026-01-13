package org.jline.builtins.telnet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Connection extends Thread {
   private static final Logger LOG = Logger.getLogger(Connection.class.getName());
   private static int number;
   private boolean dead;
   private List<ConnectionListener> listeners;
   private ConnectionData connectionData;

   public Connection(ThreadGroup tcg, ConnectionData cd) {
      super(tcg, "Connection" + ++number);
      this.connectionData = cd;
      this.listeners = new CopyOnWriteArrayList<>();
      this.dead = false;
   }

   @Override
   public void run() {
      try {
         this.doRun();
      } catch (Exception var5) {
         LOG.log(Level.SEVERE, "run()", (Throwable)var5);
      } finally {
         if (!this.dead) {
            this.close();
         }
      }

      LOG.log(Level.FINE, "run():: Returning from " + this.toString());
   }

   protected abstract void doRun() throws Exception;

   protected abstract void doClose() throws Exception;

   public ConnectionData getConnectionData() {
      return this.connectionData;
   }

   public synchronized void close() {
      if (!this.dead) {
         try {
            this.dead = true;
            this.doClose();
         } catch (Exception var5) {
            LOG.log(Level.SEVERE, "close()", (Throwable)var5);
         }

         try {
            this.connectionData.getSocket().close();
         } catch (Exception var4) {
            LOG.log(Level.SEVERE, "close()", (Throwable)var4);
         }

         try {
            this.connectionData.getManager().registerClosedConnection(this);
         } catch (Exception var3) {
            LOG.log(Level.SEVERE, "close()", (Throwable)var3);
         }

         try {
            this.interrupt();
         } catch (Exception var2) {
            LOG.log(Level.SEVERE, "close()", (Throwable)var2);
         }

         LOG.log(Level.FINE, "Closed " + this.toString() + " and inactive.");
      }
   }

   public boolean isActive() {
      return !this.dead;
   }

   public void addConnectionListener(ConnectionListener cl) {
      this.listeners.add(cl);
   }

   public void removeConnectionListener(ConnectionListener cl) {
      this.listeners.remove(cl);
   }

   public void processConnectionEvent(ConnectionEvent ce) {
      for (ConnectionListener cl : this.listeners) {
         switch (ce.getType()) {
            case CONNECTION_IDLE:
               cl.connectionIdle(ce);
               break;
            case CONNECTION_TIMEDOUT:
               cl.connectionTimedOut(ce);
               break;
            case CONNECTION_LOGOUTREQUEST:
               cl.connectionLogoutRequest(ce);
               break;
            case CONNECTION_BREAK:
               cl.connectionSentBreak(ce);
               break;
            case CONNECTION_TERMINAL_GEOMETRY_CHANGED:
               cl.connectionTerminalGeometryChanged(ce);
         }
      }
   }
}
