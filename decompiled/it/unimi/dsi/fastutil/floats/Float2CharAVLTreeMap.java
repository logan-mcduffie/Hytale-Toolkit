package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Float2CharAVLTreeMap extends AbstractFloat2CharSortedMap implements Serializable, Cloneable {
   protected transient Float2CharAVLTreeMap.Entry tree;
   protected int count;
   protected transient Float2CharAVLTreeMap.Entry firstEntry;
   protected transient Float2CharAVLTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Float2CharMap.Entry> entries;
   protected transient FloatSortedSet keys;
   protected transient CharCollection values;
   protected transient boolean modified;
   protected Comparator<? super Float> storedComparator;
   protected transient FloatComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Float2CharAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = FloatComparators.asFloatComparator(this.storedComparator);
   }

   public Float2CharAVLTreeMap(Comparator<? super Float> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Float2CharAVLTreeMap(Map<? extends Float, ? extends Character> m) {
      this();
      this.putAll(m);
   }

   public Float2CharAVLTreeMap(SortedMap<Float, Character> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Float2CharAVLTreeMap(Float2CharMap m) {
      this();
      this.putAll(m);
   }

   public Float2CharAVLTreeMap(Float2CharSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Float2CharAVLTreeMap(float[] k, char[] v, Comparator<? super Float> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Float2CharAVLTreeMap(float[] k, char[] v) {
      this(k, v, null);
   }

   final int compare(float k1, float k2) {
      return this.actualComparator == null ? Float.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Float2CharAVLTreeMap.Entry findKey(float k) {
      Float2CharAVLTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Float2CharAVLTreeMap.Entry locateKey(float k) {
      Float2CharAVLTreeMap.Entry e = this.tree;
      Float2CharAVLTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public char addTo(float k, char incr) {
      Float2CharAVLTreeMap.Entry e = this.add(k);
      char oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public char put(float k, char v) {
      Float2CharAVLTreeMap.Entry e = this.add(k);
      char oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Float2CharAVLTreeMap.Entry add(float k) {
      this.modified = false;
      Float2CharAVLTreeMap.Entry e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Float2CharAVLTreeMap.Entry(k, this.defRetValue);
         this.modified = true;
      } else {
         Float2CharAVLTreeMap.Entry p = this.tree;
         Float2CharAVLTreeMap.Entry q = null;
         Float2CharAVLTreeMap.Entry y = this.tree;
         Float2CharAVLTreeMap.Entry z = null;
         Float2CharAVLTreeMap.Entry w = null;
         int i = 0;

         while (true) {
            int cmp;
            if ((cmp = this.compare(k, p.key)) == 0) {
               return p;
            }

            if (p.balance() != 0) {
               i = 0;
               z = q;
               y = p;
            }

            if (this.dirPath[i++] = cmp > 0) {
               if (p.succ()) {
                  this.count++;
                  e = new Float2CharAVLTreeMap.Entry(k, this.defRetValue);
                  this.modified = true;
                  if (p.right == null) {
                     this.lastEntry = e;
                  }

                  e.left = p;
                  e.right = p.right;
                  p.right(e);
                  break;
               }

               q = p;
               p = p.right;
            } else {
               if (p.pred()) {
                  this.count++;
                  e = new Float2CharAVLTreeMap.Entry(k, this.defRetValue);
                  this.modified = true;
                  if (p.left == null) {
                     this.firstEntry = e;
                  }

                  e.right = p;
                  e.left = p.left;
                  p.left(e);
                  break;
               }

               q = p;
               p = p.left;
            }
         }

         p = y;

         for (int var14 = 0; p != e; p = this.dirPath[var14++] ? p.right : p.left) {
            if (this.dirPath[var14]) {
               p.incBalance();
            } else {
               p.decBalance();
            }
         }

         if (y.balance() == -2) {
            Float2CharAVLTreeMap.Entry x = y.left;
            if (x.balance() == -1) {
               w = x;
               if (x.succ()) {
                  x.succ(false);
                  y.pred(x);
               } else {
                  y.left = x.right;
               }

               x.right = y;
               x.balance(0);
               y.balance(0);
            } else {
               assert x.balance() == 1;

               w = x.right;
               x.right = w.left;
               w.left = x;
               y.left = w.right;
               w.right = y;
               if (w.balance() == -1) {
                  x.balance(0);
                  y.balance(1);
               } else if (w.balance() == 0) {
                  x.balance(0);
                  y.balance(0);
               } else {
                  x.balance(-1);
                  y.balance(0);
               }

               w.balance(0);
               if (w.pred()) {
                  x.succ(w);
                  w.pred(false);
               }

               if (w.succ()) {
                  y.pred(w);
                  w.succ(false);
               }
            }
         } else {
            if (y.balance() != 2) {
               return e;
            }

            Float2CharAVLTreeMap.Entry x = y.right;
            if (x.balance() == 1) {
               w = x;
               if (x.pred()) {
                  x.pred(false);
                  y.succ(x);
               } else {
                  y.right = x.left;
               }

               x.left = y;
               x.balance(0);
               y.balance(0);
            } else {
               assert x.balance() == -1;

               w = x.left;
               x.left = w.right;
               w.right = x;
               y.right = w.left;
               w.left = y;
               if (w.balance() == 1) {
                  x.balance(0);
                  y.balance(-1);
               } else if (w.balance() == 0) {
                  x.balance(0);
                  y.balance(0);
               } else {
                  x.balance(1);
                  y.balance(0);
               }

               w.balance(0);
               if (w.pred()) {
                  y.succ(w);
                  w.pred(false);
               }

               if (w.succ()) {
                  x.pred(w);
                  w.succ(false);
               }
            }
         }

         if (z == null) {
            this.tree = w;
         } else if (z.left == y) {
            z.left = w;
         } else {
            z.right = w;
         }
      }

      return e;
   }

   private Float2CharAVLTreeMap.Entry parent(Float2CharAVLTreeMap.Entry e) {
      if (e == this.tree) {
         return null;
      } else {
         Float2CharAVLTreeMap.Entry y = e;

         Float2CharAVLTreeMap.Entry x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Float2CharAVLTreeMap.Entry p = x.left;
               if (p == null || p.right != e) {
                  while (!y.succ()) {
                     y = y.right;
                  }

                  p = y.right;
               }

               return p;
            }

            x = x.left;
         }

         Float2CharAVLTreeMap.Entry p = y.right;
         if (p == null || p.left != e) {
            while (!x.pred()) {
               x = x.left;
            }

            p = x.left;
         }

         return p;
      }
   }

   @Override
   public char remove(float k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Float2CharAVLTreeMap.Entry p = this.tree;
         Float2CharAVLTreeMap.Entry q = null;
         boolean dir = false;
         float kk = k;

         int cmp;
         while ((cmp = this.compare(kk, p.key)) != 0) {
            if (dir = cmp > 0) {
               q = p;
               if ((p = p.right()) == null) {
                  return this.defRetValue;
               }
            } else {
               q = p;
               if ((p = p.left()) == null) {
                  return this.defRetValue;
               }
            }
         }

         if (p.left == null) {
            this.firstEntry = p.next();
         }

         if (p.right == null) {
            this.lastEntry = p.prev();
         }

         if (p.succ()) {
            if (p.pred()) {
               if (q != null) {
                  if (dir) {
                     q.succ(p.right);
                  } else {
                     q.pred(p.left);
                  }
               } else {
                  this.tree = dir ? p.right : p.left;
               }
            } else {
               p.prev().right = p.right;
               if (q != null) {
                  if (dir) {
                     q.right = p.left;
                  } else {
                     q.left = p.left;
                  }
               } else {
                  this.tree = p.left;
               }
            }
         } else {
            Float2CharAVLTreeMap.Entry r = p.right;
            if (r.pred()) {
               r.left = p.left;
               r.pred(p.pred());
               if (!r.pred()) {
                  r.prev().right = r;
               }

               if (q != null) {
                  if (dir) {
                     q.right = r;
                  } else {
                     q.left = r;
                  }
               } else {
                  this.tree = r;
               }

               r.balance(p.balance());
               q = r;
               dir = true;
            } else {
               while (true) {
                  Float2CharAVLTreeMap.Entry s = r.left;
                  if (s.pred()) {
                     if (s.succ()) {
                        r.pred(s);
                     } else {
                        r.left = s.right;
                     }

                     s.left = p.left;
                     if (!p.pred()) {
                        p.prev().right = s;
                        s.pred(false);
                     }

                     s.right = p.right;
                     s.succ(false);
                     if (q != null) {
                        if (dir) {
                           q.right = s;
                        } else {
                           q.left = s;
                        }
                     } else {
                        this.tree = s;
                     }

                     s.balance(p.balance());
                     q = r;
                     dir = false;
                     break;
                  }

                  r = s;
               }
            }
         }

         while (q != null) {
            Float2CharAVLTreeMap.Entry y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Float2CharAVLTreeMap.Entry x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Float2CharAVLTreeMap.Entry w = x.left;
                     x.left = w.right;
                     w.right = x;
                     y.right = w.left;
                     w.left = y;
                     if (w.balance() == 1) {
                        x.balance(0);
                        y.balance(-1);
                     } else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                     } else {
                        assert w.balance() == -1;

                        x.balance(1);
                        y.balance(0);
                     }

                     w.balance(0);
                     if (w.pred()) {
                        y.succ(w);
                        w.pred(false);
                     }

                     if (w.succ()) {
                        x.pred(w);
                        w.succ(false);
                     }

                     if (q != null) {
                        if (dir) {
                           q.right = w;
                        } else {
                           q.left = w;
                        }
                     } else {
                        this.tree = w;
                     }
                  } else {
                     if (q != null) {
                        if (dir) {
                           q.right = x;
                        } else {
                           q.left = x;
                        }
                     } else {
                        this.tree = x;
                     }

                     if (x.balance() == 0) {
                        y.right = x.left;
                        x.left = y;
                        x.balance(-1);
                        y.balance(1);
                        break;
                     }

                     assert x.balance() == 1;

                     if (x.pred()) {
                        y.succ(true);
                        x.pred(false);
                     } else {
                        y.right = x.left;
                     }

                     x.left = y;
                     y.balance(0);
                     x.balance(0);
                  }
               }
            } else {
               dir = q != null && q.left != y;
               y.decBalance();
               if (y.balance() == -1) {
                  break;
               }

               if (y.balance() == -2) {
                  Float2CharAVLTreeMap.Entry xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Float2CharAVLTreeMap.Entry wx = xx.right;
                     xx.right = wx.left;
                     wx.left = xx;
                     y.left = wx.right;
                     wx.right = y;
                     if (wx.balance() == -1) {
                        xx.balance(0);
                        y.balance(1);
                     } else if (wx.balance() == 0) {
                        xx.balance(0);
                        y.balance(0);
                     } else {
                        assert wx.balance() == 1;

                        xx.balance(-1);
                        y.balance(0);
                     }

                     wx.balance(0);
                     if (wx.pred()) {
                        xx.succ(wx);
                        wx.pred(false);
                     }

                     if (wx.succ()) {
                        y.pred(wx);
                        wx.succ(false);
                     }

                     if (q != null) {
                        if (dir) {
                           q.right = wx;
                        } else {
                           q.left = wx;
                        }
                     } else {
                        this.tree = wx;
                     }
                  } else {
                     if (q != null) {
                        if (dir) {
                           q.right = xx;
                        } else {
                           q.left = xx;
                        }
                     } else {
                        this.tree = xx;
                     }

                     if (xx.balance() == 0) {
                        y.left = xx.right;
                        xx.right = y;
                        xx.balance(1);
                        y.balance(-1);
                        break;
                     }

                     assert xx.balance() == -1;

                     if (xx.succ()) {
                        y.pred(true);
                        xx.succ(false);
                     } else {
                        y.left = xx.right;
                     }

                     xx.right = y;
                     y.balance(0);
                     xx.balance(0);
                  }
               }
            }
         }

         this.modified = true;
         this.count--;
         return p.value;
      }
   }

   @Override
   public boolean containsValue(char v) {
      Float2CharAVLTreeMap.ValueIterator i = new Float2CharAVLTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         char ev = i.nextChar();
         if (ev == v) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void clear() {
      this.count = 0;
      this.tree = null;
      this.entries = null;
      this.values = null;
      this.keys = null;
      this.firstEntry = this.lastEntry = null;
   }

   @Override
   public boolean containsKey(float k) {
      return this.findKey(k) != null;
   }

   @Override
   public int size() {
      return this.count;
   }

   @Override
   public boolean isEmpty() {
      return this.count == 0;
   }

   @Override
   public char get(float k) {
      Float2CharAVLTreeMap.Entry e = this.findKey(k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public float firstFloatKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public float lastFloatKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Float2CharMap.Entry>() {
            final Comparator<? super Float2CharMap.Entry> comparator = Float2CharAVLTreeMap.this.actualComparator == null
               ? (x, y) -> Float.compare(x.getFloatKey(), y.getFloatKey())
               : (x, y) -> Float2CharAVLTreeMap.this.actualComparator.compare(x.getFloatKey(), y.getFloatKey());

            @Override
            public Comparator<? super Float2CharMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Float2CharMap.Entry> iterator() {
               return Float2CharAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Float2CharMap.Entry> iterator(Float2CharMap.Entry from) {
               return Float2CharAVLTreeMap.this.new EntryIterator(from.getFloatKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Float)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Character) {
                     Float2CharAVLTreeMap.Entry f = Float2CharAVLTreeMap.this.findKey((Float)e.getKey());
                     return e.equals(f);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            }

            @Override
            public boolean remove(Object o) {
               if (!(o instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                     return false;
                  } else if (!(e.getValue() instanceof Character)) {
                     return false;
                  } else {
                     Float2CharAVLTreeMap.Entry f = Float2CharAVLTreeMap.this.findKey((Float)e.getKey());
                     if (f != null && f.getCharValue() == (Character)e.getValue()) {
                        Float2CharAVLTreeMap.this.remove(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            }

            @Override
            public int size() {
               return Float2CharAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Float2CharAVLTreeMap.this.clear();
            }

            public Float2CharMap.Entry first() {
               return Float2CharAVLTreeMap.this.firstEntry;
            }

            public Float2CharMap.Entry last() {
               return Float2CharAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Float2CharMap.Entry> subSet(Float2CharMap.Entry from, Float2CharMap.Entry to) {
               return Float2CharAVLTreeMap.this.subMap(from.getFloatKey(), to.getFloatKey()).float2CharEntrySet();
            }

            public ObjectSortedSet<Float2CharMap.Entry> headSet(Float2CharMap.Entry to) {
               return Float2CharAVLTreeMap.this.headMap(to.getFloatKey()).float2CharEntrySet();
            }

            public ObjectSortedSet<Float2CharMap.Entry> tailSet(Float2CharMap.Entry from) {
               return Float2CharAVLTreeMap.this.tailMap(from.getFloatKey()).float2CharEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public FloatSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2CharAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public CharCollection values() {
      if (this.values == null) {
         this.values = new AbstractCharCollection() {
            @Override
            public CharIterator iterator() {
               return Float2CharAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(char k) {
               return Float2CharAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Float2CharAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Float2CharAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public FloatComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public Float2CharSortedMap headMap(float to) {
      return new Float2CharAVLTreeMap.Submap(0.0F, true, to, false);
   }

   @Override
   public Float2CharSortedMap tailMap(float from) {
      return new Float2CharAVLTreeMap.Submap(from, false, 0.0F, true);
   }

   @Override
   public Float2CharSortedMap subMap(float from, float to) {
      return new Float2CharAVLTreeMap.Submap(from, false, to, false);
   }

   public Float2CharAVLTreeMap clone() {
      Float2CharAVLTreeMap c;
      try {
         c = (Float2CharAVLTreeMap)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.allocatePaths();
      if (this.count == 0) {
         return c;
      } else {
         Float2CharAVLTreeMap.Entry rp = new Float2CharAVLTreeMap.Entry();
         Float2CharAVLTreeMap.Entry rq = new Float2CharAVLTreeMap.Entry();
         Float2CharAVLTreeMap.Entry p = rp;
         rp.left(this.tree);
         Float2CharAVLTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Float2CharAVLTreeMap.Entry e = p.left.clone();
               e.pred(q.left);
               e.succ(q);
               q.left(e);
               p = p.left;
               q = q.left;
            } else {
               while (p.succ()) {
                  p = p.right;
                  if (p == null) {
                     q.right = null;
                     c.tree = rq.left;
                     c.firstEntry = c.tree;

                     while (c.firstEntry.left != null) {
                        c.firstEntry = c.firstEntry.left;
                     }

                     c.lastEntry = c.tree;

                     while (c.lastEntry.right != null) {
                        c.lastEntry = c.lastEntry.right;
                     }

                     return c;
                  }

                  q = q.right;
               }

               p = p.right;
               q = q.right;
            }

            if (!p.succ()) {
               Float2CharAVLTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Float2CharAVLTreeMap.EntryIterator i = new Float2CharAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Float2CharAVLTreeMap.Entry e = i.nextEntry();
         s.writeFloat(e.key);
         s.writeChar(e.value);
      }
   }

   private Float2CharAVLTreeMap.Entry readTree(ObjectInputStream s, int n, Float2CharAVLTreeMap.Entry pred, Float2CharAVLTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Float2CharAVLTreeMap.Entry top = new Float2CharAVLTreeMap.Entry(s.readFloat(), s.readChar());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Float2CharAVLTreeMap.Entry top = new Float2CharAVLTreeMap.Entry(s.readFloat(), s.readChar());
         top.right(new Float2CharAVLTreeMap.Entry(s.readFloat(), s.readChar()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Float2CharAVLTreeMap.Entry top = new Float2CharAVLTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readFloat();
         top.value = s.readChar();
         top.right(this.readTree(s, rightN, top, succ));
         if (n == (n & -n)) {
            top.balance(1);
         }

         return top;
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.setActualComparator();
      this.allocatePaths();
      if (this.count != 0) {
         this.tree = this.readTree(s, this.count, null, null);
         Float2CharAVLTreeMap.Entry e = this.tree;

         while (e.left() != null) {
            e = e.left();
         }

         this.firstEntry = e;
         e = this.tree;

         while (e.right() != null) {
            e = e.right();
         }

         this.lastEntry = e;
      }
   }

   private static final class Entry extends AbstractFloat2CharMap.BasicEntry implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Float2CharAVLTreeMap.Entry left;
      Float2CharAVLTreeMap.Entry right;
      int info;

      Entry() {
         super(0.0F, '\u0000');
      }

      Entry(float k, char v) {
         super(k, v);
         this.info = -1073741824;
      }

      Float2CharAVLTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Float2CharAVLTreeMap.Entry right() {
         return (this.info & -2147483648) != 0 ? null : this.right;
      }

      boolean pred() {
         return (this.info & 1073741824) != 0;
      }

      boolean succ() {
         return (this.info & -2147483648) != 0;
      }

      void pred(boolean pred) {
         if (pred) {
            this.info |= 1073741824;
         } else {
            this.info &= -1073741825;
         }
      }

      void succ(boolean succ) {
         if (succ) {
            this.info |= Integer.MIN_VALUE;
         } else {
            this.info &= Integer.MAX_VALUE;
         }
      }

      void pred(Float2CharAVLTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Float2CharAVLTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Float2CharAVLTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Float2CharAVLTreeMap.Entry right) {
         this.info &= Integer.MAX_VALUE;
         this.right = right;
      }

      int balance() {
         return (byte)this.info;
      }

      void balance(int level) {
         this.info &= -256;
         this.info |= level & 0xFF;
      }

      void incBalance() {
         this.info = this.info & -256 | (byte)this.info + 1 & 0xFF;
      }

      protected void decBalance() {
         this.info = this.info & -256 | (byte)this.info - 1 & 0xFF;
      }

      Float2CharAVLTreeMap.Entry next() {
         Float2CharAVLTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Float2CharAVLTreeMap.Entry prev() {
         Float2CharAVLTreeMap.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public char setValue(char value) {
         char oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Float2CharAVLTreeMap.Entry clone() {
         Float2CharAVLTreeMap.Entry c;
         try {
            c = (Float2CharAVLTreeMap.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         c.key = this.key;
         c.value = this.value;
         c.info = this.info;
         return c;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<Float, Character> e = (java.util.Map.Entry<Float, Character>)o;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getKey()) && this.value == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Float2CharAVLTreeMap.TreeIterator implements ObjectListIterator<Float2CharMap.Entry> {
      EntryIterator() {
      }

      EntryIterator(float k) {
         super(k);
      }

      public Float2CharMap.Entry next() {
         return this.nextEntry();
      }

      public Float2CharMap.Entry previous() {
         return this.previousEntry();
      }

      public void set(Float2CharMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Float2CharMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Float2CharAVLTreeMap.TreeIterator implements FloatListIterator {
      public KeyIterator() {
      }

      public KeyIterator(float k) {
         super(k);
      }

      @Override
      public float nextFloat() {
         return this.nextEntry().key;
      }

      @Override
      public float previousFloat() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractFloat2CharSortedMap.KeySet {
      private KeySet() {
      }

      @Override
      public FloatBidirectionalIterator iterator() {
         return Float2CharAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public FloatBidirectionalIterator iterator(float from) {
         return Float2CharAVLTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractFloat2CharSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      float from;
      float to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Float2CharMap.Entry> entries;
      protected transient FloatSortedSet keys;
      protected transient CharCollection values;

      public Submap(float from, boolean bottom, float to, boolean top) {
         if (!bottom && !top && Float2CharAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Float2CharAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Float2CharAVLTreeMap.Submap.SubmapIterator i = new Float2CharAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(float k) {
         return (this.bottom || Float2CharAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Float2CharAVLTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Float2CharMap.Entry>() {
               @Override
               public ObjectBidirectionalIterator<Float2CharMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Float2CharMap.Entry> iterator(Float2CharMap.Entry from) {
                  return Submap.this.new SubmapEntryIterator(from.getFloatKey());
               }

               @Override
               public Comparator<? super Float2CharMap.Entry> comparator() {
                  return Float2CharAVLTreeMap.this.float2CharEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Character) {
                        Float2CharAVLTreeMap.Entry f = Float2CharAVLTreeMap.this.findKey((Float)e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                     } else {
                        return false;
                     }
                  }
               }

               @Override
               public boolean remove(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Character) {
                        Float2CharAVLTreeMap.Entry f = Float2CharAVLTreeMap.this.findKey((Float)e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                           Submap.this.remove(f.key);
                        }

                        return f != null;
                     } else {
                        return false;
                     }
                  }
               }

               @Override
               public int size() {
                  int c = 0;
                  Iterator<?> i = this.iterator();

                  while (i.hasNext()) {
                     c++;
                     i.next();
                  }

                  return c;
               }

               @Override
               public boolean isEmpty() {
                  return !Submap.this.new SubmapIterator().hasNext();
               }

               @Override
               public void clear() {
                  Submap.this.clear();
               }

               public Float2CharMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Float2CharMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Float2CharMap.Entry> subSet(Float2CharMap.Entry from, Float2CharMap.Entry to) {
                  return Submap.this.subMap(from.getFloatKey(), to.getFloatKey()).float2CharEntrySet();
               }

               public ObjectSortedSet<Float2CharMap.Entry> headSet(Float2CharMap.Entry to) {
                  return Submap.this.headMap(to.getFloatKey()).float2CharEntrySet();
               }

               public ObjectSortedSet<Float2CharMap.Entry> tailSet(Float2CharMap.Entry from) {
                  return Submap.this.tailMap(from.getFloatKey()).float2CharEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Float2CharAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public CharCollection values() {
         if (this.values == null) {
            this.values = new AbstractCharCollection() {
               @Override
               public CharIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(char k) {
                  return Submap.this.containsValue(k);
               }

               @Override
               public int size() {
                  return Submap.this.size();
               }

               @Override
               public void clear() {
                  Submap.this.clear();
               }
            };
         }

         return this.values;
      }

      @Override
      public boolean containsKey(float k) {
         return this.in(k) && Float2CharAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(char v) {
         Float2CharAVLTreeMap.Submap.SubmapIterator i = new Float2CharAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            char ev = i.nextEntry().value;
            if (ev == v) {
               return true;
            }
         }

         return false;
      }

      @Override
      public char get(float k) {
         Float2CharAVLTreeMap.Entry e;
         return this.in(k) && (e = Float2CharAVLTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public char put(float k, char v) {
         Float2CharAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            char oldValue = Float2CharAVLTreeMap.this.put(k, v);
            return Float2CharAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public char remove(float k) {
         Float2CharAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            char oldValue = Float2CharAVLTreeMap.this.remove(k);
            return Float2CharAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Float2CharAVLTreeMap.Submap.SubmapIterator i = new Float2CharAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Float2CharAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public FloatComparator comparator() {
         return Float2CharAVLTreeMap.this.actualComparator;
      }

      @Override
      public Float2CharSortedMap headMap(float to) {
         if (this.top) {
            return Float2CharAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Float2CharAVLTreeMap.this.compare(to, this.to) < 0 ? Float2CharAVLTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Float2CharSortedMap tailMap(float from) {
         if (this.bottom) {
            return Float2CharAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Float2CharAVLTreeMap.this.compare(from, this.from) > 0 ? Float2CharAVLTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Float2CharSortedMap subMap(float from, float to) {
         if (this.top && this.bottom) {
            return Float2CharAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Float2CharAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Float2CharAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Float2CharAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Float2CharAVLTreeMap.Entry firstEntry() {
         if (Float2CharAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Float2CharAVLTreeMap.Entry e;
            if (this.bottom) {
               e = Float2CharAVLTreeMap.this.firstEntry;
            } else {
               e = Float2CharAVLTreeMap.this.locateKey(this.from);
               if (Float2CharAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Float2CharAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Float2CharAVLTreeMap.Entry lastEntry() {
         if (Float2CharAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Float2CharAVLTreeMap.Entry e;
            if (this.top) {
               e = Float2CharAVLTreeMap.this.lastEntry;
            } else {
               e = Float2CharAVLTreeMap.this.locateKey(this.to);
               if (Float2CharAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Float2CharAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public float firstFloatKey() {
         Float2CharAVLTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public float lastFloatKey() {
         Float2CharAVLTreeMap.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractFloat2CharSortedMap.KeySet {
         private KeySet() {
         }

         @Override
         public FloatBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public FloatBidirectionalIterator iterator(float from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Float2CharAVLTreeMap.Submap.SubmapIterator implements ObjectListIterator<Float2CharMap.Entry> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(float k) {
            super(k);
         }

         public Float2CharMap.Entry next() {
            return this.nextEntry();
         }

         public Float2CharMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Float2CharAVLTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(float k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Float2CharAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Float2CharAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Float2CharAVLTreeMap.this.locateKey(k);
                  if (Float2CharAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                     this.prev = this.next;
                     this.next = this.next.next();
                  } else {
                     this.prev = this.next.prev();
                  }
               }
            }
         }

         @Override
         void updatePrevious() {
            this.prev = this.prev.prev();
            if (!Submap.this.bottom && this.prev != null && Float2CharAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Float2CharAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Float2CharAVLTreeMap.Submap.SubmapIterator implements FloatListIterator {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(float from) {
            super(from);
         }

         @Override
         public float nextFloat() {
            return this.nextEntry().key;
         }

         @Override
         public float previousFloat() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Float2CharAVLTreeMap.Submap.SubmapIterator implements CharListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public char nextChar() {
            return this.nextEntry().value;
         }

         @Override
         public char previousChar() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Float2CharAVLTreeMap.Entry prev;
      Float2CharAVLTreeMap.Entry next;
      Float2CharAVLTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Float2CharAVLTreeMap.this.firstEntry;
      }

      TreeIterator(float k) {
         if ((this.next = Float2CharAVLTreeMap.this.locateKey(k)) != null) {
            if (Float2CharAVLTreeMap.this.compare(this.next.key, k) <= 0) {
               this.prev = this.next;
               this.next = this.next.next();
            } else {
               this.prev = this.next.prev();
            }
         }
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public boolean hasPrevious() {
         return this.prev != null;
      }

      void updateNext() {
         this.next = this.next.next();
      }

      Float2CharAVLTreeMap.Entry nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            this.index++;
            this.updateNext();
            return this.curr;
         }
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      Float2CharAVLTreeMap.Entry previousEntry() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next = this.prev;
            this.index--;
            this.updatePrevious();
            return this.curr;
         }
      }

      public int nextIndex() {
         return this.index;
      }

      public int previousIndex() {
         return this.index - 1;
      }

      public void remove() {
         if (this.curr == null) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               this.index--;
            }

            this.next = this.prev = this.curr;
            this.updatePrevious();
            this.updateNext();
            Float2CharAVLTreeMap.this.remove(this.curr.key);
            this.curr = null;
         }
      }

      public int skip(int n) {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return n - i - 1;
      }

      public int back(int n) {
         int i = n;

         while (i-- != 0 && this.hasPrevious()) {
            this.previousEntry();
         }

         return n - i - 1;
      }
   }

   private final class ValueIterator extends Float2CharAVLTreeMap.TreeIterator implements CharListIterator {
      private ValueIterator() {
      }

      @Override
      public char nextChar() {
         return this.nextEntry().value;
      }

      @Override
      public char previousChar() {
         return this.previousEntry().value;
      }
   }
}
