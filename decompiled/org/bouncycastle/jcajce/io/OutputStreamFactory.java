package org.bouncycastle.jcajce.io;

import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.Signature;
import javax.crypto.Mac;

public class OutputStreamFactory {
   public static OutputStream createStream(Signature var0) {
      return new SignatureUpdatingOutputStream(var0);
   }

   public static OutputStream createStream(MessageDigest var0) {
      return new DigestUpdatingOutputStream(var0);
   }

   public static OutputStream createStream(Mac var0) {
      return new MacUpdatingOutputStream(var0);
   }
}
