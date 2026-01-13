package org.bouncycastle.jcajce;

import java.security.cert.Certificate;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public interface PKIXCertStore<T extends Certificate> extends Store<T> {
   @Override
   Collection<T> getMatches(Selector<T> var1) throws StoreException;
}
