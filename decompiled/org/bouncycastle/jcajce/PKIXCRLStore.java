package org.bouncycastle.jcajce;

import java.security.cert.CRL;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public interface PKIXCRLStore<T extends CRL> extends Store<T> {
   @Override
   Collection<T> getMatches(Selector<T> var1) throws StoreException;
}
