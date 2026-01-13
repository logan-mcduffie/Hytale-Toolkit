package org.bouncycastle.cms;

import org.bouncycastle.util.Selector;

public abstract class RecipientId implements Selector {
   public static final int keyTrans = 0;
   public static final int kek = 1;
   public static final int keyAgree = 2;
   public static final int password = 3;
   public static final int kem = 4;
   private final int type;

   protected RecipientId(int var1) {
      this.type = var1;
   }

   public int getType() {
      return this.type;
   }

   @Override
   public abstract Object clone();
}
