package org.jline.builtins;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;

public class TTop {
   public static final String STAT_UPTIME = "uptime";
   public static final String STAT_TID = "tid";
   public static final String STAT_NAME = "name";
   public static final String STAT_STATE = "state";
   public static final String STAT_BLOCKED_TIME = "blocked_time";
   public static final String STAT_BLOCKED_COUNT = "blocked_count";
   public static final String STAT_WAITED_TIME = "waited_time";
   public static final String STAT_WAITED_COUNT = "waited_count";
   public static final String STAT_LOCK_NAME = "lock_name";
   public static final String STAT_LOCK_OWNER_ID = "lock_owner_id";
   public static final String STAT_LOCK_OWNER_NAME = "lock_owner_name";
   public static final String STAT_USER_TIME = "user_time";
   public static final String STAT_USER_TIME_PERC = "user_time_perc";
   public static final String STAT_CPU_TIME = "cpu_time";
   public static final String STAT_CPU_TIME_PERC = "cpu_time_perc";
   public List<String> sort;
   public long delay;
   public List<String> stats;
   public int nthreads;
   private final Map<String, TTop.Column> columns = new LinkedHashMap<>();
   private final Terminal terminal;
   private final Display display;
   private final BindingReader bindingReader;
   private final KeyMap<TTop.Operation> keys;
   private final Size size = new Size();
   private Comparator<Map<String, Comparable<?>>> comparator;
   private Map<Long, Map<String, Object>> previous = new HashMap<>();
   private Map<Long, Map<String, Long>> changes = new HashMap<>();
   private Map<String, Integer> widths = new HashMap<>();

   public static void ttop(Terminal terminal, PrintStream out, PrintStream err, String[] argv) throws Exception {
      String[] usage = new String[]{
         "ttop -  display and update sorted information about threads",
         "Usage: ttop [OPTIONS]",
         "  -? --help                    Show help",
         "  -o --order=ORDER             Comma separated list of sorting keys",
         "  -t --stats=STATS             Comma separated list of stats to display",
         "  -s --seconds=SECONDS         Delay between updates in seconds",
         "  -m --millis=MILLIS           Delay between updates in milliseconds",
         "  -n --nthreads=NTHREADS       Only display up to NTHREADS threads"
      };
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         TTop ttop = new TTop(terminal);
         ttop.sort = opt.isSet("order") ? Arrays.asList(opt.get("order").split(",")) : null;
         ttop.delay = opt.isSet("seconds") ? opt.getNumber("seconds") * 1000 : ttop.delay;
         ttop.delay = opt.isSet("millis") ? opt.getNumber("millis") : ttop.delay;
         ttop.stats = opt.isSet("stats") ? Arrays.asList(opt.get("stats").split(",")) : null;
         ttop.nthreads = opt.isSet("nthreads") ? opt.getNumber("nthreads") : ttop.nthreads;
         ttop.run();
      }
   }

   public TTop(Terminal terminal) {
      this.terminal = terminal;
      this.display = new Display(terminal, true);
      this.bindingReader = new BindingReader(terminal.reader());
      DecimalFormatSymbols dfs = new DecimalFormatSymbols();
      dfs.setDecimalSeparator('.');
      DecimalFormat perc = new DecimalFormat("0.00%", dfs);
      this.register("tid", TTop.Align.Right, "TID", o -> String.format("%3d", (Long)o));
      this.register("name", TTop.Align.Left, "NAME", padcut(40));
      this.register("state", TTop.Align.Left, "STATE", o -> o.toString().toLowerCase());
      this.register("blocked_time", TTop.Align.Right, "T-BLOCKED", o -> millis((Long)o));
      this.register("blocked_count", TTop.Align.Right, "#-BLOCKED", Object::toString);
      this.register("waited_time", TTop.Align.Right, "T-WAITED", o -> millis((Long)o));
      this.register("waited_count", TTop.Align.Right, "#-WAITED", Object::toString);
      this.register("lock_name", TTop.Align.Left, "LOCK-NAME", Object::toString);
      this.register("lock_owner_id", TTop.Align.Right, "LOCK-OWNER-ID", id -> (Long)id >= 0L ? id.toString() : "");
      this.register("lock_owner_name", TTop.Align.Left, "LOCK-OWNER-NAME", name -> name != null ? name.toString() : "");
      this.register("user_time", TTop.Align.Right, "T-USR", o -> nanos((Long)o));
      this.register("cpu_time", TTop.Align.Right, "T-CPU", o -> nanos((Long)o));
      this.register("user_time_perc", TTop.Align.Right, "%-USR", perc::format);
      this.register("cpu_time_perc", TTop.Align.Right, "%-CPU", perc::format);
      this.keys = new KeyMap<>();
      this.bindKeys(this.keys);
   }

   public KeyMap<TTop.Operation> getKeys() {
      return this.keys;
   }

   public void run() throws IOException, InterruptedException {
      this.comparator = this.buildComparator(this.sort);
      this.delay = this.delay > 0L ? Math.max(this.delay, 100L) : 1000L;
      if (this.stats == null || this.stats.isEmpty()) {
         this.stats = new ArrayList<>(Arrays.asList("tid", "name", "state", "cpu_time", "lock_owner_id"));
      }

      Boolean isThreadContentionMonitoringEnabled = null;
      ThreadMXBean threadsBean = ManagementFactory.getThreadMXBean();
      if (this.stats.contains("blocked_time")
         || this.stats.contains("blocked_count")
         || this.stats.contains("waited_time")
         || this.stats.contains("waited_count")) {
         if (threadsBean.isThreadContentionMonitoringSupported()) {
            isThreadContentionMonitoringEnabled = threadsBean.isThreadContentionMonitoringEnabled();
            if (!isThreadContentionMonitoringEnabled) {
               threadsBean.setThreadContentionMonitoringEnabled(true);
            }
         } else {
            this.stats.removeAll(Arrays.asList("blocked_time", "blocked_count", "waited_time", "waited_count"));
         }
      }

      Boolean isThreadCpuTimeEnabled = null;
      if (this.stats.contains("user_time") || this.stats.contains("cpu_time")) {
         if (threadsBean.isThreadCpuTimeSupported()) {
            isThreadCpuTimeEnabled = threadsBean.isThreadCpuTimeEnabled();
            if (!isThreadCpuTimeEnabled) {
               threadsBean.setThreadCpuTimeEnabled(true);
            }
         } else {
            this.stats.removeAll(Arrays.asList("user_time", "cpu_time"));
         }
      }

      this.size.copy(this.terminal.getSize());
      Terminal.SignalHandler prevHandler = this.terminal.handle(Terminal.Signal.WINCH, this::handle);
      Attributes attr = this.terminal.enterRawMode();

      try {
         if (!this.terminal.puts(InfoCmp.Capability.enter_ca_mode)) {
            this.terminal.puts(InfoCmp.Capability.clear_screen);
         }

         this.terminal.puts(InfoCmp.Capability.keypad_xmit);
         this.terminal.puts(InfoCmp.Capability.cursor_invisible);
         this.terminal.writer().flush();
         long t0 = System.currentTimeMillis();

         TTop.Operation op;
         do {
            this.display();
            this.checkInterrupted();
            op = null;
            long delta = ((System.currentTimeMillis() - t0) / this.delay + 1L) * this.delay + t0 - System.currentTimeMillis();
            int ch = this.bindingReader.peekCharacter(delta);
            if (ch == -1) {
               op = TTop.Operation.EXIT;
            } else if (ch != -2) {
               op = this.bindingReader.readBinding(this.keys, null, false);
            }

            if (op != null) {
               switch (op) {
                  case INCREASE_DELAY:
                     this.delay *= 2L;
                     t0 = System.currentTimeMillis();
                     break;
                  case DECREASE_DELAY:
                     this.delay = Math.max(this.delay / 2L, 16L);
                     t0 = System.currentTimeMillis();
                  case HELP:
                  case EXIT:
                  default:
                     break;
                  case CLEAR:
                     this.display.clear();
                     break;
                  case REVERSE:
                     this.comparator = this.comparator.reversed();
               }
            }
         } while (op != TTop.Operation.EXIT);

         return;
      } catch (InterruptedException var16) {
         return;
      } catch (Error var17) {
         Log.info("Error: ", var17);
      } finally {
         this.terminal.setAttributes(attr);
         if (prevHandler != null) {
            this.terminal.handle(Terminal.Signal.WINCH, prevHandler);
         }

         if (!this.terminal.puts(InfoCmp.Capability.exit_ca_mode)) {
            this.terminal.puts(InfoCmp.Capability.clear_screen);
         }

         this.terminal.puts(InfoCmp.Capability.keypad_local);
         this.terminal.puts(InfoCmp.Capability.cursor_visible);
         this.terminal.writer().flush();
         if (isThreadContentionMonitoringEnabled != null) {
            threadsBean.setThreadContentionMonitoringEnabled(isThreadContentionMonitoringEnabled);
         }

         if (isThreadCpuTimeEnabled != null) {
            threadsBean.setThreadCpuTimeEnabled(isThreadCpuTimeEnabled);
         }
      }
   }

   private void handle(Terminal.Signal signal) {
      int prevw = this.size.getColumns();
      this.size.copy(this.terminal.getSize());

      try {
         if (this.size.getColumns() < prevw) {
            this.display.clear();
         }

         this.display();
      } catch (IOException var4) {
      }
   }

   private List<Map<String, Comparable<?>>> infos() {
      long ctime = ManagementFactory.getRuntimeMXBean().getUptime();
      Long ptime = (Long)this.previous.computeIfAbsent(-1L, id -> new HashMap<>()).put("uptime", ctime);
      long delta = ptime != null ? ctime - ptime : 0L;
      ThreadMXBean threadsBean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] infos = threadsBean.dumpAllThreads(false, false);
      List<Map<String, Comparable<?>>> threads = new ArrayList<>();

      for (ThreadInfo ti : infos) {
         Map<String, Comparable<?>> t = new HashMap<>();
         t.put("tid", ti.getThreadId());
         t.put("name", ti.getThreadName());
         t.put("state", ti.getThreadState());
         if (threadsBean.isThreadContentionMonitoringEnabled()) {
            t.put("blocked_time", ti.getBlockedTime());
            t.put("blocked_count", ti.getBlockedCount());
            t.put("waited_time", ti.getWaitedTime());
            t.put("waited_count", ti.getWaitedCount());
         }

         t.put("lock_name", ti.getLockName());
         t.put("lock_owner_id", ti.getLockOwnerId());
         t.put("lock_owner_name", ti.getLockOwnerName());
         if (threadsBean.isThreadCpuTimeSupported() && threadsBean.isThreadCpuTimeEnabled()) {
            long tid = ti.getThreadId();
            long t1 = threadsBean.getThreadCpuTime(tid);
            long t0 = (Long)this.previous.computeIfAbsent(tid, id -> new HashMap<>()).getOrDefault("cpu_time", t1);
            t.put("cpu_time", t1);
            t.put("cpu_time_perc", delta != 0L ? (t1 - t0) / (delta * 1000000.0) : 0.0);
            t1 = threadsBean.getThreadUserTime(tid);
            t0 = (Long)this.previous.computeIfAbsent(tid, id -> new HashMap<>()).getOrDefault("user_time", t1);
            t.put("user_time", t1);
            t.put("user_time_perc", delta != 0L ? (t1 - t0) / (delta * 1000000.0) : 0.0);
         }

         threads.add(t);
      }

      return threads;
   }

   private void align(AttributedStringBuilder sb, String val, int width, TTop.Align align) {
      if (align == TTop.Align.Left) {
         sb.append(val);

         for (int i = 0; i < width - val.length(); i++) {
            sb.append(' ');
         }
      } else {
         for (int i = 0; i < width - val.length(); i++) {
            sb.append(' ');
         }

         sb.append(val);
      }
   }

   private synchronized void display() throws IOException {
      long now = System.currentTimeMillis();
      this.display.resize(this.size.getRows(), this.size.getColumns());
      List<AttributedString> lines = new ArrayList<>();
      AttributedStringBuilder sb = new AttributedStringBuilder(this.size.getColumns());
      sb.style(sb.style().bold());
      sb.append("ttop");
      sb.style(sb.style().boldOff());
      sb.append(" - ");
      sb.append(String.format("%8tT", new Date()));
      sb.append(".");
      OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
      String osinfo = "OS: " + os.getName() + " " + os.getVersion() + ", " + os.getArch() + ", " + os.getAvailableProcessors() + " cpus.";
      if (sb.length() + 1 + osinfo.length() < this.size.getColumns()) {
         sb.append(" ");
      } else {
         lines.add(sb.toAttributedString());
         sb.setLength(0);
      }

      sb.append(osinfo);
      ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();
      String clsinfo = "Classes: "
         + cl.getLoadedClassCount()
         + " loaded, "
         + cl.getUnloadedClassCount()
         + " unloaded, "
         + cl.getTotalLoadedClassCount()
         + " loaded total.";
      if (sb.length() + 1 + clsinfo.length() < this.size.getColumns()) {
         sb.append(" ");
      } else {
         lines.add(sb.toAttributedString());
         sb.setLength(0);
      }

      sb.append(clsinfo);
      ThreadMXBean th = ManagementFactory.getThreadMXBean();
      String thinfo = "Threads: " + th.getThreadCount() + ", peak: " + th.getPeakThreadCount() + ", started: " + th.getTotalStartedThreadCount() + ".";
      if (sb.length() + 1 + thinfo.length() < this.size.getColumns()) {
         sb.append(" ");
      } else {
         lines.add(sb.toAttributedString());
         sb.setLength(0);
      }

      sb.append(thinfo);
      MemoryMXBean me = ManagementFactory.getMemoryMXBean();
      String meinfo = "Memory: heap: "
         + memory(me.getHeapMemoryUsage().getUsed(), me.getHeapMemoryUsage().getMax())
         + ", non heap: "
         + memory(me.getNonHeapMemoryUsage().getUsed(), me.getNonHeapMemoryUsage().getMax())
         + ".";
      if (sb.length() + 1 + meinfo.length() < this.size.getColumns()) {
         sb.append(" ");
      } else {
         lines.add(sb.toAttributedString());
         sb.setLength(0);
      }

      sb.append(meinfo);
      StringBuilder sbc = new StringBuilder();
      sbc.append("GC: ");
      boolean first = true;

      for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
         if (first) {
            first = false;
         } else {
            sbc.append(", ");
         }

         long count = gc.getCollectionCount();
         long time = gc.getCollectionTime();
         sbc.append(gc.getName())
            .append(": ")
            .append(count)
            .append(" col. / ")
            .append(String.format("%d", time / 1000L))
            .append(".")
            .append(String.format("%03d", time % 1000L))
            .append(" s");
      }

      sbc.append(".");
      if (sb.length() + 1 + sbc.length() < this.size.getColumns()) {
         sb.append(" ");
      } else {
         lines.add(sb.toAttributedString());
         sb.setLength(0);
      }

      sb.append(sbc);
      lines.add(sb.toAttributedString());
      sb.setLength(0);
      lines.add(sb.toAttributedString());
      List<Map<String, Comparable<?>>> threads = this.infos();
      Collections.sort(threads, this.comparator);
      int nb = Math.min(this.size.getRows() - lines.size() - 2, this.nthreads > 0 ? this.nthreads : threads.size());
      List<Map<String, String>> values = threads.subList(0, nb)
         .stream()
         .map(threadx -> this.stats.stream().collect(Collectors.toMap(Function.identity(), keyx -> this.columns.get(keyx).format.apply(threadx.get(keyx)))))
         .collect(Collectors.toList());

      for (String key : this.stats) {
         int width = values.stream().mapToInt(map -> map.get(key).length()).max().orElse(0);
         this.widths.put(key, Math.max(this.columns.get(key).header.length(), Math.max(width, this.widths.getOrDefault(key, 0))));
      }

      List<String> cstats;
      if (this.widths.values().stream().mapToInt(Integer::intValue).sum() + this.stats.size() - 1 < this.size.getColumns()) {
         cstats = this.stats;
      } else {
         cstats = new ArrayList<>();
         int sz = 0;

         for (String stat : this.stats) {
            int nsz = sz;
            if (sz > 0) {
               nsz = sz + 1;
            }

            nsz += this.widths.get(stat);
            if (nsz >= this.size.getColumns()) {
               break;
            }

            sz = nsz;
            cstats.add(stat);
         }
      }

      for (String key : cstats) {
         if (sb.length() > 0) {
            sb.append(" ");
         }

         TTop.Column col = this.columns.get(key);
         this.align(sb, col.header, this.widths.get(key), col.align);
      }

      lines.add(sb.toAttributedString());
      sb.setLength(0);

      for (int i = 0; i < nb; i++) {
         Map<String, Comparable<?>> thread = threads.get(i);
         long tid = (Long)thread.get("tid");

         for (String key : cstats) {
            if (sb.length() > 0) {
               sb.append(" ");
            }

            Object cur = thread.get(key);
            Object prv = this.previous.computeIfAbsent(tid, id -> new HashMap<>()).put(key, cur);
            long last;
            if (prv != null && !prv.equals(cur)) {
               this.changes.computeIfAbsent(tid, id -> new HashMap<>()).put(key, now);
               last = now;
            } else {
               last = this.changes.computeIfAbsent(tid, id -> new HashMap<>()).getOrDefault(key, 0L);
            }

            long fade = this.delay * 24L;
            if (now - last < fade) {
               int r = (int)((now - last) / (fade / 24L));
               sb.style(sb.style().foreground(255 - r).background(9));
            }

            this.align(sb, values.get(i).get(key), this.widths.get(key), this.columns.get(key).align);
            sb.style(sb.style().backgroundOff().foregroundOff());
         }

         lines.add(sb.toAttributedString());
         sb.setLength(0);
      }

      this.display.update(lines, 0);
   }

   private Comparator<Map<String, Comparable<?>>> buildComparator(List<String> sort) {
      if (sort == null || sort.isEmpty()) {
         sort = Collections.singletonList("tid");
      }

      Comparator<Map<String, Comparable<?>>> comparator = null;

      for (String key : sort) {
         String fkey;
         boolean asc;
         if (key.startsWith("+")) {
            fkey = key.substring(1);
            asc = true;
         } else if (key.startsWith("-")) {
            fkey = key.substring(1);
            asc = false;
         } else {
            fkey = key;
            asc = true;
         }

         if (!this.columns.containsKey(fkey)) {
            throw new IllegalArgumentException("Unsupported sort key: " + fkey);
         }

         Comparator<Map<String, Comparable<?>>> comp = Comparator.comparing(m -> m.get(fkey));
         if (asc) {
            comp = comp.reversed();
         }

         if (comparator != null) {
            comparator = comparator.thenComparing(comp);
         } else {
            comparator = comp;
         }
      }

      return comparator;
   }

   private void register(String name, TTop.Align align, String header, Function<Object, String> format) {
      this.columns.put(name, new TTop.Column(name, align, header, format));
   }

   private static String nanos(long nanos) {
      return millis(nanos / 1000000L);
   }

   private static String millis(long millis) {
      long secs = millis / 1000L;
      millis %= 1000L;
      long mins = secs / 60L;
      secs %= 60L;
      long hours = mins / 60L;
      mins %= 60L;
      if (hours > 0L) {
         return String.format("%d:%02d:%02d.%03d", hours, mins, secs, millis);
      } else {
         return mins > 0L ? String.format("%d:%02d.%03d", mins, secs, millis) : String.format("%d.%03d", secs, millis);
      }
   }

   private static Function<Object, String> padcut(int nb) {
      return o -> padcut(o.toString(), nb);
   }

   private static String padcut(String str, int nb) {
      if (str.length() > nb) {
         return str.substring(0, nb - 3) + "...";
      } else {
         StringBuilder sb = new StringBuilder(nb);
         sb.append(str);

         while (sb.length() < nb) {
            sb.append(' ');
         }

         return sb.toString();
      }
   }

   private static String memory(long cur, long max) {
      if (max <= 0L) {
         return humanReadableByteCount(cur, false);
      } else {
         String smax = humanReadableByteCount(max, false);
         String cmax = humanReadableByteCount(cur, false);
         StringBuilder sb = new StringBuilder(smax.length() * 2 + 3);

         for (int i = cmax.length(); i < smax.length(); i++) {
            sb.append(' ');
         }

         sb.append(cmax).append(" / ").append(smax);
         return sb.toString();
      }
   }

   private static String humanReadableByteCount(long bytes, boolean si) {
      int unit = si ? 1000 : 1024;
      if (bytes < 1024L) {
         return bytes + " B";
      } else {
         int exp = (int)(Math.log(bytes) / Math.log(1024.0));
         String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
         return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
      }
   }

   private void checkInterrupted() throws InterruptedException {
      Thread.yield();
      if (Thread.currentThread().isInterrupted()) {
         throw new InterruptedException();
      }
   }

   private void bindKeys(KeyMap<TTop.Operation> map) {
      map.bind(TTop.Operation.HELP, "h", "?");
      map.bind(TTop.Operation.EXIT, "q", ":q", "Q", ":Q", "ZZ");
      map.bind(TTop.Operation.INCREASE_DELAY, "+");
      map.bind(TTop.Operation.DECREASE_DELAY, "-");
      map.bind(TTop.Operation.CLEAR, KeyMap.ctrl('L'));
      map.bind(TTop.Operation.REVERSE, "r");
   }

   public static enum Align {
      Left,
      Right;
   }

   private static class Column {
      final String name;
      final TTop.Align align;
      final String header;
      final Function<Object, String> format;

      Column(String name, TTop.Align align, String header, Function<Object, String> format) {
         this.name = name;
         this.align = align;
         this.header = header;
         this.format = format;
      }
   }

   public static enum Operation {
      INCREASE_DELAY,
      DECREASE_DELAY,
      HELP,
      EXIT,
      CLEAR,
      REVERSE;
   }
}
