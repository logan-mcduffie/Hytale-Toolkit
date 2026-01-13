package org.jline.reader;

@FunctionalInterface
public interface Widget extends Binding {
   boolean apply();
}
