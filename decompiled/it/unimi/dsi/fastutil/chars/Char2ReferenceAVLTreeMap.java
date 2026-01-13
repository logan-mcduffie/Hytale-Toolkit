package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Char2ReferenceAVLTreeMap<V> extends AbstractChar2ReferenceSortedMap<V> implements Serializable, Cloneable {
   protected transient Char2ReferenceAVLTreeMap.Entry<V> tree;
   protected int count;
   protected transient Char2ReferenceAVLTreeMap.Entry<V> firstEntry;
   protected transient Char2ReferenceAVLTreeMap.Entry<V> lastEntry;
   protected transient ObjectSortedSet<Char2ReferenceMap.Entry<V>> entries;
   protected transient CharSortedSet keys;
   protected transient ReferenceCollection<V> values;
   protected transient boolean modified;
   protected Comparator<? super Character> storedComparator;
   protected transient CharComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Char2ReferenceAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = CharComparators.asCharComparator(this.storedComparator);
   }

   public Char2ReferenceAVLTreeMap(Comparator<? super Character> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Char2ReferenceAVLTreeMap(Map<? extends Character, ? extends V> m) {
      this();
      this.putAll(m);
   }

   public Char2ReferenceAVLTreeMap(SortedMap<Character, V> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Char2ReferenceAVLTreeMap(Char2ReferenceMap<? extends V> m) {
      this();
      this.putAll(m);
   }

   public Char2ReferenceAVLTreeMap(Char2ReferenceSortedMap<V> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Char2ReferenceAVLTreeMap(char[] k, V[] v, Comparator<? super Character> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Char2ReferenceAVLTreeMap(char[] k, V[] v) {
      this(k, v, null);
   }

   final int compare(char k1, char k2) {
      return this.actualComparator == null ? Character.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Char2ReferenceAVLTreeMap.Entry<V> findKey(char k) {
      Char2ReferenceAVLTreeMap.Entry<V> e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Char2ReferenceAVLTreeMap.Entry<V> locateKey(char k) {
      Char2ReferenceAVLTreeMap.Entry<V> e = this.tree;
      Char2ReferenceAVLTreeMap.Entry<V> last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   @Override
   public V put(char k, V v) {
      Char2ReferenceAVLTreeMap.Entry<V> e = this.add(k);
      V oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Char2ReferenceAVLTreeMap.Entry<V> add(char k) {
      this.modified = false;
      Char2ReferenceAVLTreeMap.Entry<V> e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Char2ReferenceAVLTreeMap.Entry<>(k, this.defRetValue);
         this.modified = true;
      } else {
         Char2ReferenceAVLTreeMap.Entry<V> p = this.tree;
         Char2ReferenceAVLTreeMap.Entry<V> q = null;
         Char2ReferenceAVLTreeMap.Entry<V> y = this.tree;
         Char2ReferenceAVLTreeMap.Entry<V> z = null;
         Char2ReferenceAVLTreeMap.Entry<V> w = null;
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
                  e = new Char2ReferenceAVLTreeMap.Entry<>(k, this.defRetValue);
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
                  e = new Char2ReferenceAVLTreeMap.Entry<>(k, this.defRetValue);
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
            Char2ReferenceAVLTreeMap.Entry<V> x = y.left;
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

            Char2ReferenceAVLTreeMap.Entry<V> x = y.right;
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

   private Char2ReferenceAVLTreeMap.Entry<V> parent(Char2ReferenceAVLTreeMap.Entry<V> e) {
      if (e == this.tree) {
         return null;
      } else {
         Char2ReferenceAVLTreeMap.Entry<V> y = e;

         Char2ReferenceAVLTreeMap.Entry<V> x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Char2ReferenceAVLTreeMap.Entry<V> p = x.left;
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

         Char2ReferenceAVLTreeMap.Entry<V> p = y.right;
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
   public V remove(char k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Char2ReferenceAVLTreeMap.Entry<V> p = this.tree;
         Char2ReferenceAVLTreeMap.Entry<V> q = null;
         boolean dir = false;
         char kk = k;

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
            Char2ReferenceAVLTreeMap.Entry<V> r = p.right;
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
                  Char2ReferenceAVLTreeMap.Entry<V> s = r.left;
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
            Char2ReferenceAVLTreeMap.Entry<V> y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Char2ReferenceAVLTreeMap.Entry<V> x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Char2ReferenceAVLTreeMap.Entry<V> w = x.left;
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
                  Char2ReferenceAVLTreeMap.Entry<V> xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Char2ReferenceAVLTreeMap.Entry<V> wx = xx.right;
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
   public boolean containsValue(Object v) {
      Char2ReferenceAVLTreeMap<V>.ValueIterator i = new Char2ReferenceAVLTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         V ev = i.next();
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
   public boolean containsKey(char k) {
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
   public V get(char k) {
      Char2ReferenceAVLTreeMap.Entry<V> e = this.findKey(k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public char firstCharKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public char lastCharKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Char2ReferenceMap.Entry<V>> char2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Char2ReferenceMap.Entry<V>>() {
            final Comparator<? super Char2ReferenceMap.Entry<V>> comparator = Char2ReferenceAVLTreeMap.this.actualComparator == null
               ? (x, y) -> Character.compare(x.getCharKey(), y.getCharKey())
               : (x, y) -> Char2ReferenceAVLTreeMap.this.actualComparator.compare(x.getCharKey(), y.getCharKey());

            @Override
            public Comparator<? super Char2ReferenceMap.Entry<V>> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> iterator() {
               return Char2ReferenceAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> iterator(Char2ReferenceMap.Entry<V> from) {
               return Char2ReferenceAVLTreeMap.this.new EntryIterator(from.getCharKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Character)) {
                     return false;
                  } else {
                     Char2ReferenceAVLTreeMap.Entry<V> f = Char2ReferenceAVLTreeMap.this.findKey((Character)e.getKey());
                     return e.equals(f);
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
                  } else if (e.getKey() != null && e.getKey() instanceof Character) {
                     Char2ReferenceAVLTreeMap.Entry<V> f = Char2ReferenceAVLTreeMap.this.findKey((Character)e.getKey());
                     if (f != null && f.getValue() == e.getValue()) {
                        Char2ReferenceAVLTreeMap.this.remove(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            }

            @Override
            public int size() {
               return Char2ReferenceAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Char2ReferenceAVLTreeMap.this.clear();
            }

            public Char2ReferenceMap.Entry<V> first() {
               return Char2ReferenceAVLTreeMap.this.firstEntry;
            }

            public Char2ReferenceMap.Entry<V> last() {
               return Char2ReferenceAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Char2ReferenceMap.Entry<V>> subSet(Char2ReferenceMap.Entry<V> from, Char2ReferenceMap.Entry<V> to) {
               return Char2ReferenceAVLTreeMap.this.subMap(from.getCharKey(), to.getCharKey()).char2ReferenceEntrySet();
            }

            public ObjectSortedSet<Char2ReferenceMap.Entry<V>> headSet(Char2ReferenceMap.Entry<V> to) {
               return Char2ReferenceAVLTreeMap.this.headMap(to.getCharKey()).char2ReferenceEntrySet();
            }

            public ObjectSortedSet<Char2ReferenceMap.Entry<V>> tailSet(Char2ReferenceMap.Entry<V> from) {
               return Char2ReferenceAVLTreeMap.this.tailMap(from.getCharKey()).char2ReferenceEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public CharSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2ReferenceAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            @Override
            public ObjectIterator<V> iterator() {
               return Char2ReferenceAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(Object k) {
               return Char2ReferenceAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Char2ReferenceAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Char2ReferenceAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public CharComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public Char2ReferenceSortedMap<V> headMap(char to) {
      return new Char2ReferenceAVLTreeMap.Submap('\u0000', true, to, false);
   }

   @Override
   public Char2ReferenceSortedMap<V> tailMap(char from) {
      return new Char2ReferenceAVLTreeMap.Submap(from, false, '\u0000', true);
   }

   @Override
   public Char2ReferenceSortedMap<V> subMap(char from, char to) {
      return new Char2ReferenceAVLTreeMap.Submap(from, false, to, false);
   }

   public Char2ReferenceAVLTreeMap<V> clone() {
      Char2ReferenceAVLTreeMap<V> c;
      try {
         c = (Char2ReferenceAVLTreeMap<V>)super.clone();
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
         Char2ReferenceAVLTreeMap.Entry<V> rp = new Char2ReferenceAVLTreeMap.Entry<>();
         Char2ReferenceAVLTreeMap.Entry<V> rq = new Char2ReferenceAVLTreeMap.Entry<>();
         Char2ReferenceAVLTreeMap.Entry<V> p = rp;
         rp.left(this.tree);
         Char2ReferenceAVLTreeMap.Entry<V> q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Char2ReferenceAVLTreeMap.Entry<V> e = p.left.clone();
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
               Char2ReferenceAVLTreeMap.Entry<V> e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Char2ReferenceAVLTreeMap<V>.EntryIterator i = new Char2ReferenceAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Char2ReferenceAVLTreeMap.Entry<V> e = i.nextEntry();
         s.writeChar(e.key);
         s.writeObject(e.value);
      }
   }

   private Char2ReferenceAVLTreeMap.Entry<V> readTree(
      ObjectInputStream s, int n, Char2ReferenceAVLTreeMap.Entry<V> pred, Char2ReferenceAVLTreeMap.Entry<V> succ
   ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Char2ReferenceAVLTreeMap.Entry<V> top = new Char2ReferenceAVLTreeMap.Entry<>(s.readChar(), (V)s.readObject());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Char2ReferenceAVLTreeMap.Entry<V> top = new Char2ReferenceAVLTreeMap.Entry<>(s.readChar(), (V)s.readObject());
         top.right(new Char2ReferenceAVLTreeMap.Entry<>(s.readChar(), (V)s.readObject()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Char2ReferenceAVLTreeMap.Entry<V> top = new Char2ReferenceAVLTreeMap.Entry<>();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readChar();
         top.value = (V)s.readObject();
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
         Char2ReferenceAVLTreeMap.Entry<V> e = this.tree;

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

   private static final class Entry<V> extends AbstractChar2ReferenceMap.BasicEntry<V> implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Char2ReferenceAVLTreeMap.Entry<V> left;
      Char2ReferenceAVLTreeMap.Entry<V> right;
      int info;

      Entry() {
         super('\u0000', null);
      }

      Entry(char k, V v) {
         super(k, v);
         this.info = -1073741824;
      }

      Char2ReferenceAVLTreeMap.Entry<V> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Char2ReferenceAVLTreeMap.Entry<V> right() {
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

      void pred(Char2ReferenceAVLTreeMap.Entry<V> pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Char2ReferenceAVLTreeMap.Entry<V> succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Char2ReferenceAVLTreeMap.Entry<V> left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Char2ReferenceAVLTreeMap.Entry<V> right) {
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

      Char2ReferenceAVLTreeMap.Entry<V> next() {
         Char2ReferenceAVLTreeMap.Entry<V> next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Char2ReferenceAVLTreeMap.Entry<V> prev() {
         Char2ReferenceAVLTreeMap.Entry<V> prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public V setValue(V value) {
         V oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Char2ReferenceAVLTreeMap.Entry<V> clone() {
         Char2ReferenceAVLTreeMap.Entry<V> c;
         try {
            c = (Char2ReferenceAVLTreeMap.Entry<V>)super.clone();
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
            java.util.Map.Entry<Character, V> e = (java.util.Map.Entry<Character, V>)o;
            return this.key == e.getKey() && this.value == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Char2ReferenceAVLTreeMap<V>.TreeIterator implements ObjectListIterator<Char2ReferenceMap.Entry<V>> {
      EntryIterator() {
      }

      EntryIterator(char k) {
         super(k);
      }

      public Char2ReferenceMap.Entry<V> next() {
         return this.nextEntry();
      }

      public Char2ReferenceMap.Entry<V> previous() {
         return this.previousEntry();
      }

      public void set(Char2ReferenceMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Char2ReferenceMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Char2ReferenceAVLTreeMap<V>.TreeIterator implements CharListIterator {
      public KeyIterator() {
      }

      public KeyIterator(char k) {
         super(k);
      }

      @Override
      public char nextChar() {
         return this.nextEntry().key;
      }

      @Override
      public char previousChar() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractChar2ReferenceSortedMap<V>.KeySet {
      private KeySet() {
      }

      @Override
      public CharBidirectionalIterator iterator() {
         return Char2ReferenceAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public CharBidirectionalIterator iterator(char from) {
         return Char2ReferenceAVLTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractChar2ReferenceSortedMap<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      char from;
      char to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Char2ReferenceMap.Entry<V>> entries;
      protected transient CharSortedSet keys;
      protected transient ReferenceCollection<V> values;

      public Submap(char from, boolean bottom, char to, boolean top) {
         if (!bottom && !top && Char2ReferenceAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Char2ReferenceAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator i = new Char2ReferenceAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(char k) {
         return (this.bottom || Char2ReferenceAVLTreeMap.this.compare(k, this.from) >= 0)
            && (this.top || Char2ReferenceAVLTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Char2ReferenceMap.Entry<V>> char2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Char2ReferenceMap.Entry<V>>() {
               @Override
               public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> iterator(Char2ReferenceMap.Entry<V> from) {
                  return Submap.this.new SubmapEntryIterator(from.getCharKey());
               }

               @Override
               public Comparator<? super Char2ReferenceMap.Entry<V>> comparator() {
                  return Char2ReferenceAVLTreeMap.this.char2ReferenceEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() != null && e.getKey() instanceof Character) {
                        Char2ReferenceAVLTreeMap.Entry<V> f = Char2ReferenceAVLTreeMap.this.findKey((Character)e.getKey());
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
                     if (e.getKey() != null && e.getKey() instanceof Character) {
                        Char2ReferenceAVLTreeMap.Entry<V> f = Char2ReferenceAVLTreeMap.this.findKey((Character)e.getKey());
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

               public Char2ReferenceMap.Entry<V> first() {
                  return Submap.this.firstEntry();
               }

               public Char2ReferenceMap.Entry<V> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Char2ReferenceMap.Entry<V>> subSet(Char2ReferenceMap.Entry<V> from, Char2ReferenceMap.Entry<V> to) {
                  return Submap.this.subMap(from.getCharKey(), to.getCharKey()).char2ReferenceEntrySet();
               }

               public ObjectSortedSet<Char2ReferenceMap.Entry<V>> headSet(Char2ReferenceMap.Entry<V> to) {
                  return Submap.this.headMap(to.getCharKey()).char2ReferenceEntrySet();
               }

               public ObjectSortedSet<Char2ReferenceMap.Entry<V>> tailSet(Char2ReferenceMap.Entry<V> from) {
                  return Submap.this.tailMap(from.getCharKey()).char2ReferenceEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Char2ReferenceAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public ReferenceCollection<V> values() {
         if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>() {
               @Override
               public ObjectIterator<V> iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(Object k) {
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
      public boolean containsKey(char k) {
         return this.in(k) && Char2ReferenceAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(Object v) {
         Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator i = new Char2ReferenceAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            Object ev = i.nextEntry().value;
            if (ev == v) {
               return true;
            }
         }

         return false;
      }

      @Override
      public V get(char k) {
         Char2ReferenceAVLTreeMap.Entry<V> e;
         return this.in(k) && (e = Char2ReferenceAVLTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public V put(char k, V v) {
         Char2ReferenceAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            V oldValue = Char2ReferenceAVLTreeMap.this.put(k, v);
            return Char2ReferenceAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public V remove(char k) {
         Char2ReferenceAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            V oldValue = Char2ReferenceAVLTreeMap.this.remove(k);
            return Char2ReferenceAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator i = new Char2ReferenceAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Char2ReferenceAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public CharComparator comparator() {
         return Char2ReferenceAVLTreeMap.this.actualComparator;
      }

      @Override
      public Char2ReferenceSortedMap<V> headMap(char to) {
         if (this.top) {
            return Char2ReferenceAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Char2ReferenceAVLTreeMap.this.compare(to, this.to) < 0 ? Char2ReferenceAVLTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Char2ReferenceSortedMap<V> tailMap(char from) {
         if (this.bottom) {
            return Char2ReferenceAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Char2ReferenceAVLTreeMap.this.compare(from, this.from) > 0 ? Char2ReferenceAVLTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Char2ReferenceSortedMap<V> subMap(char from, char to) {
         if (this.top && this.bottom) {
            return Char2ReferenceAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Char2ReferenceAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Char2ReferenceAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Char2ReferenceAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Char2ReferenceAVLTreeMap.Entry<V> firstEntry() {
         if (Char2ReferenceAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Char2ReferenceAVLTreeMap.Entry<V> e;
            if (this.bottom) {
               e = Char2ReferenceAVLTreeMap.this.firstEntry;
            } else {
               e = Char2ReferenceAVLTreeMap.this.locateKey(this.from);
               if (Char2ReferenceAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Char2ReferenceAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Char2ReferenceAVLTreeMap.Entry<V> lastEntry() {
         if (Char2ReferenceAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Char2ReferenceAVLTreeMap.Entry<V> e;
            if (this.top) {
               e = Char2ReferenceAVLTreeMap.this.lastEntry;
            } else {
               e = Char2ReferenceAVLTreeMap.this.locateKey(this.to);
               if (Char2ReferenceAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Char2ReferenceAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public char firstCharKey() {
         Char2ReferenceAVLTreeMap.Entry<V> e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public char lastCharKey() {
         Char2ReferenceAVLTreeMap.Entry<V> e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractChar2ReferenceSortedMap<V>.KeySet {
         private KeySet() {
         }

         @Override
         public CharBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public CharBidirectionalIterator iterator(char from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<Char2ReferenceMap.Entry<V>> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(char k) {
            super(k);
         }

         public Char2ReferenceMap.Entry<V> next() {
            return this.nextEntry();
         }

         public Char2ReferenceMap.Entry<V> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Char2ReferenceAVLTreeMap<V>.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(char k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Char2ReferenceAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Char2ReferenceAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Char2ReferenceAVLTreeMap.this.locateKey(k);
                  if (Char2ReferenceAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Char2ReferenceAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Char2ReferenceAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator implements CharListIterator {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(char from) {
            super(from);
         }

         @Override
         public char nextChar() {
            return this.nextEntry().key;
         }

         @Override
         public char previousChar() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<V> {
         private SubmapValueIterator() {
         }

         @Override
         public V next() {
            return this.nextEntry().value;
         }

         @Override
         public V previous() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Char2ReferenceAVLTreeMap.Entry<V> prev;
      Char2ReferenceAVLTreeMap.Entry<V> next;
      Char2ReferenceAVLTreeMap.Entry<V> curr;
      int index = 0;

      TreeIterator() {
         this.next = Char2ReferenceAVLTreeMap.this.firstEntry;
      }

      TreeIterator(char k) {
         if ((this.next = Char2ReferenceAVLTreeMap.this.locateKey(k)) != null) {
            if (Char2ReferenceAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Char2ReferenceAVLTreeMap.Entry<V> nextEntry() {
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

      Char2ReferenceAVLTreeMap.Entry<V> previousEntry() {
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
            Char2ReferenceAVLTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Char2ReferenceAVLTreeMap<V>.TreeIterator implements ObjectListIterator<V> {
      private ValueIterator() {
      }

      @Override
      public V next() {
         return this.nextEntry().value;
      }

      @Override
      public V previous() {
         return this.previousEntry().value;
      }
   }
}
