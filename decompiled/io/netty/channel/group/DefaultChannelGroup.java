package io.netty.channel.group;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.ServerChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultChannelGroup extends AbstractSet<Channel> implements ChannelGroup {
   private static final AtomicInteger nextId = new AtomicInteger();
   private final String name;
   private final EventExecutor executor;
   private final ConcurrentMap<ChannelId, Channel> serverChannels = new ConcurrentHashMap<>();
   private final ConcurrentMap<ChannelId, Channel> nonServerChannels = new ConcurrentHashMap<>();
   private final ChannelFutureListener remover = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
         DefaultChannelGroup.this.remove(future.channel());
      }
   };
   private final VoidChannelGroupFuture voidFuture = new VoidChannelGroupFuture(this);
   private final boolean stayClosed;
   private volatile boolean closed;

   public DefaultChannelGroup(EventExecutor executor) {
      this(executor, false);
   }

   public DefaultChannelGroup(String name, EventExecutor executor) {
      this(name, executor, false);
   }

   public DefaultChannelGroup(EventExecutor executor, boolean stayClosed) {
      this("group-0x" + Integer.toHexString(nextId.incrementAndGet()), executor, stayClosed);
   }

   public DefaultChannelGroup(String name, EventExecutor executor, boolean stayClosed) {
      ObjectUtil.checkNotNull(name, "name");
      this.name = name;
      this.executor = executor;
      this.stayClosed = stayClosed;
   }

   @Override
   public String name() {
      return this.name;
   }

   @Override
   public Channel find(ChannelId id) {
      Channel c = this.nonServerChannels.get(id);
      return c != null ? c : this.serverChannels.get(id);
   }

   @Override
   public boolean isEmpty() {
      return this.nonServerChannels.isEmpty() && this.serverChannels.isEmpty();
   }

   @Override
   public int size() {
      return this.nonServerChannels.size() + this.serverChannels.size();
   }

   @Override
   public boolean contains(Object o) {
      if (o instanceof ServerChannel) {
         return this.serverChannels.containsValue(o);
      } else {
         return o instanceof Channel ? this.nonServerChannels.containsValue(o) : false;
      }
   }

   public boolean add(Channel channel) {
      ConcurrentMap<ChannelId, Channel> map = channel instanceof ServerChannel ? this.serverChannels : this.nonServerChannels;
      boolean added = map.putIfAbsent(channel.id(), channel) == null;
      if (added) {
         channel.closeFuture().addListener(this.remover);
      }

      if (this.stayClosed && this.closed) {
         channel.close();
      }

      return added;
   }

   @Override
   public boolean remove(Object o) {
      Channel c = null;
      if (o instanceof ChannelId) {
         c = this.nonServerChannels.remove(o);
         if (c == null) {
            c = this.serverChannels.remove(o);
         }
      } else if (o instanceof Channel) {
         c = (Channel)o;
         if (c instanceof ServerChannel) {
            c = this.serverChannels.remove(c.id());
         } else {
            c = this.nonServerChannels.remove(c.id());
         }
      }

      if (c == null) {
         return false;
      } else {
         c.closeFuture().removeListener(this.remover);
         return true;
      }
   }

   @Override
   public void clear() {
      this.nonServerChannels.clear();
      this.serverChannels.clear();
   }

   @Override
   public Iterator<Channel> iterator() {
      return new CombinedIterator<>(this.serverChannels.values().iterator(), this.nonServerChannels.values().iterator());
   }

   @Override
   public Object[] toArray() {
      Collection<Channel> channels = new ArrayList<>(this.size());
      channels.addAll(this.serverChannels.values());
      channels.addAll(this.nonServerChannels.values());
      return channels.toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      Collection<Channel> channels = new ArrayList<>(this.size());
      channels.addAll(this.serverChannels.values());
      channels.addAll(this.nonServerChannels.values());
      return (T[])channels.toArray(a);
   }

   @Override
   public ChannelGroupFuture close() {
      return this.close(ChannelMatchers.all());
   }

   @Override
   public ChannelGroupFuture disconnect() {
      return this.disconnect(ChannelMatchers.all());
   }

   @Override
   public ChannelGroupFuture deregister() {
      return this.deregister(ChannelMatchers.all());
   }

   @Override
   public ChannelGroupFuture write(Object message) {
      return this.write(message, ChannelMatchers.all());
   }

   private static Object safeDuplicate(Object message) {
      if (message instanceof ByteBuf) {
         return ((ByteBuf)message).retainedDuplicate();
      } else {
         return message instanceof ByteBufHolder ? ((ByteBufHolder)message).retainedDuplicate() : ReferenceCountUtil.retain(message);
      }
   }

   @Override
   public ChannelGroupFuture write(Object message, ChannelMatcher matcher) {
      return this.write(message, matcher, false);
   }

   @Override
   public ChannelGroupFuture write(Object message, ChannelMatcher matcher, boolean voidPromise) {
      ObjectUtil.checkNotNull(message, "message");
      ObjectUtil.checkNotNull(matcher, "matcher");
      ChannelGroupFuture future;
      if (voidPromise) {
         for (Channel c : this.nonServerChannels.values()) {
            if (matcher.matches(c)) {
               c.write(safeDuplicate(message), c.voidPromise());
            }
         }

         future = this.voidFuture;
      } else {
         Map<Channel, ChannelFuture> futures = new LinkedHashMap<>(this.nonServerChannels.size());

         for (Channel cx : this.nonServerChannels.values()) {
            if (matcher.matches(cx)) {
               futures.put(cx, cx.write(safeDuplicate(message)));
            }
         }

         future = new DefaultChannelGroupFuture(this, futures, this.executor);
      }

      ReferenceCountUtil.release(message);
      return future;
   }

   @Override
   public ChannelGroup flush() {
      return this.flush(ChannelMatchers.all());
   }

   @Override
   public ChannelGroupFuture flushAndWrite(Object message) {
      return this.writeAndFlush(message);
   }

   @Override
   public ChannelGroupFuture writeAndFlush(Object message) {
      return this.writeAndFlush(message, ChannelMatchers.all());
   }

   @Override
   public ChannelGroupFuture disconnect(ChannelMatcher matcher) {
      ObjectUtil.checkNotNull(matcher, "matcher");
      Map<Channel, ChannelFuture> futures = new LinkedHashMap<>(this.size());

      for (Channel c : this.serverChannels.values()) {
         if (matcher.matches(c)) {
            futures.put(c, c.disconnect());
         }
      }

      for (Channel cx : this.nonServerChannels.values()) {
         if (matcher.matches(cx)) {
            futures.put(cx, cx.disconnect());
         }
      }

      return new DefaultChannelGroupFuture(this, futures, this.executor);
   }

   @Override
   public ChannelGroupFuture close(ChannelMatcher matcher) {
      ObjectUtil.checkNotNull(matcher, "matcher");
      Map<Channel, ChannelFuture> futures = new LinkedHashMap<>(this.size());
      if (this.stayClosed) {
         this.closed = true;
      }

      for (Channel c : this.serverChannels.values()) {
         if (matcher.matches(c)) {
            futures.put(c, c.close());
         }
      }

      for (Channel cx : this.nonServerChannels.values()) {
         if (matcher.matches(cx)) {
            futures.put(cx, cx.close());
         }
      }

      return new DefaultChannelGroupFuture(this, futures, this.executor);
   }

   @Override
   public ChannelGroupFuture deregister(ChannelMatcher matcher) {
      ObjectUtil.checkNotNull(matcher, "matcher");
      Map<Channel, ChannelFuture> futures = new LinkedHashMap<>(this.size());

      for (Channel c : this.serverChannels.values()) {
         if (matcher.matches(c)) {
            futures.put(c, c.deregister());
         }
      }

      for (Channel cx : this.nonServerChannels.values()) {
         if (matcher.matches(cx)) {
            futures.put(cx, cx.deregister());
         }
      }

      return new DefaultChannelGroupFuture(this, futures, this.executor);
   }

   @Override
   public ChannelGroup flush(ChannelMatcher matcher) {
      for (Channel c : this.nonServerChannels.values()) {
         if (matcher.matches(c)) {
            c.flush();
         }
      }

      return this;
   }

   @Override
   public ChannelGroupFuture flushAndWrite(Object message, ChannelMatcher matcher) {
      return this.writeAndFlush(message, matcher);
   }

   @Override
   public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher) {
      return this.writeAndFlush(message, matcher, false);
   }

   @Override
   public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher, boolean voidPromise) {
      ObjectUtil.checkNotNull(message, "message");
      ChannelGroupFuture future;
      if (voidPromise) {
         for (Channel c : this.nonServerChannels.values()) {
            if (matcher.matches(c)) {
               c.writeAndFlush(safeDuplicate(message), c.voidPromise());
            }
         }

         future = this.voidFuture;
      } else {
         Map<Channel, ChannelFuture> futures = new LinkedHashMap<>(this.nonServerChannels.size());

         for (Channel cx : this.nonServerChannels.values()) {
            if (matcher.matches(cx)) {
               futures.put(cx, cx.writeAndFlush(safeDuplicate(message)));
            }
         }

         future = new DefaultChannelGroupFuture(this, futures, this.executor);
      }

      ReferenceCountUtil.release(message);
      return future;
   }

   @Override
   public ChannelGroupFuture newCloseFuture() {
      return this.newCloseFuture(ChannelMatchers.all());
   }

   @Override
   public ChannelGroupFuture newCloseFuture(ChannelMatcher matcher) {
      Map<Channel, ChannelFuture> futures = new LinkedHashMap<>(this.size());

      for (Channel c : this.serverChannels.values()) {
         if (matcher.matches(c)) {
            futures.put(c, c.closeFuture());
         }
      }

      for (Channel cx : this.nonServerChannels.values()) {
         if (matcher.matches(cx)) {
            futures.put(cx, cx.closeFuture());
         }
      }

      return new DefaultChannelGroupFuture(this, futures, this.executor);
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this);
   }

   @Override
   public boolean equals(Object o) {
      return this == o;
   }

   public int compareTo(ChannelGroup o) {
      int v = this.name().compareTo(o.name());
      return v != 0 ? v : System.identityHashCode(this) - System.identityHashCode(o);
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(name: " + this.name() + ", size: " + this.size() + ')';
   }
}
