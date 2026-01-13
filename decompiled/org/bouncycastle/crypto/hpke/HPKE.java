package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class HPKE {
   public static final byte mode_base = 0;
   public static final byte mode_psk = 1;
   public static final byte mode_auth = 2;
   public static final byte mode_auth_psk = 3;
   public static final short kem_P256_SHA256 = 16;
   /** @deprecated */
   public static final short kem_P384_SHA348 = 17;
   public static final short kem_P384_SHA384 = 17;
   public static final short kem_P521_SHA512 = 18;
   public static final short kem_X25519_SHA256 = 32;
   public static final short kem_X448_SHA512 = 33;
   public static final short kdf_HKDF_SHA256 = 1;
   public static final short kdf_HKDF_SHA384 = 2;
   public static final short kdf_HKDF_SHA512 = 3;
   public static final short aead_AES_GCM128 = 1;
   public static final short aead_AES_GCM256 = 2;
   public static final short aead_CHACHA20_POLY1305 = 3;
   public static final short aead_EXPORT_ONLY = -1;
   private final byte[] default_psk = null;
   private final byte[] default_psk_id = null;
   private final byte mode;
   private final short kemId;
   private final short kdfId;
   private final short aeadId;
   private final KEM kem;
   private final HKDF hkdf;
   private final int encSize;
   short Nk;

   public HPKE(byte var1, short var2, short var3, short var4) {
      this.mode = var1;
      this.kemId = var2;
      this.kdfId = var3;
      this.aeadId = var4;
      this.hkdf = new HKDF(var3);
      this.kem = new DHKEM(var2);
      if (var4 == 1) {
         this.Nk = 16;
      } else {
         this.Nk = 32;
      }

      this.encSize = this.kem.getEncryptionSize();
   }

   public HPKE(byte var1, short var2, short var3, short var4, KEM var5, int var6) {
      this.mode = var1;
      this.kemId = var2;
      this.kdfId = var3;
      this.aeadId = var4;
      this.hkdf = new HKDF(var3);
      this.kem = var5;
      if (var4 == 1) {
         this.Nk = 16;
      } else {
         this.Nk = 32;
      }

      this.encSize = var6;
   }

   public int getEncSize() {
      return this.encSize;
   }

   public short getAeadId() {
      return this.aeadId;
   }

   private void VerifyPSKInputs(byte var1, byte[] var2, byte[] var3) {
      boolean var4 = !Arrays.areEqual(var2, this.default_psk);
      boolean var5 = !Arrays.areEqual(var3, this.default_psk_id);
      if (var4 != var5) {
         throw new IllegalArgumentException("Inconsistent PSK inputs");
      } else if (var4 && var1 % 2 == 0) {
         throw new IllegalArgumentException("PSK input provided when not needed");
      } else if (!var4 && var1 % 2 == 1) {
         throw new IllegalArgumentException("Missing required PSK input");
      }
   }

   private HPKEContext keySchedule(byte var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5) {
      this.VerifyPSKInputs(var1, var4, var5);
      byte[] var6 = Arrays.concatenate(
         Strings.toByteArray("HPKE"), Pack.shortToBigEndian(this.kemId), Pack.shortToBigEndian(this.kdfId), Pack.shortToBigEndian(this.aeadId)
      );
      byte[] var7 = this.hkdf.LabeledExtract(null, var6, "psk_id_hash", var5);
      byte[] var8 = this.hkdf.LabeledExtract(null, var6, "info_hash", var3);
      byte[] var9 = new byte[]{var1};
      byte[] var10 = Arrays.concatenate(var9, var7, var8);
      byte[] var11 = this.hkdf.LabeledExtract(var2, var6, "secret", var4);
      byte[] var12 = this.hkdf.LabeledExpand(var11, var6, "key", var10, this.Nk);
      byte[] var13 = this.hkdf.LabeledExpand(var11, var6, "base_nonce", var10, 12);
      byte[] var14 = this.hkdf.LabeledExpand(var11, var6, "exp", var10, this.hkdf.getHashSize());
      return new HPKEContext(new AEAD(this.aeadId, var12, var13), this.hkdf, var14, var6);
   }

   public AsymmetricCipherKeyPair generatePrivateKey() {
      return this.kem.GeneratePrivateKey();
   }

   public byte[] serializePublicKey(AsymmetricKeyParameter var1) {
      return this.kem.SerializePublicKey(var1);
   }

   public byte[] serializePrivateKey(AsymmetricKeyParameter var1) {
      return this.kem.SerializePrivateKey(var1);
   }

   public AsymmetricKeyParameter deserializePublicKey(byte[] var1) {
      return this.kem.DeserializePublicKey(var1);
   }

   public AsymmetricCipherKeyPair deserializePrivateKey(byte[] var1, byte[] var2) {
      return this.kem.DeserializePrivateKey(var1, var2);
   }

   public AsymmetricCipherKeyPair deriveKeyPair(byte[] var1) {
      return this.kem.DeriveKeyPair(var1);
   }

   public byte[][] sendExport(AsymmetricKeyParameter var1, byte[] var2, byte[] var3, int var4, byte[] var5, byte[] var6, AsymmetricCipherKeyPair var7) {
      byte[][] var9 = new byte[2][];
      HPKEContextWithEncapsulation var8;
      switch (this.mode) {
         case 0:
            var8 = this.setupBaseS(var1, var2);
            break;
         case 1:
            var8 = this.SetupPSKS(var1, var2, var5, var6);
            break;
         case 2:
            var8 = this.setupAuthS(var1, var2, var7);
            break;
         case 3:
            var8 = this.setupAuthPSKS(var1, var2, var5, var6, var7);
            break;
         default:
            throw new IllegalStateException("Unknown mode");
      }

      var9[0] = var8.encapsulation;
      var9[1] = var8.export(var3, var4);
      return var9;
   }

   public byte[] receiveExport(
      byte[] var1, AsymmetricCipherKeyPair var2, byte[] var3, byte[] var4, int var5, byte[] var6, byte[] var7, AsymmetricKeyParameter var8
   ) {
      HPKEContext var9;
      switch (this.mode) {
         case 0:
            var9 = this.setupBaseR(var1, var2, var3);
            break;
         case 1:
            var9 = this.setupPSKR(var1, var2, var3, var6, var7);
            break;
         case 2:
            var9 = this.setupAuthR(var1, var2, var3, var8);
            break;
         case 3:
            var9 = this.setupAuthPSKR(var1, var2, var3, var6, var7, var8);
            break;
         default:
            throw new IllegalStateException("Unknown mode");
      }

      return var9.export(var4, var5);
   }

   public byte[][] seal(AsymmetricKeyParameter var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, AsymmetricCipherKeyPair var7) throws InvalidCipherTextException {
      byte[][] var9 = new byte[2][];
      HPKEContextWithEncapsulation var8;
      switch (this.mode) {
         case 0:
            var8 = this.setupBaseS(var1, var2);
            break;
         case 1:
            var8 = this.SetupPSKS(var1, var2, var5, var6);
            break;
         case 2:
            var8 = this.setupAuthS(var1, var2, var7);
            break;
         case 3:
            var8 = this.setupAuthPSKS(var1, var2, var5, var6, var7);
            break;
         default:
            throw new IllegalStateException("Unknown mode");
      }

      var9[0] = var8.seal(var3, var4);
      var9[1] = var8.getEncapsulation();
      return var9;
   }

   public byte[] open(byte[] var1, AsymmetricCipherKeyPair var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, AsymmetricKeyParameter var8) throws InvalidCipherTextException {
      HPKEContext var9;
      switch (this.mode) {
         case 0:
            var9 = this.setupBaseR(var1, var2, var3);
            break;
         case 1:
            var9 = this.setupPSKR(var1, var2, var3, var6, var7);
            break;
         case 2:
            var9 = this.setupAuthR(var1, var2, var3, var8);
            break;
         case 3:
            var9 = this.setupAuthPSKR(var1, var2, var3, var6, var7, var8);
            break;
         default:
            throw new IllegalStateException("Unknown mode");
      }

      return var9.open(var4, var5);
   }

   public HPKEContextWithEncapsulation setupBaseS(AsymmetricKeyParameter var1, byte[] var2) {
      byte[][] var3 = this.kem.Encap(var1);
      HPKEContext var4 = this.keySchedule((byte)0, var3[0], var2, this.default_psk, this.default_psk_id);
      return new HPKEContextWithEncapsulation(var4, var3[1]);
   }

   public HPKEContextWithEncapsulation setupBaseS(AsymmetricKeyParameter var1, byte[] var2, AsymmetricCipherKeyPair var3) {
      byte[][] var4 = this.kem.Encap(var1, var3);
      HPKEContext var5 = this.keySchedule((byte)0, var4[0], var2, this.default_psk, this.default_psk_id);
      return new HPKEContextWithEncapsulation(var5, var4[1]);
   }

   public HPKEContext setupBaseR(byte[] var1, AsymmetricCipherKeyPair var2, byte[] var3) {
      byte[] var4 = this.kem.Decap(var1, var2);
      return this.keySchedule((byte)0, var4, var3, this.default_psk, this.default_psk_id);
   }

   public HPKEContextWithEncapsulation SetupPSKS(AsymmetricKeyParameter var1, byte[] var2, byte[] var3, byte[] var4) {
      byte[][] var5 = this.kem.Encap(var1);
      HPKEContext var6 = this.keySchedule((byte)1, var5[0], var2, var3, var4);
      return new HPKEContextWithEncapsulation(var6, var5[1]);
   }

   public HPKEContext setupPSKR(byte[] var1, AsymmetricCipherKeyPair var2, byte[] var3, byte[] var4, byte[] var5) {
      byte[] var6 = this.kem.Decap(var1, var2);
      return this.keySchedule((byte)1, var6, var3, var4, var5);
   }

   public HPKEContextWithEncapsulation setupAuthS(AsymmetricKeyParameter var1, byte[] var2, AsymmetricCipherKeyPair var3) {
      byte[][] var4 = this.kem.AuthEncap(var1, var3);
      HPKEContext var5 = this.keySchedule((byte)2, var4[0], var2, this.default_psk, this.default_psk_id);
      return new HPKEContextWithEncapsulation(var5, var4[1]);
   }

   public HPKEContext setupAuthR(byte[] var1, AsymmetricCipherKeyPair var2, byte[] var3, AsymmetricKeyParameter var4) {
      byte[] var5 = this.kem.AuthDecap(var1, var2, var4);
      return this.keySchedule((byte)2, var5, var3, this.default_psk, this.default_psk_id);
   }

   public HPKEContextWithEncapsulation setupAuthPSKS(AsymmetricKeyParameter var1, byte[] var2, byte[] var3, byte[] var4, AsymmetricCipherKeyPair var5) {
      byte[][] var6 = this.kem.AuthEncap(var1, var5);
      HPKEContext var7 = this.keySchedule((byte)3, var6[0], var2, var3, var4);
      return new HPKEContextWithEncapsulation(var7, var6[1]);
   }

   public HPKEContext setupAuthPSKR(byte[] var1, AsymmetricCipherKeyPair var2, byte[] var3, byte[] var4, byte[] var5, AsymmetricKeyParameter var6) {
      byte[] var7 = this.kem.AuthDecap(var1, var2, var6);
      return this.keySchedule((byte)3, var7, var3, var4, var5);
   }
}
