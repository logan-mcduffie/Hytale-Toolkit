package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.util.Exceptions;

public abstract class BaseDeterministicOrRandomSignature extends SignatureSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private final AlgorithmParameterSpec originalSpec;
   protected AlgorithmParameters engineParams;
   protected ContextParameterSpec paramSpec;
   protected AsymmetricKeyParameter keyParams;
   protected boolean isInitState = true;

   protected BaseDeterministicOrRandomSignature(String var1) {
      this.originalSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
   }

   @Override
   protected final void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      this.verifyInit(var1);
      this.paramSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
      this.isInitState = true;
      this.reInit();
   }

   protected abstract void verifyInit(PublicKey var1) throws InvalidKeyException;

   @Override
   protected final void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      this.signInit(var1, null);
      this.paramSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
      this.isInitState = true;
      this.reInit();
   }

   @Override
   protected final void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.signInit(var1, var2);
      this.paramSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
      this.isInitState = true;
      this.reInit();
   }

   protected abstract void signInit(PrivateKey var1, SecureRandom var2) throws InvalidKeyException;

   @Override
   protected final void engineUpdate(byte var1) throws SignatureException {
      this.isInitState = false;
      this.updateEngine(var1);
   }

   protected abstract void updateEngine(byte var1) throws SignatureException;

   @Override
   protected final void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      this.isInitState = false;
      this.updateEngine(var1, var2, var3);
   }

   protected abstract void updateEngine(byte[] var1, int var2, int var3) throws SignatureException;

   @Override
   protected void engineSetParameter(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 == null) {
         if (this.originalSpec == null) {
            return;
         }

         var1 = this.originalSpec;
      }

      if (!this.isInitState) {
         throw new ProviderException("cannot call setParameter in the middle of update");
      } else {
         if (var1 instanceof ContextParameterSpec) {
            this.paramSpec = (ContextParameterSpec)var1;
            this.reInit();
         } else {
            byte[] var2 = SpecUtil.getContextFrom(var1);
            if (var2 == null) {
               throw new InvalidAlgorithmParameterException("unknown AlgorithmParameterSpec in signature");
            }

            this.paramSpec = new ContextParameterSpec(var2);
            this.reInit();
         }
      }
   }

   private void reInit() {
      Object var1 = this.keyParams;
      if (this.keyParams.isPrivate()) {
         if (this.appRandom != null) {
            var1 = new ParametersWithRandom((CipherParameters)var1, this.appRandom);
         }

         if (this.paramSpec != null) {
            var1 = new ParametersWithContext((CipherParameters)var1, this.paramSpec.getContext());
         }

         this.reInitialize(true, (CipherParameters)var1);
      } else {
         if (this.paramSpec != null) {
            var1 = new ParametersWithContext((CipherParameters)var1, this.paramSpec.getContext());
         }

         this.reInitialize(false, (CipherParameters)var1);
      }
   }

   protected abstract void reInitialize(boolean var1, CipherParameters var2);

   @Override
   protected final AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null && this.paramSpec != null && this.paramSpec != ContextParameterSpec.EMPTY_CONTEXT_SPEC) {
         try {
            this.engineParams = this.helper.createAlgorithmParameters("CONTEXT");
            this.engineParams.init(this.paramSpec);
         } catch (Exception var2) {
            throw Exceptions.illegalStateException(var2.toString(), var2);
         }
      }

      return this.engineParams;
   }

   /** @deprecated */
   @Override
   protected final void engineSetParameter(String var1, Object var2) {
      throw new UnsupportedOperationException("SetParameter unsupported");
   }

   /** @deprecated */
   @Override
   protected final Object engineGetParameter(String var1) {
      throw new UnsupportedOperationException("GetParameter unsupported");
   }
}
