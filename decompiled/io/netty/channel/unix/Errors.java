package io.netty.channel.unix;

import io.netty.util.internal.EmptyArrays;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;

public final class Errors {
   public static final int ERRNO_ENOENT_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoENOENT();
   public static final int ERRNO_ENOTCONN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoENOTCONN();
   public static final int ERRNO_EBADF_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEBADF();
   public static final int ERRNO_EPIPE_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEPIPE();
   public static final int ERRNO_ECONNRESET_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoECONNRESET();
   public static final int ERRNO_EAGAIN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEAGAIN();
   public static final int ERRNO_EWOULDBLOCK_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEWOULDBLOCK();
   public static final int ERRNO_EINPROGRESS_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEINPROGRESS();
   public static final int ERROR_ECONNREFUSED_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorECONNREFUSED();
   public static final int ERROR_EISCONN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorEISCONN();
   public static final int ERROR_EALREADY_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorEALREADY();
   public static final int ERROR_ENETUNREACH_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorENETUNREACH();
   public static final int ERROR_EHOSTUNREACH_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorEHOSTUNREACH();
   private static final String[] ERRORS = new String[2048];

   public static boolean handleConnectErrno(String method, int err) throws IOException {
      if (err != ERRNO_EINPROGRESS_NEGATIVE && err != ERROR_EALREADY_NEGATIVE) {
         throw newConnectException0(method, err);
      } else {
         return false;
      }
   }

   @Deprecated
   public static void throwConnectException(String method, int err) throws IOException {
      if (err == ERROR_EALREADY_NEGATIVE) {
         throw new ConnectionPendingException();
      } else {
         throw newConnectException0(method, err);
      }
   }

   private static String errnoString(int err) {
      return err < ERRORS.length - 1 ? ERRORS[err] : ErrorsStaticallyReferencedJniMethods.strError(err);
   }

   private static IOException newConnectException0(String method, int err) {
      if (err == ERROR_ENETUNREACH_NEGATIVE || err == ERROR_EHOSTUNREACH_NEGATIVE) {
         return new NoRouteToHostException();
      } else if (err == ERROR_EISCONN_NEGATIVE) {
         throw new AlreadyConnectedException();
      } else {
         return (IOException)(err == ERRNO_ENOENT_NEGATIVE
            ? new FileNotFoundException()
            : new ConnectException(method + "(..) failed with error(" + err + "): " + errnoString(-err)));
      }
   }

   public static Errors.NativeIoException newConnectionResetException(String method, int errnoNegative) {
      Errors.NativeIoException exception = new Errors.NativeIoException(method, errnoNegative, false);
      exception.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
      return exception;
   }

   public static Errors.NativeIoException newIOException(String method, int err) {
      return new Errors.NativeIoException(method, err);
   }

   @Deprecated
   public static int ioResult(String method, int err, Errors.NativeIoException resetCause, ClosedChannelException closedCause) throws IOException {
      if (err == ERRNO_EAGAIN_NEGATIVE || err == ERRNO_EWOULDBLOCK_NEGATIVE) {
         return 0;
      } else if (err == resetCause.expectedErr()) {
         throw resetCause;
      } else if (err == ERRNO_EBADF_NEGATIVE) {
         throw closedCause;
      } else if (err == ERRNO_ENOTCONN_NEGATIVE) {
         throw new NotYetConnectedException();
      } else if (err == ERRNO_ENOENT_NEGATIVE) {
         throw new FileNotFoundException();
      } else {
         throw newIOException(method, err);
      }
   }

   public static int ioResult(String method, int err) throws IOException {
      if (err == ERRNO_EAGAIN_NEGATIVE || err == ERRNO_EWOULDBLOCK_NEGATIVE) {
         return 0;
      } else if (err == ERRNO_EBADF_NEGATIVE) {
         throw new ClosedChannelException();
      } else if (err == ERRNO_ENOTCONN_NEGATIVE) {
         throw new NotYetConnectedException();
      } else if (err == ERRNO_ENOENT_NEGATIVE) {
         throw new FileNotFoundException();
      } else {
         throw new Errors.NativeIoException(method, err, false);
      }
   }

   private Errors() {
   }

   static {
      for (int i = 0; i < ERRORS.length; i++) {
         ERRORS[i] = ErrorsStaticallyReferencedJniMethods.strError(i);
      }
   }

   static final class NativeConnectException extends ConnectException {
      private static final long serialVersionUID = -5532328671712318161L;
      private final int expectedErr;

      NativeConnectException(String method, int expectedErr) {
         super(method + "(..) failed with error(" + expectedErr + "):" + Errors.errnoString(-expectedErr));
         this.expectedErr = expectedErr;
      }

      int expectedErr() {
         return this.expectedErr;
      }
   }

   public static final class NativeIoException extends IOException {
      private static final long serialVersionUID = 8222160204268655526L;
      private final int expectedErr;
      private final boolean fillInStackTrace;

      public NativeIoException(String method, int expectedErr) {
         this(method, expectedErr, true);
      }

      public NativeIoException(String method, int expectedErr, boolean fillInStackTrace) {
         super(method + "(..) failed with error(" + expectedErr + "): " + Errors.errnoString(-expectedErr));
         this.expectedErr = expectedErr;
         this.fillInStackTrace = fillInStackTrace;
      }

      public int expectedErr() {
         return this.expectedErr;
      }

      @Override
      public synchronized Throwable fillInStackTrace() {
         return (Throwable)(this.fillInStackTrace ? super.fillInStackTrace() : this);
      }
   }
}
