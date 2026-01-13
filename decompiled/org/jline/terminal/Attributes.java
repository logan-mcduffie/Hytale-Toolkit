package org.jline.terminal;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Attributes {
   final EnumSet<Attributes.InputFlag> iflag = EnumSet.noneOf(Attributes.InputFlag.class);
   final EnumSet<Attributes.OutputFlag> oflag = EnumSet.noneOf(Attributes.OutputFlag.class);
   final EnumSet<Attributes.ControlFlag> cflag = EnumSet.noneOf(Attributes.ControlFlag.class);
   final EnumSet<Attributes.LocalFlag> lflag = EnumSet.noneOf(Attributes.LocalFlag.class);
   final EnumMap<Attributes.ControlChar, Integer> cchars = new EnumMap<>(Attributes.ControlChar.class);

   public Attributes() {
   }

   public Attributes(Attributes attr) {
      this.copy(attr);
   }

   public EnumSet<Attributes.InputFlag> getInputFlags() {
      return this.iflag;
   }

   public void setInputFlags(EnumSet<Attributes.InputFlag> flags) {
      this.iflag.clear();
      this.iflag.addAll(flags);
   }

   public boolean getInputFlag(Attributes.InputFlag flag) {
      return this.iflag.contains(flag);
   }

   public void setInputFlags(EnumSet<Attributes.InputFlag> flags, boolean value) {
      if (value) {
         this.iflag.addAll(flags);
      } else {
         this.iflag.removeAll(flags);
      }
   }

   public void setInputFlag(Attributes.InputFlag flag, boolean value) {
      if (value) {
         this.iflag.add(flag);
      } else {
         this.iflag.remove(flag);
      }
   }

   public EnumSet<Attributes.OutputFlag> getOutputFlags() {
      return this.oflag;
   }

   public void setOutputFlags(EnumSet<Attributes.OutputFlag> flags) {
      this.oflag.clear();
      this.oflag.addAll(flags);
   }

   public boolean getOutputFlag(Attributes.OutputFlag flag) {
      return this.oflag.contains(flag);
   }

   public void setOutputFlags(EnumSet<Attributes.OutputFlag> flags, boolean value) {
      if (value) {
         this.oflag.addAll(flags);
      } else {
         this.oflag.removeAll(flags);
      }
   }

   public void setOutputFlag(Attributes.OutputFlag flag, boolean value) {
      if (value) {
         this.oflag.add(flag);
      } else {
         this.oflag.remove(flag);
      }
   }

   public EnumSet<Attributes.ControlFlag> getControlFlags() {
      return this.cflag;
   }

   public void setControlFlags(EnumSet<Attributes.ControlFlag> flags) {
      this.cflag.clear();
      this.cflag.addAll(flags);
   }

   public boolean getControlFlag(Attributes.ControlFlag flag) {
      return this.cflag.contains(flag);
   }

   public void setControlFlags(EnumSet<Attributes.ControlFlag> flags, boolean value) {
      if (value) {
         this.cflag.addAll(flags);
      } else {
         this.cflag.removeAll(flags);
      }
   }

   public void setControlFlag(Attributes.ControlFlag flag, boolean value) {
      if (value) {
         this.cflag.add(flag);
      } else {
         this.cflag.remove(flag);
      }
   }

   public EnumSet<Attributes.LocalFlag> getLocalFlags() {
      return this.lflag;
   }

   public void setLocalFlags(EnumSet<Attributes.LocalFlag> flags) {
      this.lflag.clear();
      this.lflag.addAll(flags);
   }

   public boolean getLocalFlag(Attributes.LocalFlag flag) {
      return this.lflag.contains(flag);
   }

   public void setLocalFlags(EnumSet<Attributes.LocalFlag> flags, boolean value) {
      if (value) {
         this.lflag.addAll(flags);
      } else {
         this.lflag.removeAll(flags);
      }
   }

   public void setLocalFlag(Attributes.LocalFlag flag, boolean value) {
      if (value) {
         this.lflag.add(flag);
      } else {
         this.lflag.remove(flag);
      }
   }

   public EnumMap<Attributes.ControlChar, Integer> getControlChars() {
      return this.cchars;
   }

   public void setControlChars(EnumMap<Attributes.ControlChar, Integer> chars) {
      this.cchars.clear();
      this.cchars.putAll(chars);
   }

   public int getControlChar(Attributes.ControlChar c) {
      Integer v = this.cchars.get(c);
      return v != null ? v : -1;
   }

   public void setControlChar(Attributes.ControlChar c, int value) {
      this.cchars.put(c, value);
   }

   public void copy(Attributes attributes) {
      this.setControlFlags(attributes.getControlFlags());
      this.setInputFlags(attributes.getInputFlags());
      this.setLocalFlags(attributes.getLocalFlags());
      this.setOutputFlags(attributes.getOutputFlags());
      this.setControlChars(attributes.getControlChars());
   }

   @Override
   public String toString() {
      return "Attributes[lflags: "
         + this.append(this.lflag)
         + ", iflags: "
         + this.append(this.iflag)
         + ", oflags: "
         + this.append(this.oflag)
         + ", cflags: "
         + this.append(this.cflag)
         + ", cchars: "
         + this.append(EnumSet.allOf(Attributes.ControlChar.class), this::display)
         + "]";
   }

   private String display(Attributes.ControlChar c) {
      int ch = this.getControlChar(c);
      String value;
      if (c == Attributes.ControlChar.VMIN || c == Attributes.ControlChar.VTIME) {
         value = Integer.toString(ch);
      } else if (ch < 0) {
         value = "<undef>";
      } else if (ch < 32) {
         value = "^" + (char)(ch + 65 - 1);
      } else if (ch == 127) {
         value = "^?";
      } else if (ch >= 128) {
         value = String.format("\\u%04x", ch);
      } else {
         value = String.valueOf((char)ch);
      }

      return c.name().toLowerCase().substring(1) + "=" + value;
   }

   private <T extends Enum<T>> String append(EnumSet<T> set) {
      return this.append(set, e -> e.name().toLowerCase());
   }

   private <T extends Enum<T>> String append(EnumSet<T> set, Function<T, String> toString) {
      return set.stream().map(toString).collect(Collectors.joining(" "));
   }

   public static enum ControlChar {
      VEOF,
      VEOL,
      VEOL2,
      VERASE,
      VWERASE,
      VKILL,
      VREPRINT,
      VINTR,
      VQUIT,
      VSUSP,
      VDSUSP,
      VSTART,
      VSTOP,
      VLNEXT,
      VDISCARD,
      VMIN,
      VTIME,
      VSTATUS;
   }

   public static enum ControlFlag {
      CIGNORE,
      CS5,
      CS6,
      CS7,
      CS8,
      CSTOPB,
      CREAD,
      PARENB,
      PARODD,
      HUPCL,
      CLOCAL,
      CCTS_OFLOW,
      CRTS_IFLOW,
      CDTR_IFLOW,
      CDSR_OFLOW,
      CCAR_OFLOW;
   }

   public static enum InputFlag {
      IGNBRK,
      BRKINT,
      IGNPAR,
      PARMRK,
      INPCK,
      ISTRIP,
      INLCR,
      IGNCR,
      ICRNL,
      IXON,
      IXOFF,
      IXANY,
      IMAXBEL,
      IUTF8,
      INORMEOL;
   }

   public static enum LocalFlag {
      ECHOKE,
      ECHOE,
      ECHOK,
      ECHO,
      ECHONL,
      ECHOPRT,
      ECHOCTL,
      ISIG,
      ICANON,
      ALTWERASE,
      IEXTEN,
      EXTPROC,
      TOSTOP,
      FLUSHO,
      NOKERNINFO,
      PENDIN,
      NOFLSH;
   }

   public static enum OutputFlag {
      OPOST,
      ONLCR,
      OXTABS,
      ONOEOT,
      OCRNL,
      ONOCR,
      ONLRET,
      OFILL,
      NLDLY,
      TABDLY,
      CRDLY,
      FFDLY,
      BSDLY,
      VTDLY,
      OFDEL;
   }
}
