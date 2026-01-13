package org.bouncycastle.est.jcajce;

import java.net.Socket;

public interface ChannelBindingProvider {
   boolean canAccessChannelBinding(Socket var1);

   byte[] getChannelBinding(Socket var1, String var2);
}
