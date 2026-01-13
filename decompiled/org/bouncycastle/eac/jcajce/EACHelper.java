package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

interface EACHelper {
   KeyFactory createKeyFactory(String var1) throws NoSuchProviderException, NoSuchAlgorithmException;
}
