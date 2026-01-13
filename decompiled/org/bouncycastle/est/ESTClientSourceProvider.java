package org.bouncycastle.est;

import java.io.IOException;

public interface ESTClientSourceProvider {
   Source makeSource(String var1, int var2) throws IOException;
}
