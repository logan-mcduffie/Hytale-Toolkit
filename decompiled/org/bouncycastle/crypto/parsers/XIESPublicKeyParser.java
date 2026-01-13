package org.bouncycastle.crypto.parsers;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.util.io.Streams;

public class XIESPublicKeyParser implements KeyParser {
   private final boolean isX25519;

   public XIESPublicKeyParser(boolean var1) {
      this.isX25519 = var1;
   }

   @Override
   public AsymmetricKeyParameter readKey(InputStream var1) throws IOException {
      int var2 = this.isX25519 ? 32 : 56;
      byte[] var3 = new byte[var2];
      Streams.readFully(var1, var3, 0, var3.length);
      return (AsymmetricKeyParameter)(this.isX25519 ? new X25519PublicKeyParameters(var3, 0) : new X448PublicKeyParameters(var3, 0));
   }
}
