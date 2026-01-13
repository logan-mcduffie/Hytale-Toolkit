package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.HQCParameterSpec;

public interface HQCKey extends Key {
   HQCParameterSpec getParameterSpec();
}
