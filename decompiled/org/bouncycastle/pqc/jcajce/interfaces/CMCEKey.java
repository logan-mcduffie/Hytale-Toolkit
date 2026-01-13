package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.CMCEParameterSpec;

public interface CMCEKey extends Key {
   CMCEParameterSpec getParameterSpec();
}
