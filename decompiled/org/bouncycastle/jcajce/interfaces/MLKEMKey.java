package org.bouncycastle.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;

public interface MLKEMKey extends Key {
   MLKEMParameterSpec getParameterSpec();
}
