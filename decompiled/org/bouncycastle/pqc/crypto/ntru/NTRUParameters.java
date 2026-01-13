package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.crypto.KEMParameters;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPS2048509;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPS2048677;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPS40961229;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPS4096821;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSS1373;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSS701;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;

public class NTRUParameters implements KEMParameters {
   public static final NTRUParameters ntruhps2048509 = new NTRUParameters("ntruhps2048509", new NTRUHPS2048509());
   public static final NTRUParameters ntruhps2048677 = new NTRUParameters("ntruhps2048677", new NTRUHPS2048677());
   public static final NTRUParameters ntruhps4096821 = new NTRUParameters("ntruhps4096821", new NTRUHPS4096821());
   public static final NTRUParameters ntruhps40961229 = new NTRUParameters("ntruhps40961229", new NTRUHPS40961229());
   public static final NTRUParameters ntruhrss701 = new NTRUParameters("ntruhrss701", new NTRUHRSS701());
   public static final NTRUParameters ntruhrss1373 = new NTRUParameters("ntruhrss1373", new NTRUHRSS1373());
   private final String name;
   private final NTRUParameterSet parameterSet;

   private NTRUParameters(String var1, NTRUParameterSet var2) {
      this.name = var1;
      this.parameterSet = var2;
   }

   public String getName() {
      return this.name;
   }

   NTRUParameterSet getParameterSet() {
      return this.parameterSet;
   }

   int getPrivateKeyLength() {
      return this.getParameterSet().ntruSecretKeyBytes();
   }

   int getPublicKeyLength() {
      return this.getParameterSet().ntruPublicKeyBytes();
   }

   public int getSessionKeySize() {
      return this.parameterSet.sharedKeyBytes() * 8;
   }
}
