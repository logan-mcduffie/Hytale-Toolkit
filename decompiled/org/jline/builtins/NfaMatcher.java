package org.jline.builtins;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NfaMatcher<T> {
   private final String regexp;
   private final BiFunction<T, String, Boolean> matcher;
   private volatile NfaMatcher.State start;

   public NfaMatcher(String regexp, BiFunction<T, String, Boolean> matcher) {
      this.regexp = regexp;
      this.matcher = matcher;
   }

   public void compile() {
      if (this.start == null) {
         this.start = toNfa(toPostFix(this.regexp));
      }
   }

   public boolean match(List<T> args) {
      Set<NfaMatcher.State> clist = new HashSet<>();
      this.compile();
      this.addState(clist, this.start);

      for (T arg : args) {
         Set<NfaMatcher.State> nlist = new HashSet<>();
         clist.stream()
            .filter(s -> !Objects.equals("++MATCH++", s.c) && !Objects.equals("++SPLIT++", s.c))
            .filter(s -> this.matcher.apply(arg, s.c))
            .forEach(s -> this.addState(nlist, s.out));
         clist = nlist;
      }

      return clist.stream().anyMatch(s -> Objects.equals("++MATCH++", s.c));
   }

   public Set<String> matchPartial(List<T> args) {
      Set<NfaMatcher.State> clist = new HashSet<>();
      this.compile();
      this.addState(clist, this.start);

      for (T arg : args) {
         Set<NfaMatcher.State> nlist = new HashSet<>();
         clist.stream()
            .filter(s -> !Objects.equals("++MATCH++", s.c) && !Objects.equals("++SPLIT++", s.c))
            .filter(s -> this.matcher.apply(arg, s.c))
            .forEach(s -> this.addState(nlist, s.out));
         clist = nlist;
      }

      return clist.stream().filter(s -> !Objects.equals("++MATCH++", s.c) && !Objects.equals("++SPLIT++", s.c)).map(s -> s.c).collect(Collectors.toSet());
   }

   void addState(Set<NfaMatcher.State> l, NfaMatcher.State s) {
      if (s != null && l.add(s) && Objects.equals("++SPLIT++", s.c)) {
         this.addState(l, s.out);
         this.addState(l, s.out1);
      }
   }

   static NfaMatcher.State toNfa(List<String> postfix) {
      Deque<NfaMatcher.Frag> stack = new ArrayDeque<>();

      for (String p : postfix) {
         switch (p) {
            case ".": {
               NfaMatcher.Frag e2 = stack.pollLast();
               NfaMatcher.Frag e1 = stack.pollLast();
               e1.patch(e2.start);
               stack.offerLast(new NfaMatcher.Frag(e1.start, e2.out));
               break;
            }
            case "|": {
               NfaMatcher.Frag e2 = stack.pollLast();
               NfaMatcher.Frag e1 = stack.pollLast();
               NfaMatcher.State s = new NfaMatcher.State("++SPLIT++", e1.start, e2.start);
               stack.offerLast(new NfaMatcher.Frag(s, e1.out, e2.out));
               break;
            }
            case "?": {
               NfaMatcher.Frag e = stack.pollLast();
               NfaMatcher.State s = new NfaMatcher.State("++SPLIT++", e.start, null);
               stack.offerLast(new NfaMatcher.Frag(s, e.out, s::setOut1));
               break;
            }
            case "*": {
               NfaMatcher.Frag e = stack.pollLast();
               NfaMatcher.State s = new NfaMatcher.State("++SPLIT++", e.start, null);
               e.patch(s);
               stack.offerLast(new NfaMatcher.Frag(s, s::setOut1));
               break;
            }
            case "+": {
               NfaMatcher.Frag e = stack.pollLast();
               NfaMatcher.State s = new NfaMatcher.State("++SPLIT++", e.start, null);
               e.patch(s);
               stack.offerLast(new NfaMatcher.Frag(e.start, s::setOut1));
               break;
            }
            default: {
               NfaMatcher.State s = new NfaMatcher.State(p, null, null);
               stack.offerLast(new NfaMatcher.Frag(s, s::setOut));
            }
         }
      }

      NfaMatcher.Frag e = stack.pollLast();
      if (!stack.isEmpty()) {
         throw new IllegalStateException("Wrong postfix expression, " + stack.size() + " elements remaining");
      } else {
         e.patch(new NfaMatcher.State("++MATCH++", null, null));
         return e.start;
      }
   }

   static List<String> toPostFix(String regexp) {
      List<String> postfix = new ArrayList<>();
      int s = -1;
      int natom = 0;
      int nalt = 0;
      Deque<Integer> natoms = new ArrayDeque<>();
      Deque<Integer> nalts = new ArrayDeque<>();

      for (int i = 0; i < regexp.length(); i++) {
         char c = regexp.charAt(i);
         if (Character.isJavaIdentifierPart(c)) {
            if (s < 0) {
               s = i;
            }
         } else {
            if (s >= 0) {
               if (natom > 1) {
                  natom--;
                  postfix.add(".");
               }

               postfix.add(regexp.substring(s, i));
               natom++;
               s = -1;
            }

            if (!Character.isWhitespace(c)) {
               switch (c) {
                  case '(':
                     if (natom > 1) {
                        natom--;
                        postfix.add(".");
                     }

                     nalts.offerLast(nalt);
                     natoms.offerLast(natom);
                     nalt = 0;
                     natom = 0;
                     break;
                  case ')':
                     if (nalts.isEmpty() || natom == 0) {
                        throw new IllegalStateException("unexpected '" + c + "' at pos " + i);
                     }

                     while (--natom > 0) {
                        postfix.add(".");
                     }

                     while (nalt > 0) {
                        postfix.add("|");
                        nalt--;
                     }

                     nalt = nalts.pollLast();
                     natom = natoms.pollLast();
                     natom++;
                     break;
                  case '*':
                  case '+':
                  case '?':
                     if (natom == 0) {
                        throw new IllegalStateException("unexpected '" + c + "' at pos " + i);
                     }

                     postfix.add(String.valueOf(c));
                     break;
                  case '|':
                     if (natom == 0) {
                        throw new IllegalStateException("unexpected '" + c + "' at pos " + i);
                     }

                     while (--natom > 0) {
                        postfix.add(".");
                     }

                     nalt++;
                     break;
                  default:
                     throw new IllegalStateException("unexpected '" + c + "' at pos " + i);
               }
            }
         }
      }

      if (s >= 0) {
         if (natom > 1) {
            natom--;
            postfix.add(".");
         }

         postfix.add(regexp.substring(s));
         natom++;
      }

      while (--natom > 0) {
         postfix.add(".");
      }

      while (nalt > 0) {
         postfix.add("|");
         nalt--;
      }

      return postfix;
   }

   private static class Frag {
      final NfaMatcher.State start;
      final List<Consumer<NfaMatcher.State>> out = new ArrayList<>();

      public Frag(NfaMatcher.State start, Collection<Consumer<NfaMatcher.State>> l) {
         this.start = start;
         this.out.addAll(l);
      }

      public Frag(NfaMatcher.State start, Collection<Consumer<NfaMatcher.State>> l1, Collection<Consumer<NfaMatcher.State>> l2) {
         this.start = start;
         this.out.addAll(l1);
         this.out.addAll(l2);
      }

      public Frag(NfaMatcher.State start, Consumer<NfaMatcher.State> c) {
         this.start = start;
         this.out.add(c);
      }

      public Frag(NfaMatcher.State start, Collection<Consumer<NfaMatcher.State>> l, Consumer<NfaMatcher.State> c) {
         this.start = start;
         this.out.addAll(l);
         this.out.add(c);
      }

      public void patch(NfaMatcher.State s) {
         this.out.forEach(c -> c.accept(s));
      }
   }

   static class State {
      static final String Match = "++MATCH++";
      static final String Split = "++SPLIT++";
      final String c;
      NfaMatcher.State out;
      NfaMatcher.State out1;

      public State(String c, NfaMatcher.State out, NfaMatcher.State out1) {
         this.c = c;
         this.out = out;
         this.out1 = out1;
      }

      public void setOut(NfaMatcher.State out) {
         this.out = out;
      }

      public void setOut1(NfaMatcher.State out1) {
         this.out1 = out1;
      }
   }
}
