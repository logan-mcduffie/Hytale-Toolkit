package org.jline.builtins.telnet;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelnetIO {
   protected static final int IAC = 255;
   protected static final int GA = 249;
   protected static final int WILL = 251;
   protected static final int WONT = 252;
   protected static final int DO = 253;
   protected static final int DONT = 254;
   protected static final int SB = 250;
   protected static final int SE = 240;
   protected static final int NOP = 241;
   protected static final int DM = 242;
   protected static final int BRK = 243;
   protected static final int IP = 244;
   protected static final int AO = 245;
   protected static final int AYT = 246;
   protected static final int EC = 247;
   protected static final int EL = 248;
   protected static final int ECHO = 1;
   protected static final int SUPGA = 3;
   protected static final int NAWS = 31;
   protected static final int TTYPE = 24;
   protected static final int IS = 0;
   protected static final int SEND = 1;
   protected static final int LOGOUT = 18;
   protected static final int LINEMODE = 34;
   protected static final int LM_MODE = 1;
   protected static final int LM_EDIT = 1;
   protected static final int LM_TRAPSIG = 2;
   protected static final int LM_MODEACK = 4;
   protected static final int LM_FORWARDMASK = 2;
   protected static final int LM_SLC = 3;
   protected static final int LM_SLC_NOSUPPORT = 0;
   protected static final int LM_SLC_DEFAULT = 3;
   protected static final int LM_SLC_VALUE = 2;
   protected static final int LM_SLC_CANTCHANGE = 1;
   protected static final int LM_SLC_LEVELBITS = 3;
   protected static final int LM_SLC_ACK = 128;
   protected static final int LM_SLC_FLUSHIN = 64;
   protected static final int LM_SLC_FLUSHOUT = 32;
   protected static final int LM_SLC_SYNCH = 1;
   protected static final int LM_SLC_BRK = 2;
   protected static final int LM_SLC_IP = 3;
   protected static final int LM_SLC_AO = 4;
   protected static final int LM_SLC_AYT = 5;
   protected static final int LM_SLC_EOR = 6;
   protected static final int LM_SLC_ABORT = 7;
   protected static final int LM_SLC_EOF = 8;
   protected static final int LM_SLC_SUSP = 9;
   protected static final int NEWENV = 39;
   protected static final int NE_INFO = 2;
   protected static final int NE_VAR = 0;
   protected static final int NE_VALUE = 1;
   protected static final int NE_ESC = 2;
   protected static final int NE_USERVAR = 3;
   protected static final int NE_VAR_OK = 2;
   protected static final int NE_VAR_DEFINED = 1;
   protected static final int NE_VAR_DEFINED_EMPTY = 0;
   protected static final int NE_VAR_UNDEFINED = -1;
   protected static final int NE_IN_ERROR = -2;
   protected static final int NE_IN_END = -3;
   protected static final int NE_VAR_NAME_MAXLENGTH = 50;
   protected static final int NE_VAR_VALUE_MAXLENGTH = 1000;
   protected static final int EXT_ASCII = 17;
   protected static final int SEND_LOC = 23;
   protected static final int AUTHENTICATION = 37;
   protected static final int ENCRYPT = 38;
   private static final Logger LOG = Logger.getLogger(TelnetIO.class.getName());
   private static final int SMALLEST_BELIEVABLE_WIDTH = 20;
   private static final int SMALLEST_BELIEVABLE_HEIGHT = 6;
   private static final int DEFAULT_WIDTH = 80;
   private static final int DEFAULT_HEIGHT = 25;
   private Connection connection;
   private ConnectionData connectionData;
   private DataOutputStream out;
   private DataInputStream in;
   private TelnetIO.IACHandler iacHandler;
   private InetAddress localAddress;
   private boolean noIac = false;
   private boolean initializing;
   private boolean crFlag;

   public void initIO() throws IOException {
      this.iacHandler = new TelnetIO.IACHandler();
      this.in = new DataInputStream(this.connectionData.getSocket().getInputStream());
      this.out = new DataOutputStream(new BufferedOutputStream(this.connectionData.getSocket().getOutputStream()));
      this.localAddress = this.connectionData.getSocket().getLocalAddress();
      this.crFlag = false;
      this.initTelnetCommunication();
   }

   public void setConnection(Connection con) {
      this.connection = con;
      this.connectionData = this.connection.getConnectionData();
   }

   public void write(byte b) throws IOException {
      if (!this.crFlag && b == 10) {
         this.out.write(13);
      }

      this.out.write(b);
      if (b == 13) {
         this.crFlag = true;
      } else {
         this.crFlag = false;
      }
   }

   public void write(int i) throws IOException {
      this.write((byte)i);
   }

   public void write(byte[] sequence) throws IOException {
      for (byte b : sequence) {
         this.write(b);
      }
   }

   public void write(int[] sequence) throws IOException {
      for (int i : sequence) {
         this.write((byte)i);
      }
   }

   public void write(char ch) throws IOException {
      this.write((byte)ch);
   }

   public void write(String str) throws IOException {
      this.write(str.getBytes());
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void closeOutput() {
      try {
         this.write(255);
         this.write(253);
         this.write(18);
         this.out.close();
      } catch (IOException var2) {
         LOG.log(Level.SEVERE, "closeOutput()", (Throwable)var2);
      }
   }

   private void rawWrite(int i) throws IOException {
      this.out.write(i);
   }

   public int read() throws IOException {
      int c = this.rawread();
      this.noIac = false;

      while (c == 255 && !this.noIac) {
         c = this.rawread();
         if (c != 255) {
            this.iacHandler.handleC(c);
            c = this.rawread();
         } else {
            this.noIac = true;
         }
      }

      return this.stripCRSeq(c);
   }

   public void closeInput() {
      try {
         this.in.close();
      } catch (IOException var2) {
      }
   }

   private int read16int() throws IOException {
      return this.in.readUnsignedShort();
   }

   private int rawread() throws IOException {
      int b = 0;
      b = this.in.readUnsignedByte();
      this.connectionData.activity();
      return b;
   }

   private int stripCRSeq(int input) throws IOException {
      if (input == 13) {
         this.rawread();
         return 10;
      } else {
         return input;
      }
   }

   private void initTelnetCommunication() {
      this.initializing = true;

      try {
         if (this.connectionData.isLineMode()) {
            this.iacHandler.doLineModeInit();
            LOG.log(Level.FINE, "Line mode initialized.");
         } else {
            this.iacHandler.doCharacterModeInit();
            LOG.log(Level.FINE, "Character mode initialized.");
         }

         this.connectionData.getSocket().setSoTimeout(1000);
         this.read();
      } catch (Exception var10) {
      } finally {
         try {
            this.connectionData.getSocket().setSoTimeout(0);
         } catch (Exception var9) {
            LOG.log(Level.SEVERE, "initTelnetCommunication()", (Throwable)var9);
         }
      }

      this.initializing = false;
   }

   private void IamHere() {
      try {
         this.write("[" + this.localAddress.toString() + ":Yes]");
         this.flush();
      } catch (Exception var2) {
         LOG.log(Level.SEVERE, "IamHere()", (Throwable)var2);
      }
   }

   private void nvtBreak() {
      this.connection.processConnectionEvent(new ConnectionEvent(this.connection, ConnectionEvent.Type.CONNECTION_BREAK));
   }

   private void setTerminalGeometry(int width, int height) {
      if (width < 20) {
         width = 80;
      }

      if (height < 6) {
         height = 25;
      }

      this.connectionData.setTerminalGeometry(width, height);
      this.connection.processConnectionEvent(new ConnectionEvent(this.connection, ConnectionEvent.Type.CONNECTION_TERMINAL_GEOMETRY_CHANGED));
   }

   public void setEcho(boolean b) {
   }

   class IACHandler {
      private int[] buffer = new int[2];
      private boolean DO_ECHO = false;
      private boolean DO_SUPGA = false;
      private boolean DO_NAWS = false;
      private boolean DO_TTYPE = false;
      private boolean DO_LINEMODE = false;
      private boolean DO_NEWENV = false;
      private boolean WAIT_DO_REPLY_SUPGA = false;
      private boolean WAIT_DO_REPLY_ECHO = false;
      private boolean WAIT_DO_REPLY_NAWS = false;
      private boolean WAIT_DO_REPLY_TTYPE = false;
      private boolean WAIT_DO_REPLY_LINEMODE = false;
      private boolean WAIT_LM_MODE_ACK = false;
      private boolean WAIT_LM_DO_REPLY_FORWARDMASK = false;
      private boolean WAIT_DO_REPLY_NEWENV = false;
      private boolean WAIT_NE_SEND_REPLY = false;
      private boolean WAIT_WILL_REPLY_SUPGA = false;
      private boolean WAIT_WILL_REPLY_ECHO = false;
      private boolean WAIT_WILL_REPLY_NAWS = false;
      private boolean WAIT_WILL_REPLY_TTYPE = false;

      public void doCharacterModeInit() throws IOException {
         this.sendCommand(251, 1, true);
         this.sendCommand(254, 1, true);
         this.sendCommand(253, 31, true);
         this.sendCommand(251, 3, true);
         this.sendCommand(253, 3, true);
         this.sendCommand(253, 24, true);
         this.sendCommand(253, 39, true);
      }

      public void doLineModeInit() throws IOException {
         this.sendCommand(253, 31, true);
         this.sendCommand(251, 3, true);
         this.sendCommand(253, 3, true);
         this.sendCommand(253, 24, true);
         this.sendCommand(253, 34, true);
         this.sendCommand(253, 39, true);
      }

      public void handleC(int i) throws IOException {
         this.buffer[0] = i;
         if (!this.parseTWO(this.buffer)) {
            this.buffer[1] = TelnetIO.this.rawread();
            this.parse(this.buffer);
         }

         this.buffer[0] = 0;
         this.buffer[1] = 0;
      }

      private boolean parseTWO(int[] buf) {
         switch (buf[0]) {
            case 241:
            case 244:
            case 245:
            case 247:
            case 248:
            case 255:
               break;
            case 242:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            default:
               return false;
            case 243:
               TelnetIO.this.nvtBreak();
               break;
            case 246:
               TelnetIO.this.IamHere();
         }

         return true;
      }

      private void parse(int[] buf) throws IOException {
         switch (buf[0]) {
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
            case 249:
            default:
               break;
            case 250:
               if (this.supported(buf[1]) && this.isEnabled(buf[1])) {
                  switch (buf[1]) {
                     case 24:
                        this.handleTTYPE();
                        break;
                     case 31:
                        this.handleNAWS();
                        break;
                     case 34:
                        this.handleLINEMODE();
                        break;
                     case 39:
                        this.handleNEWENV();
                  }
               }
               break;
            case 251:
               if (!this.supported(buf[1]) || !this.isEnabled(buf[1])) {
                  if (this.waitDOreply(buf[1]) && this.supported(buf[1])) {
                     this.enable(buf[1]);
                     this.setWait(253, buf[1], false);
                  } else if (this.supported(buf[1])) {
                     this.sendCommand(253, buf[1], false);
                     this.enable(buf[1]);
                  } else {
                     this.sendCommand(254, buf[1], false);
                  }
               }
               break;
            case 252:
               if (this.waitDOreply(buf[1]) && this.supported(buf[1])) {
                  this.setWait(253, buf[1], false);
               } else if (this.supported(buf[1]) && this.isEnabled(buf[1])) {
                  this.enable(buf[1]);
               }
               break;
            case 253:
               if (!this.supported(buf[1]) || !this.isEnabled(buf[1])) {
                  if (this.waitWILLreply(buf[1]) && this.supported(buf[1])) {
                     this.enable(buf[1]);
                     this.setWait(251, buf[1], false);
                  } else if (this.supported(buf[1])) {
                     this.sendCommand(251, buf[1], false);
                     this.enable(buf[1]);
                  } else {
                     this.sendCommand(252, buf[1], false);
                  }
               }
               break;
            case 254:
               if (this.waitWILLreply(buf[1]) && this.supported(buf[1])) {
                  this.setWait(251, buf[1], false);
               } else if (this.supported(buf[1]) && this.isEnabled(buf[1])) {
                  this.enable(buf[1]);
               }
         }
      }

      private void handleNAWS() throws IOException {
         int width = TelnetIO.this.read16int();
         if (width == 255) {
            width = TelnetIO.this.read16int();
         }

         int height = TelnetIO.this.read16int();
         if (height == 255) {
            height = TelnetIO.this.read16int();
         }

         this.skipToSE();
         TelnetIO.this.setTerminalGeometry(width, height);
      }

      private void handleTTYPE() throws IOException {
         String tmpstr = "";
         TelnetIO.this.rawread();
         tmpstr = this.readIACSETerminatedString(40);
         TelnetIO.LOG.log(Level.FINE, "Reported terminal name " + tmpstr);
         TelnetIO.this.connectionData.setNegotiatedTerminalType(tmpstr);
      }

      public void handleLINEMODE() throws IOException {
         int c = TelnetIO.this.rawread();
         switch (c) {
            case 1:
               this.handleLMMode();
               break;
            case 3:
               this.handleLMSLC();
               break;
            case 251:
            case 252:
               this.handleLMForwardMask(c);
               break;
            default:
               this.skipToSE();
         }
      }

      public void handleLMMode() throws IOException {
         if (this.WAIT_LM_MODE_ACK) {
            int mask = TelnetIO.this.rawread();
            if (mask != 7) {
               TelnetIO.LOG.log(Level.FINE, "Client violates linemodeack sent: " + mask);
            }

            this.WAIT_LM_MODE_ACK = false;
         }

         this.skipToSE();
      }

      public void handleLMSLC() throws IOException {
         int[] triple = new int[3];
         if (this.readTriple(triple)) {
            if (triple[0] == 0 && triple[1] == 3 && triple[2] == 0) {
               this.skipToSE();
               TelnetIO.this.rawWrite(255);
               TelnetIO.this.rawWrite(250);
               TelnetIO.this.rawWrite(34);
               TelnetIO.this.rawWrite(3);

               for (int i = 1; i < 12; i++) {
                  TelnetIO.this.rawWrite(i);
                  TelnetIO.this.rawWrite(3);
                  TelnetIO.this.rawWrite(0);
               }

               TelnetIO.this.rawWrite(255);
               TelnetIO.this.rawWrite(240);
               TelnetIO.this.flush();
            } else {
               TelnetIO.this.rawWrite(255);
               TelnetIO.this.rawWrite(250);
               TelnetIO.this.rawWrite(34);
               TelnetIO.this.rawWrite(3);
               TelnetIO.this.rawWrite(triple[0]);
               TelnetIO.this.rawWrite(triple[1] | 128);
               TelnetIO.this.rawWrite(triple[2]);

               while (this.readTriple(triple)) {
                  TelnetIO.this.rawWrite(triple[0]);
                  TelnetIO.this.rawWrite(triple[1] | 128);
                  TelnetIO.this.rawWrite(triple[2]);
               }

               TelnetIO.this.rawWrite(255);
               TelnetIO.this.rawWrite(240);
               TelnetIO.this.flush();
            }
         }
      }

      public void handleLMForwardMask(int WHAT) throws IOException {
         switch (WHAT) {
            case 252:
               if (this.WAIT_LM_DO_REPLY_FORWARDMASK) {
                  this.WAIT_LM_DO_REPLY_FORWARDMASK = false;
               }
            default:
               this.skipToSE();
         }
      }

      public void handleNEWENV() throws IOException {
         TelnetIO.LOG.log(Level.FINE, "handleNEWENV()");
         int c = TelnetIO.this.rawread();
         switch (c) {
            case 0:
               this.handleNEIs();
               break;
            case 2:
               this.handleNEInfo();
               break;
            default:
               this.skipToSE();
         }
      }

      private int readNEVariableName(StringBuffer sbuf) throws IOException {
         TelnetIO.LOG.log(Level.FINE, "readNEVariableName()");
         int i = -1;

         while (true) {
            i = TelnetIO.this.rawread();
            if (i == -1) {
               return -2;
            }

            if (i == 255) {
               i = TelnetIO.this.rawread();
               if (i != 255) {
                  if (i == 240) {
                     return -3;
                  }

                  return -2;
               }

               sbuf.append((char)i);
            } else if (i != 2) {
               if (i == 0 || i == 3) {
                  return -1;
               }

               if (i == 1) {
                  return 1;
               }

               if (sbuf.length() >= 50) {
                  return -2;
               }

               sbuf.append((char)i);
            } else {
               i = TelnetIO.this.rawread();
               if (i != 2 && i != 0 && i != 3 && i != 1) {
                  return -2;
               }

               sbuf.append((char)i);
            }
         }
      }

      private int readNEVariableValue(StringBuffer sbuf) throws IOException {
         TelnetIO.LOG.log(Level.FINE, "readNEVariableValue()");
         int i = TelnetIO.this.rawread();
         if (i == -1) {
            return -2;
         } else if (i == 255) {
            i = TelnetIO.this.rawread();
            if (i == 255) {
               return 0;
            } else {
               return i == 240 ? -3 : -2;
            }
         } else if (i != 0 && i != 3) {
            if (i == 2) {
               i = TelnetIO.this.rawread();
               if (i != 2 && i != 0 && i != 3 && i != 1) {
                  return -2;
               }

               sbuf.append((char)i);
            } else {
               sbuf.append((char)i);
            }

            while (true) {
               i = TelnetIO.this.rawread();
               if (i == -1) {
                  return -2;
               }

               if (i == 255) {
                  i = TelnetIO.this.rawread();
                  if (i != 255) {
                     if (i == 240) {
                        return -3;
                     }

                     return -2;
                  }

                  sbuf.append((char)i);
               } else if (i != 2) {
                  if (i == 0 || i == 3) {
                     return 2;
                  }

                  if (sbuf.length() > 1000) {
                     return -2;
                  }

                  sbuf.append((char)i);
               } else {
                  i = TelnetIO.this.rawread();
                  if (i != 2 && i != 0 && i != 3 && i != 1) {
                     return -2;
                  }

                  sbuf.append((char)i);
               }
            }
         } else {
            return 0;
         }
      }

      public void readNEVariables() throws IOException {
         TelnetIO.LOG.log(Level.FINE, "readNEVariables()");
         StringBuffer sbuf = new StringBuffer(50);
         int i = TelnetIO.this.rawread();
         if (i == 255) {
            this.skipToSE();
            TelnetIO.LOG.log(Level.FINE, "readNEVariables()::INVALID VARIABLE");
         } else {
            boolean cont = true;
            if (i == 0 || i == 3) {
               do {
                  switch (this.readNEVariableName(sbuf)) {
                     case -3:
                        TelnetIO.LOG.log(Level.FINE, "readNEVariables()::NE_IN_END");
                        return;
                     case -2:
                        TelnetIO.LOG.log(Level.FINE, "readNEVariables()::NE_IN_ERROR");
                        return;
                     case -1:
                        TelnetIO.LOG.log(Level.FINE, "readNEVariables()::NE_VAR_UNDEFINED");
                     case 0:
                     default:
                        break;
                     case 1:
                        TelnetIO.LOG.log(Level.FINE, "readNEVariables()::NE_VAR_DEFINED");
                        String str = sbuf.toString();
                        sbuf.delete(0, sbuf.length());
                        switch (this.readNEVariableValue(sbuf)) {
                           case -3:
                              TelnetIO.LOG.log(Level.FINE, "readNEVariables()::NE_IN_END");
                              return;
                           case -2:
                              TelnetIO.LOG.log(Level.FINE, "readNEVariables()::NE_IN_ERROR");
                              return;
                           case -1:
                           case 1:
                           default:
                              break;
                           case 0:
                              TelnetIO.LOG.log(Level.FINE, "readNEVariables()::NE_VAR_DEFINED_EMPTY");
                              break;
                           case 2:
                              TelnetIO.LOG.log(Level.FINE, "readNEVariables()::NE_VAR_OK:VAR=" + str + " VAL=" + sbuf.toString());
                              TelnetIO.this.connectionData.getEnvironment().put(str, sbuf.toString());
                              sbuf.delete(0, sbuf.length());
                        }
                  }
               } while (cont);
            }
         }
      }

      public void handleNEIs() throws IOException {
         TelnetIO.LOG.log(Level.FINE, "handleNEIs()");
         if (this.isEnabled(39)) {
            this.readNEVariables();
         }
      }

      public void handleNEInfo() throws IOException {
         TelnetIO.LOG.log(Level.FINE, "handleNEInfo()");
         if (this.isEnabled(39)) {
            this.readNEVariables();
         }
      }

      public void getTTYPE() throws IOException {
         if (this.isEnabled(24)) {
            TelnetIO.this.rawWrite(255);
            TelnetIO.this.rawWrite(250);
            TelnetIO.this.rawWrite(24);
            TelnetIO.this.rawWrite(1);
            TelnetIO.this.rawWrite(255);
            TelnetIO.this.rawWrite(240);
            TelnetIO.this.flush();
         }
      }

      public void negotiateLineMode() throws IOException {
         if (this.isEnabled(34)) {
            TelnetIO.this.rawWrite(255);
            TelnetIO.this.rawWrite(250);
            TelnetIO.this.rawWrite(34);
            TelnetIO.this.rawWrite(1);
            TelnetIO.this.rawWrite(3);
            TelnetIO.this.rawWrite(255);
            TelnetIO.this.rawWrite(240);
            this.WAIT_LM_MODE_ACK = true;
            TelnetIO.this.rawWrite(255);
            TelnetIO.this.rawWrite(250);
            TelnetIO.this.rawWrite(34);
            TelnetIO.this.rawWrite(254);
            TelnetIO.this.rawWrite(2);
            TelnetIO.this.rawWrite(255);
            TelnetIO.this.rawWrite(240);
            this.WAIT_LM_DO_REPLY_FORWARDMASK = true;
            TelnetIO.this.flush();
         }
      }

      private void negotiateEnvironment() throws IOException {
         if (this.isEnabled(39)) {
            TelnetIO.this.rawWrite(255);
            TelnetIO.this.rawWrite(250);
            TelnetIO.this.rawWrite(39);
            TelnetIO.this.rawWrite(1);
            TelnetIO.this.rawWrite(0);
            TelnetIO.this.rawWrite(3);
            TelnetIO.this.rawWrite(255);
            TelnetIO.this.rawWrite(240);
            this.WAIT_NE_SEND_REPLY = true;
            TelnetIO.this.flush();
         }
      }

      private void skipToSE() throws IOException {
         while (TelnetIO.this.rawread() != 240) {
         }
      }

      private boolean readTriple(int[] triple) throws IOException {
         triple[0] = TelnetIO.this.rawread();
         triple[1] = TelnetIO.this.rawread();
         if (triple[0] == 255 && triple[1] == 240) {
            return false;
         } else {
            triple[2] = TelnetIO.this.rawread();
            return true;
         }
      }

      private String readIACSETerminatedString(int maxlength) throws IOException {
         int where = 0;
         char[] cbuf = new char[maxlength];
         char b = ' ';
         boolean cont = true;

         do {
            int i = TelnetIO.this.rawread();
            switch (i) {
               case -1:
                  return new String("default");
               case 255:
                  i = TelnetIO.this.rawread();
                  if (i == 240) {
                     cont = false;
                  }
            }

            if (cont) {
               b = (char)i;
               if (b != '\n' && b != '\r' && where != maxlength) {
                  cbuf[where++] = b;
               } else {
                  cont = false;
               }
            }
         } while (cont);

         return new String(cbuf, 0, where);
      }

      private boolean supported(int i) {
         switch (i) {
            case 1:
            case 3:
            case 24:
            case 31:
            case 39:
               return true;
            case 34:
               return TelnetIO.this.connectionData.isLineMode();
            default:
               return false;
         }
      }

      private void sendCommand(int i, int j, boolean westarted) throws IOException {
         TelnetIO.this.rawWrite(255);
         TelnetIO.this.rawWrite(i);
         TelnetIO.this.rawWrite(j);
         if (i == 253 && westarted) {
            this.setWait(253, j, true);
         }

         if (i == 251 && westarted) {
            this.setWait(251, j, true);
         }

         TelnetIO.this.flush();
      }

      private void enable(int i) throws IOException {
         switch (i) {
            case 1:
               if (this.DO_ECHO) {
                  this.DO_ECHO = false;
               } else {
                  this.DO_ECHO = true;
               }
               break;
            case 3:
               if (this.DO_SUPGA) {
                  this.DO_SUPGA = false;
               } else {
                  this.DO_SUPGA = true;
               }
               break;
            case 24:
               if (this.DO_TTYPE) {
                  this.DO_TTYPE = false;
               } else {
                  this.DO_TTYPE = true;
                  this.getTTYPE();
               }
               break;
            case 31:
               if (this.DO_NAWS) {
                  this.DO_NAWS = false;
               } else {
                  this.DO_NAWS = true;
               }
               break;
            case 34:
               if (this.DO_LINEMODE) {
                  this.DO_LINEMODE = false;
                  TelnetIO.this.connectionData.setLineMode(false);
               } else {
                  this.DO_LINEMODE = true;
                  this.negotiateLineMode();
               }
               break;
            case 39:
               if (this.DO_NEWENV) {
                  this.DO_NEWENV = false;
               } else {
                  this.DO_NEWENV = true;
                  this.negotiateEnvironment();
               }
         }
      }

      private boolean isEnabled(int i) {
         switch (i) {
            case 1:
               return this.DO_ECHO;
            case 3:
               return this.DO_SUPGA;
            case 24:
               return this.DO_TTYPE;
            case 31:
               return this.DO_NAWS;
            case 34:
               return this.DO_LINEMODE;
            case 39:
               return this.DO_NEWENV;
            default:
               return false;
         }
      }

      private boolean waitWILLreply(int i) {
         switch (i) {
            case 1:
               return this.WAIT_WILL_REPLY_ECHO;
            case 3:
               return this.WAIT_WILL_REPLY_SUPGA;
            case 24:
               return this.WAIT_WILL_REPLY_TTYPE;
            case 31:
               return this.WAIT_WILL_REPLY_NAWS;
            default:
               return false;
         }
      }

      private boolean waitDOreply(int i) {
         switch (i) {
            case 1:
               return this.WAIT_DO_REPLY_ECHO;
            case 3:
               return this.WAIT_DO_REPLY_SUPGA;
            case 24:
               return this.WAIT_DO_REPLY_TTYPE;
            case 31:
               return this.WAIT_DO_REPLY_NAWS;
            case 34:
               return this.WAIT_DO_REPLY_LINEMODE;
            case 39:
               return this.WAIT_DO_REPLY_NEWENV;
            default:
               return false;
         }
      }

      private void setWait(int WHAT, int OPTION, boolean WAIT) {
         switch (WHAT) {
            case 251:
               switch (OPTION) {
                  case 1:
                     this.WAIT_WILL_REPLY_ECHO = WAIT;
                     return;
                  case 3:
                     this.WAIT_WILL_REPLY_SUPGA = WAIT;
                     return;
                  case 24:
                     this.WAIT_WILL_REPLY_TTYPE = WAIT;
                     return;
                  case 31:
                     this.WAIT_WILL_REPLY_NAWS = WAIT;
                     return;
                  default:
                     return;
               }
            case 253:
               switch (OPTION) {
                  case 1:
                     this.WAIT_DO_REPLY_ECHO = WAIT;
                     break;
                  case 3:
                     this.WAIT_DO_REPLY_SUPGA = WAIT;
                     break;
                  case 24:
                     this.WAIT_DO_REPLY_TTYPE = WAIT;
                     break;
                  case 31:
                     this.WAIT_DO_REPLY_NAWS = WAIT;
                     break;
                  case 34:
                     this.WAIT_DO_REPLY_LINEMODE = WAIT;
                     break;
                  case 39:
                     this.WAIT_DO_REPLY_NEWENV = WAIT;
               }
         }
      }
   }
}
