package org.bouncycastle.est;

import java.io.IOException;

public interface ESTClient {
   ESTResponse doRequest(ESTRequest var1) throws IOException;
}
