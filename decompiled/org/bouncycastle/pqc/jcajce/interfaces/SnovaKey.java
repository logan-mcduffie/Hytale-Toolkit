package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.SnovaParameterSpec;

public interface SnovaKey extends Key {
   SnovaParameterSpec getParameterSpec();
}
