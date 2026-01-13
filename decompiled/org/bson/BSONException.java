package org.bson;

public class BSONException extends RuntimeException {
   private static final long serialVersionUID = -4415279469780082174L;
   private Integer errorCode = null;

   public BSONException(String msg) {
      super(msg);
   }

   public BSONException(int errorCode, String msg) {
      super(msg);
      this.errorCode = errorCode;
   }

   public BSONException(String msg, Throwable t) {
      super(msg, t);
   }

   public BSONException(int errorCode, String msg, Throwable t) {
      super(msg, t);
      this.errorCode = errorCode;
   }

   public Integer getErrorCode() {
      return this.errorCode;
   }

   public boolean hasErrorCode() {
      return this.errorCode != null;
   }
}
