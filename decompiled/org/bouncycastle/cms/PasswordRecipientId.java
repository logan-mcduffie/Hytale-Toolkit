package org.bouncycastle.cms;

public class PasswordRecipientId extends RecipientId {
   public PasswordRecipientId() {
      super(3);
   }

   @Override
   public int hashCode() {
      return 3;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof PasswordRecipientId;
   }

   @Override
   public Object clone() {
      return new PasswordRecipientId();
   }

   @Override
   public boolean match(Object var1) {
      return var1 instanceof PasswordRecipientInformation;
   }
}
