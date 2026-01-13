package io.netty.util;

public final class Signal extends Error implements Constant<Signal> {
   private static final long serialVersionUID = -221145131122459977L;
   private static final ConstantPool<Signal> pool = new ConstantPool<Signal>() {
      protected Signal newConstant(int id, String name) {
         return new Signal(id, name);
      }
   };
   private final Signal.SignalConstant constant;

   public static Signal valueOf(String name) {
      return pool.valueOf(name);
   }

   public static Signal valueOf(Class<?> firstNameComponent, String secondNameComponent) {
      return pool.valueOf(firstNameComponent, secondNameComponent);
   }

   private Signal(int id, String name) {
      this.constant = new Signal.SignalConstant(id, name);
   }

   public void expect(Signal signal) {
      if (this != signal) {
         throw new IllegalStateException("unexpected signal: " + signal);
      }
   }

   @Override
   public Throwable initCause(Throwable cause) {
      return this;
   }

   @Override
   public Throwable fillInStackTrace() {
      return this;
   }

   @Override
   public int id() {
      return this.constant.id();
   }

   @Override
   public String name() {
      return this.constant.name();
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj;
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this);
   }

   public int compareTo(Signal other) {
      return this == other ? 0 : this.constant.compareTo(other.constant);
   }

   @Override
   public String toString() {
      return this.name();
   }

   private static final class SignalConstant extends AbstractConstant<Signal.SignalConstant> {
      SignalConstant(int id, String name) {
         super(id, name);
      }
   }
}
