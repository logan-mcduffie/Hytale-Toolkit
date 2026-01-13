package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi;

public class Snova {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.snova.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.Snova", "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi");
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_24_5_4_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_24_5_4_SSK",
            BCObjectIdentifiers.snova_24_5_4_ssk,
            new SnovaKeyFactorySpi.SNOVA_24_5_4_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_24_5_4_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_24_5_4_ESK",
            BCObjectIdentifiers.snova_24_5_4_esk,
            new SnovaKeyFactorySpi.SNOVA_24_5_4_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_24_5_4_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_24_5_4_SHAKE_SSK",
            BCObjectIdentifiers.snova_24_5_4_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_24_5_4_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_24_5_4_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_24_5_4_SHAKE_ESK",
            BCObjectIdentifiers.snova_24_5_4_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_24_5_4_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_24_5_5_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_24_5_5_SSK",
            BCObjectIdentifiers.snova_24_5_5_ssk,
            new SnovaKeyFactorySpi.SNOVA_24_5_5_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_24_5_5_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_24_5_5_ESK",
            BCObjectIdentifiers.snova_24_5_5_esk,
            new SnovaKeyFactorySpi.SNOVA_24_5_5_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_24_5_5_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_24_5_5_SHAKE_SSK",
            BCObjectIdentifiers.snova_24_5_5_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_24_5_5_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_24_5_5_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_24_5_5_SHAKE_ESK",
            BCObjectIdentifiers.snova_24_5_5_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_24_5_5_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_25_8_3_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_25_8_3_SSK",
            BCObjectIdentifiers.snova_25_8_3_ssk,
            new SnovaKeyFactorySpi.SNOVA_25_8_3_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_25_8_3_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_25_8_3_ESK",
            BCObjectIdentifiers.snova_25_8_3_esk,
            new SnovaKeyFactorySpi.SNOVA_25_8_3_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_25_8_3_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_25_8_3_SHAKE_SSK",
            BCObjectIdentifiers.snova_25_8_3_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_25_8_3_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_25_8_3_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_25_8_3_SHAKE_ESK",
            BCObjectIdentifiers.snova_25_8_3_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_25_8_3_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_29_6_5_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_29_6_5_SSK",
            BCObjectIdentifiers.snova_29_6_5_ssk,
            new SnovaKeyFactorySpi.SNOVA_29_6_5_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_29_6_5_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_29_6_5_ESK",
            BCObjectIdentifiers.snova_29_6_5_esk,
            new SnovaKeyFactorySpi.SNOVA_29_6_5_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_29_6_5_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_29_6_5_SHAKE_SSK",
            BCObjectIdentifiers.snova_29_6_5_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_29_6_5_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_29_6_5_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_29_6_5_SHAKE_ESK",
            BCObjectIdentifiers.snova_29_6_5_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_29_6_5_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_37_8_4_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_37_8_4_SSK",
            BCObjectIdentifiers.snova_37_8_4_ssk,
            new SnovaKeyFactorySpi.SNOVA_37_8_4_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_37_8_4_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_37_8_4_ESK",
            BCObjectIdentifiers.snova_37_8_4_esk,
            new SnovaKeyFactorySpi.SNOVA_37_8_4_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_37_8_4_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_37_8_4_SHAKE_SSK",
            BCObjectIdentifiers.snova_37_8_4_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_37_8_4_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_37_8_4_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_37_8_4_SHAKE_ESK",
            BCObjectIdentifiers.snova_37_8_4_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_37_8_4_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_37_17_2_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_37_17_2_SSK",
            BCObjectIdentifiers.snova_37_17_2_ssk,
            new SnovaKeyFactorySpi.SNOVA_37_17_2_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_37_17_2_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_37_17_2_ESK",
            BCObjectIdentifiers.snova_37_17_2_esk,
            new SnovaKeyFactorySpi.SNOVA_37_17_2_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_37_17_2_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_37_17_2_SHAKE_SSK",
            BCObjectIdentifiers.snova_37_17_2_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_37_17_2_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_37_17_2_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_37_17_2_SHAKE_ESK",
            BCObjectIdentifiers.snova_37_17_2_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_37_17_2_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_49_11_3_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_49_11_3_SSK",
            BCObjectIdentifiers.snova_49_11_3_ssk,
            new SnovaKeyFactorySpi.SNOVA_49_11_3_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_49_11_3_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_49_11_3_ESK",
            BCObjectIdentifiers.snova_49_11_3_esk,
            new SnovaKeyFactorySpi.SNOVA_49_11_3_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_49_11_3_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_49_11_3_SHAKE_SSK",
            BCObjectIdentifiers.snova_49_11_3_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_49_11_3_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_49_11_3_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_49_11_3_SHAKE_ESK",
            BCObjectIdentifiers.snova_49_11_3_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_49_11_3_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_56_25_2_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_56_25_2_SSK",
            BCObjectIdentifiers.snova_56_25_2_ssk,
            new SnovaKeyFactorySpi.SNOVA_56_25_2_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_56_25_2_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_56_25_2_ESK",
            BCObjectIdentifiers.snova_56_25_2_esk,
            new SnovaKeyFactorySpi.SNOVA_56_25_2_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_56_25_2_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_56_25_2_SHAKE_SSK",
            BCObjectIdentifiers.snova_56_25_2_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_56_25_2_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_56_25_2_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_56_25_2_SHAKE_ESK",
            BCObjectIdentifiers.snova_56_25_2_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_56_25_2_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_60_10_4_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_60_10_4_SSK",
            BCObjectIdentifiers.snova_60_10_4_ssk,
            new SnovaKeyFactorySpi.SNOVA_60_10_4_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_60_10_4_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_60_10_4_ESK",
            BCObjectIdentifiers.snova_60_10_4_esk,
            new SnovaKeyFactorySpi.SNOVA_60_10_4_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_60_10_4_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_60_10_4_SHAKE_SSK",
            BCObjectIdentifiers.snova_60_10_4_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_60_10_4_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_60_10_4_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_60_10_4_SHAKE_ESK",
            BCObjectIdentifiers.snova_60_10_4_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_60_10_4_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_66_15_3_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_66_15_3_SSK",
            BCObjectIdentifiers.snova_66_15_3_ssk,
            new SnovaKeyFactorySpi.SNOVA_66_15_3_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_66_15_3_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_66_15_3_ESK",
            BCObjectIdentifiers.snova_66_15_3_esk,
            new SnovaKeyFactorySpi.SNOVA_66_15_3_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_66_15_3_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_66_15_3_SHAKE_SSK",
            BCObjectIdentifiers.snova_66_15_3_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_66_15_3_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_66_15_3_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_66_15_3_SHAKE_ESK",
            BCObjectIdentifiers.snova_66_15_3_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_66_15_3_SHAKE_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_75_33_2_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_75_33_2_SSK",
            BCObjectIdentifiers.snova_75_33_2_ssk,
            new SnovaKeyFactorySpi.SNOVA_75_33_2_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_75_33_2_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_75_33_2_ESK",
            BCObjectIdentifiers.snova_75_33_2_esk,
            new SnovaKeyFactorySpi.SNOVA_75_33_2_ESK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_75_33_2_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_75_33_2_SHAKE_SSK",
            BCObjectIdentifiers.snova_75_33_2_shake_ssk,
            new SnovaKeyFactorySpi.SNOVA_75_33_2_SHAKE_SSK()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SNOVA_75_33_2_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi$SNOVA_75_33_2_SHAKE_ESK",
            BCObjectIdentifiers.snova_75_33_2_shake_esk,
            new SnovaKeyFactorySpi.SNOVA_75_33_2_SHAKE_ESK()
         );
         var1.addAlgorithm("KeyPairGenerator.Snova", "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi");
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_24_5_4_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_24_5_4_SSK",
            BCObjectIdentifiers.snova_24_5_4_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_24_5_4_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_24_5_4_ESK",
            BCObjectIdentifiers.snova_24_5_4_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_24_5_4_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_24_5_4_SHAKE_SSK",
            BCObjectIdentifiers.snova_24_5_4_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_24_5_4_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_24_5_4_SHAKE_ESK",
            BCObjectIdentifiers.snova_24_5_4_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_24_5_5_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_24_5_5_SSK",
            BCObjectIdentifiers.snova_24_5_5_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_24_5_5_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_24_5_5_ESK",
            BCObjectIdentifiers.snova_24_5_5_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_24_5_5_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_24_5_5_SHAKE_SSK",
            BCObjectIdentifiers.snova_24_5_5_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_24_5_5_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_24_5_5_SHAKE_ESK",
            BCObjectIdentifiers.snova_24_5_5_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_25_8_3_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_25_8_3_SSK",
            BCObjectIdentifiers.snova_25_8_3_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_25_8_3_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_25_8_3_ESK",
            BCObjectIdentifiers.snova_25_8_3_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_25_8_3_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_25_8_3_SHAKE_SSK",
            BCObjectIdentifiers.snova_25_8_3_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_25_8_3_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_25_8_3_SHAKE_ESK",
            BCObjectIdentifiers.snova_25_8_3_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_29_6_5_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_29_6_5_SSK",
            BCObjectIdentifiers.snova_29_6_5_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_29_6_5_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_29_6_5_ESK",
            BCObjectIdentifiers.snova_29_6_5_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_29_6_5_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_29_6_5_SHAKE_SSK",
            BCObjectIdentifiers.snova_29_6_5_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_29_6_5_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_29_6_5_SHAKE_ESK",
            BCObjectIdentifiers.snova_29_6_5_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_37_8_4_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_37_8_4_SSK",
            BCObjectIdentifiers.snova_37_8_4_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_37_8_4_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_37_8_4_ESK",
            BCObjectIdentifiers.snova_37_8_4_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_37_8_4_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_37_8_4_SHAKE_SSK",
            BCObjectIdentifiers.snova_37_8_4_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_37_8_4_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_37_8_4_SHAKE_ESK",
            BCObjectIdentifiers.snova_37_8_4_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_37_17_2_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_37_17_2_SSK",
            BCObjectIdentifiers.snova_37_17_2_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_37_17_2_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_37_17_2_ESK",
            BCObjectIdentifiers.snova_37_17_2_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_37_17_2_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_37_17_2_SHAKE_SSK",
            BCObjectIdentifiers.snova_37_17_2_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_37_17_2_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_37_17_2_SHAKE_ESK",
            BCObjectIdentifiers.snova_37_17_2_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_49_11_3_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_49_11_3_SSK",
            BCObjectIdentifiers.snova_49_11_3_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_49_11_3_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_49_11_3_ESK",
            BCObjectIdentifiers.snova_49_11_3_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_49_11_3_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_49_11_3_SHAKE_SSK",
            BCObjectIdentifiers.snova_49_11_3_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_49_11_3_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_49_11_3_SHAKE_ESK",
            BCObjectIdentifiers.snova_49_11_3_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_56_25_2_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_56_25_2_SSK",
            BCObjectIdentifiers.snova_56_25_2_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_56_25_2_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_56_25_2_ESK",
            BCObjectIdentifiers.snova_56_25_2_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_56_25_2_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_56_25_2_SHAKE_SSK",
            BCObjectIdentifiers.snova_56_25_2_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_56_25_2_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_56_25_2_SHAKE_ESK",
            BCObjectIdentifiers.snova_56_25_2_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_60_10_4_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_60_10_4_SSK",
            BCObjectIdentifiers.snova_60_10_4_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_60_10_4_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_60_10_4_ESK",
            BCObjectIdentifiers.snova_60_10_4_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_60_10_4_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_60_10_4_SHAKE_SSK",
            BCObjectIdentifiers.snova_60_10_4_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_60_10_4_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_60_10_4_SHAKE_ESK",
            BCObjectIdentifiers.snova_60_10_4_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_66_15_3_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_66_15_3_SSK",
            BCObjectIdentifiers.snova_66_15_3_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_66_15_3_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_66_15_3_ESK",
            BCObjectIdentifiers.snova_66_15_3_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_66_15_3_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_66_15_3_SHAKE_SSK",
            BCObjectIdentifiers.snova_66_15_3_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_66_15_3_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_66_15_3_SHAKE_ESK",
            BCObjectIdentifiers.snova_66_15_3_shake_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_75_33_2_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_75_33_2_SSK",
            BCObjectIdentifiers.snova_75_33_2_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_75_33_2_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_75_33_2_ESK",
            BCObjectIdentifiers.snova_75_33_2_esk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_75_33_2_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_75_33_2_SHAKE_SSK",
            BCObjectIdentifiers.snova_75_33_2_shake_ssk
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SNOVA_75_33_2_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyPairGeneratorSpi$SNOVA_75_33_2_SHAKE_ESK",
            BCObjectIdentifiers.snova_75_33_2_shake_esk
         );
         this.addSignatureAlgorithm(var1, "Snova", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$Base", BCObjectIdentifiers.snova);
         this.addSignatureAlgorithm(
            var1, "SNOVA_24_5_4_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_24_5_4_SSK", BCObjectIdentifiers.snova_24_5_4_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_24_5_4_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_24_5_4_ESK", BCObjectIdentifiers.snova_24_5_4_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_24_5_4_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_24_5_4_SHAKE_SSK",
            BCObjectIdentifiers.snova_24_5_4_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_24_5_4_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_24_5_4_SHAKE_ESK",
            BCObjectIdentifiers.snova_24_5_4_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_24_5_5_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_24_5_5_SSK", BCObjectIdentifiers.snova_24_5_5_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_24_5_5_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_24_5_5_ESK", BCObjectIdentifiers.snova_24_5_5_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_24_5_5_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_24_5_5_SHAKE_SSK",
            BCObjectIdentifiers.snova_24_5_5_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_24_5_5_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_24_5_5_SHAKE_ESK",
            BCObjectIdentifiers.snova_24_5_5_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_25_8_3_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_25_8_3_SSK", BCObjectIdentifiers.snova_25_8_3_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_25_8_3_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_25_8_3_ESK", BCObjectIdentifiers.snova_25_8_3_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_25_8_3_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_25_8_3_SHAKE_SSK",
            BCObjectIdentifiers.snova_25_8_3_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_25_8_3_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_25_8_3_SHAKE_ESK",
            BCObjectIdentifiers.snova_25_8_3_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_29_6_5_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_29_6_5_SSK", BCObjectIdentifiers.snova_29_6_5_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_29_6_5_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_29_6_5_ESK", BCObjectIdentifiers.snova_29_6_5_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_29_6_5_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_29_6_5_SHAKE_SSK",
            BCObjectIdentifiers.snova_29_6_5_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_29_6_5_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_29_6_5_SHAKE_ESK",
            BCObjectIdentifiers.snova_29_6_5_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_37_8_4_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_37_8_4_SSK", BCObjectIdentifiers.snova_37_8_4_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_37_8_4_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_37_8_4_ESK", BCObjectIdentifiers.snova_37_8_4_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_37_8_4_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_37_8_4_SHAKE_SSK",
            BCObjectIdentifiers.snova_37_8_4_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_37_8_4_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_37_8_4_SHAKE_ESK",
            BCObjectIdentifiers.snova_37_8_4_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_37_17_2_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_37_17_2_SSK", BCObjectIdentifiers.snova_37_17_2_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_37_17_2_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_37_17_2_ESK", BCObjectIdentifiers.snova_37_17_2_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_37_17_2_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_37_17_2_SHAKE_SSK",
            BCObjectIdentifiers.snova_37_17_2_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_37_17_2_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_37_17_2_SHAKE_ESK",
            BCObjectIdentifiers.snova_37_17_2_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_49_11_3_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_49_11_3_SSK", BCObjectIdentifiers.snova_49_11_3_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_49_11_3_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_49_11_3_ESK", BCObjectIdentifiers.snova_49_11_3_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_49_11_3_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_49_11_3_SHAKE_SSK",
            BCObjectIdentifiers.snova_49_11_3_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_49_11_3_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_49_11_3_SHAKE_ESK",
            BCObjectIdentifiers.snova_49_11_3_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_56_25_2_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_56_25_2_SSK", BCObjectIdentifiers.snova_56_25_2_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_56_25_2_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_56_25_2_ESK", BCObjectIdentifiers.snova_56_25_2_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_56_25_2_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_56_25_2_SHAKE_SSK",
            BCObjectIdentifiers.snova_56_25_2_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_56_25_2_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_56_25_2_SHAKE_ESK",
            BCObjectIdentifiers.snova_56_25_2_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_60_10_4_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_60_10_4_SSK", BCObjectIdentifiers.snova_60_10_4_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_60_10_4_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_60_10_4_ESK", BCObjectIdentifiers.snova_60_10_4_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_60_10_4_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_60_10_4_SHAKE_SSK",
            BCObjectIdentifiers.snova_60_10_4_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_60_10_4_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_60_10_4_SHAKE_ESK",
            BCObjectIdentifiers.snova_60_10_4_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_66_15_3_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_66_15_3_SSK", BCObjectIdentifiers.snova_66_15_3_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_66_15_3_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_66_15_3_ESK", BCObjectIdentifiers.snova_66_15_3_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_66_15_3_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_66_15_3_SHAKE_SSK",
            BCObjectIdentifiers.snova_66_15_3_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_66_15_3_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_66_15_3_SHAKE_ESK",
            BCObjectIdentifiers.snova_66_15_3_shake_esk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_75_33_2_SSK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_75_33_2_SSK", BCObjectIdentifiers.snova_75_33_2_ssk
         );
         this.addSignatureAlgorithm(
            var1, "SNOVA_75_33_2_ESK", "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_75_33_2_ESK", BCObjectIdentifiers.snova_75_33_2_esk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_75_33_2_SHAKE_SSK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_75_33_2_SHAKE_SSK",
            BCObjectIdentifiers.snova_75_33_2_shake_ssk
         );
         this.addSignatureAlgorithm(
            var1,
            "SNOVA_75_33_2_SHAKE_ESK",
            "org.bouncycastle.pqc.jcajce.provider.snova.SignatureSpi$SNOVA_75_33_2_SHAKE_ESK",
            BCObjectIdentifiers.snova_75_33_2_shake_esk
         );
      }
   }
}
