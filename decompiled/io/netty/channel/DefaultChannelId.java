package io.netty.channel;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.MacAddressUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultChannelId implements ChannelId {
   private static final long serialVersionUID = 3884076183504074063L;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelId.class);
   private static final byte[] MACHINE_ID;
   private static final int PROCESS_ID_LEN = 4;
   private static final int PROCESS_ID;
   private static final int SEQUENCE_LEN = 4;
   private static final int TIMESTAMP_LEN = 8;
   private static final int RANDOM_LEN = 4;
   private static final AtomicInteger nextSequence = new AtomicInteger();
   private final byte[] data;
   private final int hashCode;
   private transient String shortValue;
   private transient String longValue;

   public static DefaultChannelId newInstance() {
      return new DefaultChannelId(
         MACHINE_ID,
         PROCESS_ID,
         nextSequence.getAndIncrement(),
         Long.reverse(System.nanoTime()) ^ System.currentTimeMillis(),
         ThreadLocalRandom.current().nextInt()
      );
   }

   static int processHandlePid(ClassLoader loader) {
      int nilValue = -1;
      if (PlatformDependent.javaVersion() >= 9) {
         Long pid;
         try {
            Class<?> processHandleImplType = Class.forName("java.lang.ProcessHandle", true, loader);
            Method processHandleCurrent = processHandleImplType.getMethod("current");
            Object processHandleInstance = processHandleCurrent.invoke(null);
            Method processHandlePid = processHandleImplType.getMethod("pid");
            pid = (Long)processHandlePid.invoke(processHandleInstance);
         } catch (Exception var7) {
            logger.debug("Could not invoke ProcessHandle.current().pid();", (Throwable)var7);
            return nilValue;
         }

         if (pid <= 2147483647L && pid >= -2147483648L) {
            return pid.intValue();
         } else {
            throw new IllegalStateException("Current process ID exceeds int range: " + pid);
         }
      } else {
         return nilValue;
      }
   }

   static int jmxPid(ClassLoader loader) {
      String value;
      try {
         Class<?> mgmtFactoryType = Class.forName("java.lang.management.ManagementFactory", true, loader);
         Class<?> runtimeMxBeanType = Class.forName("java.lang.management.RuntimeMXBean", true, loader);
         Method getRuntimeMXBean = mgmtFactoryType.getMethod("getRuntimeMXBean", EmptyArrays.EMPTY_CLASSES);
         Object bean = getRuntimeMXBean.invoke(null, EmptyArrays.EMPTY_OBJECTS);
         Method getName = runtimeMxBeanType.getMethod("getName", EmptyArrays.EMPTY_CLASSES);
         value = (String)getName.invoke(bean, EmptyArrays.EMPTY_OBJECTS);
      } catch (Throwable var9) {
         logger.debug("Could not invoke ManagementFactory.getRuntimeMXBean().getName(); Android?", var9);

         try {
            Class<?> processType = Class.forName("android.os.Process", true, loader);
            Method myPid = processType.getMethod("myPid", EmptyArrays.EMPTY_CLASSES);
            value = myPid.invoke(null, EmptyArrays.EMPTY_OBJECTS).toString();
         } catch (Throwable var8) {
            logger.debug("Could not invoke Process.myPid(); not Android?", var8);
            value = "";
         }
      }

      int atIndex = value.indexOf(64);
      if (atIndex >= 0) {
         value = value.substring(0, atIndex);
      }

      int pid;
      try {
         pid = Integer.parseInt(value);
      } catch (NumberFormatException var7) {
         pid = -1;
      }

      if (pid < 0) {
         pid = ThreadLocalRandom.current().nextInt();
         logger.warn("Failed to find the current process ID from '{}'; using a random value: {}", value, pid);
      }

      return pid;
   }

   static int defaultProcessId() {
      ClassLoader loader = PlatformDependent.getClassLoader(DefaultChannelId.class);
      int processId = processHandlePid(loader);
      return processId != -1 ? processId : jmxPid(loader);
   }

   DefaultChannelId(byte[] machineId, int processId, int sequence, long timestamp, int random) {
      byte[] data = new byte[machineId.length + 4 + 4 + 8 + 4];
      int i = 0;
      System.arraycopy(machineId, 0, data, i, machineId.length);
      i += machineId.length;
      writeInt(data, i, processId);
      i += 4;
      writeInt(data, i, sequence);
      i += 4;
      writeLong(data, i, timestamp);
      i += 8;
      writeInt(data, i, random);
      i += 4;

      assert i == data.length;

      this.data = data;
      this.hashCode = Arrays.hashCode(data);
   }

   private static void writeInt(byte[] data, int i, int value) {
      if (PlatformDependent.isUnaligned()) {
         PlatformDependent.putInt(data, i, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Integer.reverseBytes(value));
      } else {
         data[i] = (byte)(value >>> 24);
         data[i + 1] = (byte)(value >>> 16);
         data[i + 2] = (byte)(value >>> 8);
         data[i + 3] = (byte)value;
      }
   }

   private static void writeLong(byte[] data, int i, long value) {
      if (PlatformDependent.isUnaligned()) {
         PlatformDependent.putLong(data, i, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Long.reverseBytes(value));
      } else {
         data[i] = (byte)(value >>> 56);
         data[i + 1] = (byte)(value >>> 48);
         data[i + 2] = (byte)(value >>> 40);
         data[i + 3] = (byte)(value >>> 32);
         data[i + 4] = (byte)(value >>> 24);
         data[i + 5] = (byte)(value >>> 16);
         data[i + 6] = (byte)(value >>> 8);
         data[i + 7] = (byte)value;
      }
   }

   @Override
   public String asShortText() {
      String shortValue = this.shortValue;
      if (shortValue == null) {
         this.shortValue = shortValue = ByteBufUtil.hexDump(this.data, this.data.length - 4, 4);
      }

      return shortValue;
   }

   @Override
   public String asLongText() {
      String longValue = this.longValue;
      if (longValue == null) {
         this.longValue = longValue = this.newLongValue();
      }

      return longValue;
   }

   private String newLongValue() {
      StringBuilder buf = new StringBuilder(2 * this.data.length + 5);
      int machineIdLen = this.data.length - 4 - 4 - 8 - 4;
      int i = 0;
      i = this.appendHexDumpField(buf, i, machineIdLen);
      i = this.appendHexDumpField(buf, i, 4);
      i = this.appendHexDumpField(buf, i, 4);
      i = this.appendHexDumpField(buf, i, 8);
      i = this.appendHexDumpField(buf, i, 4);

      assert i == this.data.length;

      return buf.substring(0, buf.length() - 1);
   }

   private int appendHexDumpField(StringBuilder buf, int i, int length) {
      buf.append(ByteBufUtil.hexDump(this.data, i, length));
      buf.append('-');
      return i + length;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   public int compareTo(ChannelId o) {
      if (this == o) {
         return 0;
      } else if (o instanceof DefaultChannelId) {
         byte[] otherData = ((DefaultChannelId)o).data;
         int len1 = this.data.length;
         int len2 = otherData.length;
         int len = Math.min(len1, len2);

         for (int k = 0; k < len; k++) {
            byte x = this.data[k];
            byte y = otherData[k];
            if (x != y) {
               return (x & 0xFF) - (y & 0xFF);
            }
         }

         return len1 - len2;
      } else {
         return this.asLongText().compareTo(o.asLongText());
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof DefaultChannelId)) {
         return false;
      } else {
         DefaultChannelId other = (DefaultChannelId)obj;
         return this.hashCode == other.hashCode && Arrays.equals(this.data, other.data);
      }
   }

   @Override
   public String toString() {
      return this.asShortText();
   }

   static {
      int processId = -1;
      String customProcessId = SystemPropertyUtil.get("io.netty.processId");
      if (customProcessId != null) {
         try {
            processId = Integer.parseInt(customProcessId);
         } catch (NumberFormatException var6) {
         }

         if (processId < 0) {
            processId = -1;
            logger.warn("-Dio.netty.processId: {} (malformed)", customProcessId);
         } else if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.processId: {} (user-set)", processId);
         }
      }

      if (processId < 0) {
         processId = defaultProcessId();
         if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.processId: {} (auto-detected)", processId);
         }
      }

      PROCESS_ID = processId;
      byte[] machineId = null;
      String customMachineId = SystemPropertyUtil.get("io.netty.machineId");
      if (customMachineId != null) {
         try {
            machineId = MacAddressUtil.parseMAC(customMachineId);
         } catch (Exception var5) {
            logger.warn("-Dio.netty.machineId: {} (malformed)", customMachineId, var5);
         }

         if (machineId != null) {
            logger.debug("-Dio.netty.machineId: {} (user-set)", customMachineId);
         }
      }

      if (machineId == null) {
         machineId = MacAddressUtil.defaultMachineId();
         if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.machineId: {} (auto-detected)", MacAddressUtil.formatAddress(machineId));
         }
      }

      MACHINE_ID = machineId;
   }
}
