package org.jline.builtins.telnet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ConnectionManager implements Runnable {
   private static Logger LOG = Logger.getLogger(ConnectionManager.class.getName());
   private final List<Connection> openConnections;
   private Thread thread;
   private ThreadGroup threadGroup;
   private Stack<Connection> closedConnections;
   private ConnectionFilter connectionFilter;
   private int maxConnections;
   private int warningTimeout;
   private int disconnectTimeout;
   private int housekeepingInterval;
   private String loginShell;
   private boolean lineMode = false;
   private boolean stopping = false;

   public ConnectionManager() {
      this.threadGroup = new ThreadGroup(this.toString() + "Connections");
      this.closedConnections = new Stack<>();
      this.openConnections = Collections.synchronizedList(new ArrayList<>(100));
   }

   public ConnectionManager(int con, int timew, int timedis, int hoke, ConnectionFilter filter, String lsh, boolean lm) {
      this();
      this.connectionFilter = filter;
      this.loginShell = lsh;
      this.lineMode = lm;
      this.maxConnections = con;
      this.warningTimeout = timew;
      this.disconnectTimeout = timedis;
      this.housekeepingInterval = hoke;
   }

   public ConnectionFilter getConnectionFilter() {
      return this.connectionFilter;
   }

   public void setConnectionFilter(ConnectionFilter filter) {
      this.connectionFilter = filter;
   }

   public int openConnectionCount() {
      return this.openConnections.size();
   }

   public Connection getConnection(int idx) {
      synchronized (this.openConnections) {
         return this.openConnections.get(idx);
      }
   }

   public Connection[] getConnectionsByAdddress(InetAddress addr) {
      ArrayList<Connection> l = new ArrayList<>();
      synchronized (this.openConnections) {
         for (Connection connection : this.openConnections) {
            if (connection.getConnectionData().getInetAddress().equals(addr)) {
               l.add(connection);
            }
         }
      }

      Connection[] conns = new Connection[l.size()];
      return l.toArray(conns);
   }

   public void start() {
      this.thread = new Thread(this);
      this.thread.start();
   }

   public void stop() {
      LOG.log(Level.FINE, "stop()::" + this.toString());
      this.stopping = true;

      try {
         if (this.thread != null) {
            this.thread.join();
         }
      } catch (InterruptedException var7) {
         LOG.log(Level.SEVERE, "stop()", (Throwable)var7);
      }

      synchronized (this.openConnections) {
         for (Connection tc : this.openConnections) {
            try {
               tc.close();
            } catch (Exception var6) {
               LOG.log(Level.SEVERE, "stop()", (Throwable)var6);
            }
         }

         this.openConnections.clear();
      }

      LOG.log(Level.FINE, "stop():: Stopped " + this.toString());
   }

   public void makeConnection(Socket insock) {
      LOG.log(Level.FINE, "makeConnection()::" + insock.toString());
      if (this.connectionFilter != null && !this.connectionFilter.isAllowed(insock.getInetAddress())) {
         LOG.info("makeConnection():: Active Filter blocked incoming connection.");

         try {
            insock.close();
         } catch (IOException var7) {
         }
      } else {
         ConnectionData newCD = new ConnectionData(insock, this);
         newCD.setLoginShell(this.loginShell);
         newCD.setLineMode(this.lineMode);
         if (this.openConnections.size() < this.maxConnections) {
            Connection con = this.createConnection(this.threadGroup, newCD);
            Object[] args = new Object[]{this.openConnections.size() + 1};
            LOG.info(MessageFormat.format("connection #{0,number,integer} made.", args));
            synchronized (this.openConnections) {
               this.openConnections.add(con);
            }

            con.start();
         }
      }
   }

   protected abstract Connection createConnection(ThreadGroup var1, ConnectionData var2);

   @Override
   public void run() {
      try {
         while (true) {
            this.cleanupClosed();
            this.checkOpenConnections();
            Thread.sleep(this.housekeepingInterval);
            if (!this.stopping) {
               continue;
            }
         }
      } catch (Exception var2) {
         LOG.log(Level.SEVERE, "run()", (Throwable)var2);
      }

      LOG.log(Level.FINE, "run():: Ran out " + this.toString());
   }

   private void cleanupClosed() {
      if (!this.stopping) {
         while (!this.closedConnections.isEmpty()) {
            Connection nextOne = this.closedConnections.pop();
            LOG.info("cleanupClosed():: Removing closed connection " + nextOne.toString());
            synchronized (this.openConnections) {
               this.openConnections.remove(nextOne);
            }
         }
      }
   }

   private void checkOpenConnections() {
      if (!this.stopping) {
         synchronized (this.openConnections) {
            for (Connection conn : this.openConnections) {
               ConnectionData cd = conn.getConnectionData();
               if (!conn.isActive()) {
                  this.registerClosedConnection(conn);
               } else {
                  long inactivity = System.currentTimeMillis() - cd.getLastActivity();
                  if (inactivity > this.warningTimeout) {
                     if (inactivity > this.disconnectTimeout + this.warningTimeout) {
                        LOG.log(Level.FINE, "checkOpenConnections():" + conn.toString() + " exceeded total timeout.");
                        conn.processConnectionEvent(new ConnectionEvent(conn, ConnectionEvent.Type.CONNECTION_TIMEDOUT));
                     } else if (!cd.isWarned()) {
                        LOG.log(Level.FINE, "checkOpenConnections():" + conn.toString() + " exceeded warning timeout.");
                        cd.setWarned(true);
                        conn.processConnectionEvent(new ConnectionEvent(conn, ConnectionEvent.Type.CONNECTION_IDLE));
                     }
                  }
               }
            }
         }
      }
   }

   public void registerClosedConnection(Connection con) {
      if (!this.stopping) {
         if (!this.closedConnections.contains(con)) {
            LOG.log(Level.FINE, "registerClosedConnection()::" + con.toString());
            this.closedConnections.push(con);
         }
      }
   }

   public int getDisconnectTimeout() {
      return this.disconnectTimeout;
   }

   public void setDisconnectTimeout(int disconnectTimeout) {
      this.disconnectTimeout = disconnectTimeout;
   }

   public int getHousekeepingInterval() {
      return this.housekeepingInterval;
   }

   public void setHousekeepingInterval(int housekeepingInterval) {
      this.housekeepingInterval = housekeepingInterval;
   }

   public boolean isLineMode() {
      return this.lineMode;
   }

   public void setLineMode(boolean lineMode) {
      this.lineMode = lineMode;
   }

   public String getLoginShell() {
      return this.loginShell;
   }

   public void setLoginShell(String loginShell) {
      this.loginShell = loginShell;
   }

   public int getMaxConnections() {
      return this.maxConnections;
   }

   public void setMaxConnections(int maxConnections) {
      this.maxConnections = maxConnections;
   }

   public int getWarningTimeout() {
      return this.warningTimeout;
   }

   public void setWarningTimeout(int warningTimeout) {
      this.warningTimeout = warningTimeout;
   }
}
