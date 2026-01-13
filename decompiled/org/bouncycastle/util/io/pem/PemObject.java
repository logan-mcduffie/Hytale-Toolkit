package org.bouncycastle.util.io.pem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PemObject implements PemObjectGenerator {
   private static final List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
   private String type;
   private List headers;
   private byte[] content;

   public PemObject(String var1, byte[] var2) {
      this(var1, EMPTY_LIST, var2);
   }

   public PemObject(String var1, List var2, byte[] var3) {
      this.type = var1;
      this.headers = Collections.unmodifiableList(var2);
      this.content = var3;
   }

   public String getType() {
      return this.type;
   }

   public List getHeaders() {
      return this.headers;
   }

   public byte[] getContent() {
      return this.content;
   }

   @Override
   public PemObject generate() throws PemGenerationException {
      return this;
   }
}
