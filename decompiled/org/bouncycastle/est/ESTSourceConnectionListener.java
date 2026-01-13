package org.bouncycastle.est;

import java.io.IOException;

public interface ESTSourceConnectionListener<T, I> {
   ESTRequest onConnection(Source<T> var1, ESTRequest var2) throws IOException;
}
