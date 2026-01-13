package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;

public interface NTRUKey extends Key {
   NTRUParameterSpec getParameterSpec();
}
