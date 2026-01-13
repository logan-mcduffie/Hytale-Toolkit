package org.bouncycastle.crypto;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface KeyParser {
   AsymmetricKeyParameter readKey(InputStream var1) throws IOException;
}
