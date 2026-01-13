package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.AbstractConstant;
import io.netty.util.ConstantPool;
import io.netty.util.internal.ObjectUtil;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class ChannelOption<T> extends AbstractConstant<ChannelOption<T>> {
   private static final ConstantPool<ChannelOption<Object>> pool = new ConstantPool<ChannelOption<Object>>() {
      protected ChannelOption<Object> newConstant(int id, String name) {
         return new ChannelOption<>(id, name);
      }
   };
   public static final ChannelOption<ByteBufAllocator> ALLOCATOR = valueOf("ALLOCATOR");
   public static final ChannelOption<RecvByteBufAllocator> RECVBUF_ALLOCATOR = valueOf("RECVBUF_ALLOCATOR");
   @Deprecated
   public static final ChannelOption<RecvByteBufAllocator> RCVBUF_ALLOCATOR = RECVBUF_ALLOCATOR;
   public static final ChannelOption<MessageSizeEstimator> MESSAGE_SIZE_ESTIMATOR = valueOf("MESSAGE_SIZE_ESTIMATOR");
   public static final ChannelOption<Integer> CONNECT_TIMEOUT_MILLIS = valueOf("CONNECT_TIMEOUT_MILLIS");
   @Deprecated
   public static final ChannelOption<Integer> MAX_MESSAGES_PER_READ = valueOf("MAX_MESSAGES_PER_READ");
   public static final ChannelOption<Integer> MAX_MESSAGES_PER_WRITE = valueOf("MAX_MESSAGES_PER_WRITE");
   public static final ChannelOption<Integer> WRITE_SPIN_COUNT = valueOf("WRITE_SPIN_COUNT");
   @Deprecated
   public static final ChannelOption<Integer> WRITE_BUFFER_HIGH_WATER_MARK = valueOf("WRITE_BUFFER_HIGH_WATER_MARK");
   @Deprecated
   public static final ChannelOption<Integer> WRITE_BUFFER_LOW_WATER_MARK = valueOf("WRITE_BUFFER_LOW_WATER_MARK");
   public static final ChannelOption<WriteBufferWaterMark> WRITE_BUFFER_WATER_MARK = valueOf("WRITE_BUFFER_WATER_MARK");
   public static final ChannelOption<Boolean> ALLOW_HALF_CLOSURE = valueOf("ALLOW_HALF_CLOSURE");
   public static final ChannelOption<Boolean> AUTO_READ = valueOf("AUTO_READ");
   public static final ChannelOption<Boolean> AUTO_CLOSE = valueOf("AUTO_CLOSE");
   public static final ChannelOption<Boolean> SO_BROADCAST = valueOf("SO_BROADCAST");
   public static final ChannelOption<Boolean> SO_KEEPALIVE = valueOf("SO_KEEPALIVE");
   public static final ChannelOption<Integer> SO_SNDBUF = valueOf("SO_SNDBUF");
   public static final ChannelOption<Integer> SO_RCVBUF = valueOf("SO_RCVBUF");
   public static final ChannelOption<Boolean> SO_REUSEADDR = valueOf("SO_REUSEADDR");
   public static final ChannelOption<Integer> SO_LINGER = valueOf("SO_LINGER");
   public static final ChannelOption<Integer> SO_BACKLOG = valueOf("SO_BACKLOG");
   public static final ChannelOption<Integer> SO_TIMEOUT = valueOf("SO_TIMEOUT");
   public static final ChannelOption<Integer> IP_TOS = valueOf("IP_TOS");
   public static final ChannelOption<InetAddress> IP_MULTICAST_ADDR = valueOf("IP_MULTICAST_ADDR");
   public static final ChannelOption<NetworkInterface> IP_MULTICAST_IF = valueOf("IP_MULTICAST_IF");
   public static final ChannelOption<Integer> IP_MULTICAST_TTL = valueOf("IP_MULTICAST_TTL");
   public static final ChannelOption<Boolean> IP_MULTICAST_LOOP_DISABLED = valueOf("IP_MULTICAST_LOOP_DISABLED");
   public static final ChannelOption<Boolean> TCP_NODELAY = valueOf("TCP_NODELAY");
   public static final ChannelOption<Boolean> TCP_FASTOPEN_CONNECT = valueOf("TCP_FASTOPEN_CONNECT");
   public static final ChannelOption<Integer> TCP_FASTOPEN = valueOf(ChannelOption.class, "TCP_FASTOPEN");
   @Deprecated
   public static final ChannelOption<Boolean> DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION = valueOf("DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION");
   public static final ChannelOption<Boolean> SINGLE_EVENTEXECUTOR_PER_GROUP = valueOf("SINGLE_EVENTEXECUTOR_PER_GROUP");

   public static <T> ChannelOption<T> valueOf(String name) {
      return (ChannelOption<T>)pool.valueOf(name);
   }

   public static <T> ChannelOption<T> valueOf(Class<?> firstNameComponent, String secondNameComponent) {
      return (ChannelOption<T>)pool.valueOf(firstNameComponent, secondNameComponent);
   }

   public static boolean exists(String name) {
      return pool.exists(name);
   }

   @Deprecated
   public static <T> ChannelOption<T> newInstance(String name) {
      return (ChannelOption<T>)pool.newInstance(name);
   }

   private ChannelOption(int id, String name) {
      super(id, name);
   }

   @Deprecated
   protected ChannelOption(String name) {
      this(pool.nextId(), name);
   }

   public void validate(T value) {
      ObjectUtil.checkNotNull(value, "value");
   }
}
