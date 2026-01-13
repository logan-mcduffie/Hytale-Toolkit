package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

public interface XMSSMTPrivateKey extends XMSSMTKey, PrivateKey {
   long getIndex();

   long getUsagesRemaining();

   XMSSMTPrivateKey extractKeyShard(int var1);
}
