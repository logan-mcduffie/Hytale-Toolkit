package org.bouncycastle.crypto.threshold;

import java.io.IOException;

public interface SplitSecret {
   SecretShare[] getSecretShares();

   byte[] getSecret() throws IOException;
}
