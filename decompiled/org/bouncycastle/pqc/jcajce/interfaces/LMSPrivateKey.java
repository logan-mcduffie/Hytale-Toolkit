package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

public interface LMSPrivateKey extends LMSKey, PrivateKey {
   long getIndex();

   long getUsagesRemaining();

   LMSPrivateKey extractKeyShard(int var1);
}
