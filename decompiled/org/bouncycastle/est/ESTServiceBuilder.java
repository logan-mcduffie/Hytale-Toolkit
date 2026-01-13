package org.bouncycastle.est;

public class ESTServiceBuilder {
   protected final String server;
   protected ESTClientProvider clientProvider;
   protected String label;

   public ESTServiceBuilder(String var1) {
      this.server = var1;
   }

   public ESTServiceBuilder withLabel(String var1) {
      this.label = var1;
      return this;
   }

   public ESTServiceBuilder withClientProvider(ESTClientProvider var1) {
      this.clientProvider = var1;
      return this;
   }

   public ESTService build() {
      return new ESTService(this.server, this.label, this.clientProvider);
   }
}
