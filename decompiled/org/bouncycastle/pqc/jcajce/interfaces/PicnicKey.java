package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.PicnicParameterSpec;

public interface PicnicKey extends Key {
   PicnicParameterSpec getParameterSpec();
}
