package org.jline.reader.impl.completer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

@Deprecated
public class FileNameCompleter implements Completer {
   @Override
   public void complete(LineReader reader, ParsedLine commandLine, List<Candidate> candidates) {
      assert commandLine != null;

      assert candidates != null;

      String buffer = commandLine.word().substring(0, commandLine.wordCursor());
      String sep = this.getUserDir().getFileSystem().getSeparator();
      int lastSep = buffer.lastIndexOf(sep);
      Path current;
      String curBuf;
      if (lastSep >= 0) {
         curBuf = buffer.substring(0, lastSep + 1);
         if (curBuf.startsWith("~")) {
            if (curBuf.startsWith("~" + sep)) {
               current = this.getUserHome().resolve(curBuf.substring(2));
            } else {
               current = this.getUserHome().getParent().resolve(curBuf.substring(1));
            }
         } else {
            current = this.getUserDir().resolve(curBuf);
         }
      } else {
         curBuf = "";
         current = this.getUserDir();
      }

      try {
         DirectoryStream<Path> directoryStream = Files.newDirectoryStream(current, this::accept);

         try {
            directoryStream.forEach(
               p -> {
                  String value = curBuf + p.getFileName().toString();
                  if (Files.isDirectory(p)) {
                     candidates.add(
                        new Candidate(
                           value + (reader.isSet(LineReader.Option.AUTO_PARAM_SLASH) ? sep : ""),
                           this.getDisplay(reader.getTerminal(), p),
                           null,
                           null,
                           reader.isSet(LineReader.Option.AUTO_REMOVE_SLASH) ? sep : null,
                           null,
                           false
                        )
                     );
                  } else {
                     candidates.add(new Candidate(value, this.getDisplay(reader.getTerminal(), p), null, null, null, null, true));
                  }
               }
            );
         } catch (Throwable var13) {
            if (directoryStream != null) {
               try {
                  directoryStream.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (directoryStream != null) {
            directoryStream.close();
         }
      } catch (IOException var14) {
      }
   }

   protected boolean accept(Path path) {
      try {
         return !Files.isHidden(path);
      } catch (IOException var3) {
         return false;
      }
   }

   protected Path getUserDir() {
      return Paths.get(System.getProperty("user.dir"));
   }

   protected Path getUserHome() {
      return Paths.get(System.getProperty("user.home"));
   }

   protected String getDisplay(Terminal terminal, Path p) {
      String name = p.getFileName().toString();
      if (Files.isDirectory(p)) {
         AttributedStringBuilder sb = new AttributedStringBuilder();
         sb.styled(AttributedStyle.BOLD.foreground(1), name);
         sb.append("/");
         name = sb.toAnsi(terminal);
      } else if (Files.isSymbolicLink(p)) {
         AttributedStringBuilder sb = new AttributedStringBuilder();
         sb.styled(AttributedStyle.BOLD.foreground(1), name);
         sb.append("@");
         name = sb.toAnsi(terminal);
      }

      return name;
   }
}
