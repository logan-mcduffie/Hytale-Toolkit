package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public abstract class KEM {
   abstract AsymmetricCipherKeyPair GeneratePrivateKey();

   abstract AsymmetricCipherKeyPair DeriveKeyPair(byte[] var1);

   abstract byte[][] Encap(AsymmetricKeyParameter var1);

   abstract byte[][] Encap(AsymmetricKeyParameter var1, AsymmetricCipherKeyPair var2);

   abstract byte[][] AuthEncap(AsymmetricKeyParameter var1, AsymmetricCipherKeyPair var2);

   abstract byte[] Decap(byte[] var1, AsymmetricCipherKeyPair var2);

   abstract byte[] AuthDecap(byte[] var1, AsymmetricCipherKeyPair var2, AsymmetricKeyParameter var3);

   abstract byte[] SerializePublicKey(AsymmetricKeyParameter var1);

   abstract byte[] SerializePrivateKey(AsymmetricKeyParameter var1);

   abstract AsymmetricKeyParameter DeserializePublicKey(byte[] var1);

   abstract AsymmetricCipherKeyPair DeserializePrivateKey(byte[] var1, byte[] var2);

   abstract int getEncryptionSize();
}
