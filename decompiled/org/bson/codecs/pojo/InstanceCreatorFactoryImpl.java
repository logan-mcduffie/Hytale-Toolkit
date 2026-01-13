package org.bson.codecs.pojo;

final class InstanceCreatorFactoryImpl<T> implements InstanceCreatorFactory<T> {
   private final CreatorExecutable<T> creatorExecutable;

   InstanceCreatorFactoryImpl(CreatorExecutable<T> creatorExecutable) {
      this.creatorExecutable = creatorExecutable;
   }

   @Override
   public InstanceCreator<T> create() {
      return new InstanceCreatorImpl<>(this.creatorExecutable);
   }
}
