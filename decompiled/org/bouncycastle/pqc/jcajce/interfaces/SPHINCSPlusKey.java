package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec;

public interface SPHINCSPlusKey extends Key {
   SPHINCSPlusParameterSpec getParameterSpec();
}
