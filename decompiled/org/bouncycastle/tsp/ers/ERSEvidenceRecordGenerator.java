package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.tsp.EvidenceRecord;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.tsp.TSPException;

public class ERSEvidenceRecordGenerator {
   private final DigestCalculatorProvider digCalcProv;

   public ERSEvidenceRecordGenerator(DigestCalculatorProvider var1) {
      this.digCalcProv = var1;
   }

   public ERSEvidenceRecord generate(ERSArchiveTimeStamp var1) throws TSPException, ERSException {
      return new ERSEvidenceRecord(new EvidenceRecord(null, null, var1.toASN1Structure()), this.digCalcProv);
   }

   public List<ERSEvidenceRecord> generate(List<ERSArchiveTimeStamp> var1) throws TSPException, ERSException {
      ArrayList var2 = new ArrayList(var1.size());

      for (int var3 = 0; var3 != var1.size(); var3++) {
         var2.add(new ERSEvidenceRecord(new EvidenceRecord(null, null, ((ERSArchiveTimeStamp)var1.get(var3)).toASN1Structure()), this.digCalcProv));
      }

      return var2;
   }
}
