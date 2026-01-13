package org.bouncycastle.its.operator;

import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;

public interface ITSContentVerifierProvider {
   boolean hasAssociatedCertificate();

   ITSCertificate getAssociatedCertificate();

   ContentVerifier get(int var1) throws OperatorCreationException;
}
