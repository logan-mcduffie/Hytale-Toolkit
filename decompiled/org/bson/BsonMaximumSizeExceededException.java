package org.bson;

public class BsonMaximumSizeExceededException extends BsonSerializationException {
   private static final long serialVersionUID = 8725368828269129777L;

   public BsonMaximumSizeExceededException(String message) {
      super(message);
   }
}
