package org.jline.reader.impl.history;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Collectors;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.impl.ReaderUtils;
import org.jline.utils.Log;

public class DefaultHistory implements History {
   public static final int DEFAULT_HISTORY_SIZE = 500;
   public static final int DEFAULT_HISTORY_FILE_SIZE = 10000;
   private final LinkedList<History.Entry> items = new LinkedList<>();
   private LineReader reader;
   private Map<String, DefaultHistory.HistoryFileData> historyFiles = new HashMap<>();
   private int offset = 0;
   private int index = 0;

   public DefaultHistory() {
   }

   public DefaultHistory(LineReader reader) {
      this.attach(reader);
   }

   private Path getPath() {
      Object obj = this.reader != null ? this.reader.getVariables().get("history-file") : null;
      if (obj instanceof Path) {
         return (Path)obj;
      } else if (obj instanceof File) {
         return ((File)obj).toPath();
      } else {
         return obj != null ? Paths.get(obj.toString()) : null;
      }
   }

   @Override
   public void attach(LineReader reader) {
      if (this.reader != reader) {
         this.reader = reader;

         try {
            this.load();
         } catch (IOException | IllegalArgumentException var3) {
            Log.warn("Failed to load history", var3);
         }
      }
   }

   @Override
   public void load() throws IOException {
      Path path = this.getPath();
      if (path != null) {
         try {
            if (Files.exists(path)) {
               Log.trace("Loading history from: ", path);
               this.internalClear();
               boolean hasErrors = false;
               BufferedReader reader = Files.newBufferedReader(path);

               try {
                  for (String line : reader.lines().collect(Collectors.toList())) {
                     try {
                        this.addHistoryLine(path, line);
                     } catch (IllegalArgumentException var9) {
                        Log.debug("Skipping invalid history line: " + line, var9);
                        hasErrors = true;
                     }
                  }
               } catch (Throwable var10) {
                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (Throwable var8) {
                        var10.addSuppressed(var8);
                     }
                  }

                  throw var10;
               }

               if (reader != null) {
                  reader.close();
               }

               this.setHistoryFileData(path, new DefaultHistory.HistoryFileData(this.items.size(), this.offset + this.items.size()));
               this.maybeResize();
               if (hasErrors) {
                  Log.info("History file contained errors, rewriting with valid entries");
                  this.write(path, false);
               }
            }
         } catch (IOException var11) {
            Log.debug("Failed to load history; clearing", var11);
            this.internalClear();
            throw var11;
         }
      }
   }

   @Override
   public void read(Path file, boolean checkDuplicates) throws IOException {
      Path path = file != null ? file : this.getPath();
      if (path != null) {
         try {
            if (Files.exists(path)) {
               Log.trace("Reading history from: ", path);
               boolean hasErrors = false;
               BufferedReader reader = Files.newBufferedReader(path);

               try {
                  for (String line : reader.lines().collect(Collectors.toList())) {
                     try {
                        this.addHistoryLine(path, line, checkDuplicates);
                     } catch (IllegalArgumentException var11) {
                        Log.debug("Skipping invalid history line: " + line, var11);
                        hasErrors = true;
                     }
                  }
               } catch (Throwable var12) {
                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (Throwable var10) {
                        var12.addSuppressed(var10);
                     }
                  }

                  throw var12;
               }

               if (reader != null) {
                  reader.close();
               }

               this.setHistoryFileData(path, new DefaultHistory.HistoryFileData(this.items.size(), this.offset + this.items.size()));
               this.maybeResize();
               if (hasErrors) {
                  Log.info("History file contained errors, rewriting with valid entries");
                  this.write(path, false);
               }
            }
         } catch (IOException var13) {
            Log.debug("Failed to read history; clearing", var13);
            this.internalClear();
            throw var13;
         }
      }
   }

   private String doHistoryFileDataKey(Path path) {
      return path != null ? path.toAbsolutePath().toString() : null;
   }

   private DefaultHistory.HistoryFileData getHistoryFileData(Path path) {
      String key = this.doHistoryFileDataKey(path);
      if (!this.historyFiles.containsKey(key)) {
         this.historyFiles.put(key, new DefaultHistory.HistoryFileData());
      }

      return this.historyFiles.get(key);
   }

   private void setHistoryFileData(Path path, DefaultHistory.HistoryFileData historyFileData) {
      this.historyFiles.put(this.doHistoryFileDataKey(path), historyFileData);
   }

   private boolean isLineReaderHistory(Path path) throws IOException {
      Path lrp = this.getPath();
      return lrp == null ? path == null : Files.isSameFile(lrp, path);
   }

   private void setLastLoaded(Path path, int lastloaded) {
      this.getHistoryFileData(path).setLastLoaded(lastloaded);
   }

   private void setEntriesInFile(Path path, int entriesInFile) {
      this.getHistoryFileData(path).setEntriesInFile(entriesInFile);
   }

   private void incEntriesInFile(Path path, int amount) {
      this.getHistoryFileData(path).incEntriesInFile(amount);
   }

   private int getLastLoaded(Path path) {
      return this.getHistoryFileData(path).getLastLoaded();
   }

   private int getEntriesInFile(Path path) {
      return this.getHistoryFileData(path).getEntriesInFile();
   }

   protected void addHistoryLine(Path path, String line) {
      this.addHistoryLine(path, line, false);
   }

   protected void addHistoryLine(Path path, String line, boolean checkDuplicates) {
      if (this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED)) {
         int idx = line.indexOf(58);
         String badHistoryFileSyntax = "Bad history file syntax! The history file `"
            + path
            + "` may be an older history: please remove it or use a different history file.";
         if (idx < 0) {
            throw new IllegalArgumentException(badHistoryFileSyntax);
         }

         Instant time;
         try {
            time = Instant.ofEpochMilli(Long.parseLong(line.substring(0, idx)));
         } catch (NumberFormatException | DateTimeException var8) {
            throw new IllegalArgumentException(badHistoryFileSyntax);
         }

         String unescaped = unescape(line.substring(idx + 1));
         this.internalAdd(time, unescaped, checkDuplicates);
      } else {
         this.internalAdd(Instant.now(), unescape(line), checkDuplicates);
      }
   }

   @Override
   public void purge() throws IOException {
      this.internalClear();
      Path path = this.getPath();
      if (path != null) {
         Log.trace("Purging history from: ", path);
         Files.deleteIfExists(path);
      }
   }

   @Override
   public void write(Path file, boolean incremental) throws IOException {
      Path path = file != null ? file : this.getPath();
      if (path != null && Files.exists(path)) {
         Files.deleteIfExists(path);
      }

      this.internalWrite(path, incremental ? this.getLastLoaded(path) : 0);
   }

   @Override
   public void append(Path file, boolean incremental) throws IOException {
      this.internalWrite(file != null ? file : this.getPath(), incremental ? this.getLastLoaded(file) : 0);
   }

   @Override
   public void save() throws IOException {
      this.internalWrite(this.getPath(), this.getLastLoaded(this.getPath()));
   }

   private void internalWrite(Path path, int from) throws IOException {
      if (path != null) {
         Log.trace("Saving history to: ", path);
         Path parent = path.toAbsolutePath().getParent();
         if (!Files.exists(parent)) {
            Files.createDirectories(parent);
         }

         BufferedWriter writer = Files.newBufferedWriter(path.toAbsolutePath(), StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

         try {
            for (History.Entry entry : this.items.subList(from, this.items.size())) {
               if (this.isPersistable(entry)) {
                  writer.append(this.format(entry));
               }
            }
         } catch (Throwable var8) {
            if (writer != null) {
               try {
                  writer.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (writer != null) {
            writer.close();
         }

         this.incEntriesInFile(path, this.items.size() - from);
         int max = ReaderUtils.getInt(this.reader, "history-file-size", 10000);
         if (this.getEntriesInFile(path) > max + max / 4) {
            this.trimHistory(path, max);
         }
      }

      this.setLastLoaded(path, this.items.size());
   }

   protected void trimHistory(Path path, int max) throws IOException {
      Log.trace("Trimming history path: ", path);
      LinkedList<History.Entry> allItems = new LinkedList<>();
      BufferedReader historyFileReader = Files.newBufferedReader(path);

      try {
         for (String l : historyFileReader.lines().collect(Collectors.toList())) {
            try {
               if (this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED)) {
                  int idx = l.indexOf(58);
                  if (idx < 0) {
                     Log.debug("Skipping invalid history line: " + l);
                  } else {
                     try {
                        Instant time = Instant.ofEpochMilli(Long.parseLong(l.substring(0, idx)));
                        String line = unescape(l.substring(idx + 1));
                        allItems.add(this.createEntry(allItems.size(), time, line));
                     } catch (NumberFormatException | DateTimeException var13) {
                        Log.debug("Skipping invalid history timestamp: " + l);
                     }
                  }
               } else {
                  allItems.add(this.createEntry(allItems.size(), Instant.now(), unescape(l)));
               }
            } catch (Exception var14) {
               Log.debug("Skipping invalid history line: " + l, var14);
            }
         }
      } catch (Throwable var16) {
         if (historyFileReader != null) {
            try {
               historyFileReader.close();
            } catch (Throwable var12) {
               var16.addSuppressed(var12);
            }
         }

         throw var16;
      }

      if (historyFileReader != null) {
         historyFileReader.close();
      }

      List<History.Entry> trimmedItems = doTrimHistory(allItems, max);
      Path temp = Files.createTempFile(path.toAbsolutePath().getParent(), path.getFileName().toString(), ".tmp");
      BufferedWriter writer = Files.newBufferedWriter(temp, StandardOpenOption.WRITE);

      try {
         for (History.Entry entry : trimmedItems) {
            writer.append(this.format(entry));
         }
      } catch (Throwable var15) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable var11) {
               var15.addSuppressed(var11);
            }
         }

         throw var15;
      }

      if (writer != null) {
         writer.close();
      }

      Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
      if (this.isLineReaderHistory(path)) {
         this.internalClear();
         this.offset = trimmedItems.get(0).index();
         this.items.addAll(trimmedItems);
         this.setHistoryFileData(path, new DefaultHistory.HistoryFileData(this.items.size(), this.items.size()));
      } else {
         this.setEntriesInFile(path, allItems.size());
      }

      this.maybeResize();
   }

   protected DefaultHistory.EntryImpl createEntry(int index, Instant time, String line) {
      return new DefaultHistory.EntryImpl(index, time, line);
   }

   private void internalClear() {
      this.offset = 0;
      this.index = 0;
      this.historyFiles = new HashMap<>();
      this.items.clear();
   }

   static List<History.Entry> doTrimHistory(List<History.Entry> allItems, int max) {
      for (int idx = 0; idx < allItems.size(); idx++) {
         int ridx = allItems.size() - idx - 1;
         String line = allItems.get(ridx).line().trim();
         ListIterator<History.Entry> iterator = allItems.listIterator(ridx);

         while (iterator.hasPrevious()) {
            String l = iterator.previous().line();
            if (line.equals(l.trim())) {
               iterator.remove();
            }
         }
      }

      while (allItems.size() > max) {
         allItems.remove(0);
      }

      int index = allItems.get(allItems.size() - 1).index() - allItems.size() + 1;
      List<History.Entry> out = new ArrayList<>();

      for (History.Entry e : allItems) {
         out.add(new DefaultHistory.EntryImpl(index++, e.time(), e.line()));
      }

      return out;
   }

   @Override
   public int size() {
      return this.items.size();
   }

   @Override
   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Override
   public int index() {
      return this.offset + this.index;
   }

   @Override
   public int first() {
      return this.offset;
   }

   @Override
   public int last() {
      return this.offset + this.items.size() - 1;
   }

   private String format(History.Entry entry) {
      return this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED)
         ? entry.time().toEpochMilli() + ":" + escape(entry.line()) + "\n"
         : escape(entry.line()) + "\n";
   }

   @Override
   public String get(int index) {
      int idx = index - this.offset;
      if (idx < this.items.size() && idx >= 0) {
         return this.items.get(idx).line();
      } else {
         throw new IllegalArgumentException("IndexOutOfBounds: Index:" + idx + ", Size:" + this.items.size());
      }
   }

   @Override
   public void add(Instant time, String line) {
      Objects.requireNonNull(time);
      Objects.requireNonNull(line);
      if (!ReaderUtils.getBoolean(this.reader, "disable-history", false)) {
         if (!ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_IGNORE_SPACE) || !line.startsWith(" ")) {
            if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_REDUCE_BLANKS)) {
               line = line.trim();
            }

            if (!ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_IGNORE_DUPS) || this.items.isEmpty() || !line.equals(this.items.getLast().line())) {
               if (!this.matchPatterns(ReaderUtils.getString(this.reader, "history-ignore", ""), line)) {
                  this.internalAdd(time, line);
                  if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_INCREMENTAL)) {
                     try {
                        this.save();
                     } catch (IOException var4) {
                        Log.warn("Failed to save history", var4);
                     }
                  }
               }
            }
         }
      }
   }

   protected boolean matchPatterns(String patterns, String line) {
      if (patterns != null && !patterns.isEmpty()) {
         StringBuilder sb = new StringBuilder();

         for (int i = 0; i < patterns.length(); i++) {
            char ch = patterns.charAt(i);
            if (ch == '\\') {
               ch = patterns.charAt(++i);
               sb.append(ch);
            } else if (ch == ':') {
               sb.append('|');
            } else if (ch == '*') {
               sb.append('.').append('*');
            } else {
               sb.append(ch);
            }
         }

         return line.matches(sb.toString());
      } else {
         return false;
      }
   }

   protected void internalAdd(Instant time, String line) {
      this.internalAdd(time, line, false);
   }

   protected void internalAdd(Instant time, String line, boolean checkDuplicates) {
      History.Entry entry = new DefaultHistory.EntryImpl(this.offset + this.items.size(), time, line);
      if (checkDuplicates) {
         for (History.Entry e : this.items) {
            if (e.line().trim().equals(line.trim())) {
               return;
            }
         }
      }

      this.items.add(entry);
      this.maybeResize();
   }

   private void maybeResize() {
      while (this.size() > ReaderUtils.getInt(this.reader, "history-size", 500)) {
         this.items.removeFirst();

         for (DefaultHistory.HistoryFileData hfd : this.historyFiles.values()) {
            hfd.decLastLoaded();
         }

         this.offset++;
      }

      this.index = this.size();
   }

   @Override
   public ListIterator<History.Entry> iterator(int index) {
      return this.items.listIterator(index - this.offset);
   }

   @Override
   public Spliterator<History.Entry> spliterator() {
      return this.items.spliterator();
   }

   @Override
   public void resetIndex() {
      this.index = Math.min(this.index, this.items.size());
   }

   @Override
   public boolean moveToLast() {
      int lastEntry = this.size() - 1;
      if (lastEntry >= 0 && lastEntry != this.index) {
         this.index = this.size() - 1;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean moveTo(int index) {
      index -= this.offset;
      if (index >= 0 && index < this.size()) {
         this.index = index;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean moveToFirst() {
      if (this.size() > 0 && this.index != 0) {
         this.index = 0;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void moveToEnd() {
      this.index = this.size();
   }

   @Override
   public String current() {
      return this.index >= this.size() ? "" : this.items.get(this.index).line();
   }

   @Override
   public boolean previous() {
      if (this.index <= 0) {
         return false;
      } else {
         this.index--;
         return true;
      }
   }

   @Override
   public boolean next() {
      if (this.index >= this.size()) {
         return false;
      } else {
         this.index++;
         return true;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();

      for (History.Entry e : this) {
         sb.append(e.toString()).append("\n");
      }

      return sb.toString();
   }

   private static String escape(String s) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < s.length(); i++) {
         char ch = s.charAt(i);
         switch (ch) {
            case '\n':
               sb.append('\\');
               sb.append('n');
               break;
            case '\r':
               sb.append('\\');
               sb.append('r');
               break;
            case '\\':
               sb.append('\\');
               sb.append('\\');
               break;
            default:
               sb.append(ch);
         }
      }

      return sb.toString();
   }

   static String unescape(String s) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < s.length(); i++) {
         char ch = s.charAt(i);
         switch (ch) {
            case '\\':
               ch = s.charAt(++i);
               if (ch == 'n') {
                  sb.append('\n');
               } else if (ch == 'r') {
                  sb.append('\r');
               } else {
                  sb.append(ch);
               }
               break;
            default:
               sb.append(ch);
         }
      }

      return sb.toString();
   }

   protected static class EntryImpl implements History.Entry {
      private final int index;
      private final Instant time;
      private final String line;

      public EntryImpl(int index, Instant time, String line) {
         this.index = index;
         this.time = time;
         this.line = line;
      }

      @Override
      public int index() {
         return this.index;
      }

      @Override
      public Instant time() {
         return this.time;
      }

      @Override
      public String line() {
         return this.line;
      }

      @Override
      public String toString() {
         return String.format("%d: %s", this.index, this.line);
      }
   }

   private static class HistoryFileData {
      private int lastLoaded = 0;
      private int entriesInFile = 0;

      public HistoryFileData() {
      }

      public HistoryFileData(int lastLoaded, int entriesInFile) {
         this.lastLoaded = lastLoaded;
         this.entriesInFile = entriesInFile;
      }

      public int getLastLoaded() {
         return this.lastLoaded;
      }

      public void setLastLoaded(int lastLoaded) {
         this.lastLoaded = lastLoaded;
      }

      public void decLastLoaded() {
         this.lastLoaded--;
         if (this.lastLoaded < 0) {
            this.lastLoaded = 0;
         }
      }

      public int getEntriesInFile() {
         return this.entriesInFile;
      }

      public void setEntriesInFile(int entriesInFile) {
         this.entriesInFile = entriesInFile;
      }

      public void incEntriesInFile(int amount) {
         this.entriesInFile += amount;
      }
   }
}
