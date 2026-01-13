package org.bouncycastle.its;

import java.util.Date;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Duration;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

public class ITSValidityPeriod {
   private final long startDate;
   private final UINT16 duration;
   private final ITSValidityPeriod.Unit timeUnit;

   public static ITSValidityPeriod.Builder from(Date var0) {
      return new ITSValidityPeriod.Builder(var0);
   }

   public ITSValidityPeriod(ValidityPeriod var1) {
      this.startDate = var1.getStart().getValue().longValue();
      Duration var2 = var1.getDuration();
      this.duration = var2.getDuration();
      this.timeUnit = ITSValidityPeriod.Unit.values()[var2.getChoice()];
   }

   ITSValidityPeriod(long var1, UINT16 var3, ITSValidityPeriod.Unit var4) {
      this.startDate = var1;
      this.duration = var3;
      this.timeUnit = var4;
   }

   public Date getStartDate() {
      return new Date(this.startDate);
   }

   public ValidityPeriod toASN1Structure() {
      return ValidityPeriod.builder()
         .setStart(new Time32(this.startDate / 1000L))
         .setDuration(new Duration(this.timeUnit.unitTag, this.duration))
         .createValidityPeriod();
   }

   public static class Builder {
      private final long startDate;

      Builder(Date var1) {
         this.startDate = var1.getTime();
      }

      public ITSValidityPeriod plusYears(int var1) {
         return new ITSValidityPeriod(this.startDate, UINT16.valueOf(var1), ITSValidityPeriod.Unit.years);
      }

      public ITSValidityPeriod plusSixtyHours(int var1) {
         return new ITSValidityPeriod(this.startDate, UINT16.valueOf(var1), ITSValidityPeriod.Unit.sixtyHours);
      }
   }

   public static enum Unit {
      microseconds(0),
      milliseconds(1),
      seconds(2),
      minutes(3),
      hours(4),
      sixtyHours(5),
      years(6);

      private final int unitTag;

      private Unit(int nullxx) {
         this.unitTag = nullxx;
      }
   }
}
