package io.sentry;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public final class BaggageHeader {
   @NotNull
   public static final String BAGGAGE_HEADER = "baggage";
   @NotNull
   private final String value;

   @Nullable
   public static BaggageHeader fromBaggageAndOutgoingHeader(@NotNull Baggage baggage, @Nullable List<String> outgoingBaggageHeaders) {
      Baggage thirdPartyBaggage = Baggage.fromHeader(outgoingBaggageHeaders, true, baggage.logger);
      String headerValue = baggage.toHeaderString(thirdPartyBaggage.getThirdPartyHeader());
      return headerValue.isEmpty() ? null : new BaggageHeader(headerValue);
   }

   public BaggageHeader(@NotNull String value) {
      this.value = value;
   }

   @NotNull
   public String getName() {
      return "baggage";
   }

   @NotNull
   public String getValue() {
      return this.value;
   }
}
