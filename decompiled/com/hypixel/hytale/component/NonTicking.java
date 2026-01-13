package com.hypixel.hytale.component;

public class NonTicking<ECS_TYPE> implements Component<ECS_TYPE> {
   private static final NonTicking<?> INSTANCE = new NonTicking();

   public static <ECS_TYPE> NonTicking<ECS_TYPE> get() {
      return (NonTicking<ECS_TYPE>)INSTANCE;
   }

   @Override
   public Component<ECS_TYPE> clone() {
      return get();
   }
}
