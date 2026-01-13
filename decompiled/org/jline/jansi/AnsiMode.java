package org.jline.jansi;

public enum AnsiMode {
   Strip("Strip all ansi sequences"),
   Default("Print ansi sequences if the stream is a terminal"),
   Force("Always print ansi sequences, even if the stream is redirected");

   private final String description;

   private AnsiMode(String description) {
      this.description = description;
   }

   String getDescription() {
      return this.description;
   }
}
