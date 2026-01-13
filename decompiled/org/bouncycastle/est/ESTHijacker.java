package org.bouncycastle.est;

import java.io.IOException;

public interface ESTHijacker {
   ESTResponse hijack(ESTRequest var1, Source var2) throws IOException;
}
