package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

public interface XMSSPrivateKey extends XMSSKey, PrivateKey {
   long getIndex();

   long getUsagesRemaining();

   XMSSPrivateKey extractKeyShard(int var1);
}
