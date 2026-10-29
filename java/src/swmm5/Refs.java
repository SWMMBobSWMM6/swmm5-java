// Refs.java - C's pointer-to-one-object, for the Java port of EPA SWMM 5.2.4. A scalar pointer parameter
// becomes a DRef/IRef/LRef/FRef (double/int/long/float) or an ORef<T>; the caller passes a box (a local
// whose address is taken), an array element, a struct field or a static variable.
// Written by Claude (Anthropic); see AI-USE.md.
package swmm5;

import java.util.function.*;

abstract class DRef { abstract double get(); abstract double set(double v); }
abstract class IRef { abstract int get(); abstract int set(int v); }
abstract class LRef { abstract long get(); abstract long set(long v); }
abstract class FRef { abstract float get(); abstract float set(float v); }
abstract class ORef<T> { abstract T get(); abstract T set(T v); }

final class DBox extends DRef {
    double v;
    DBox(double v) { this.v = v; }
    double get() { return v; }
    double set(double x) { return v = x; }
}
final class IBox extends IRef {
    int v;
    IBox(int v) { this.v = v; }
    int get() { return v; }
    int set(int x) { return v = x; }
}
final class LBox extends LRef {
    long v;
    LBox(long v) { this.v = v; }
    long get() { return v; }
    long set(long x) { return v = x; }
}
final class FBox extends FRef {
    float v;
    FBox(float v) { this.v = v; }
    float get() { return v; }
    float set(float x) { return v = x; }
}
final class OBox<T> extends ORef<T> {
    T v;
    OBox(T v) { this.v = v; }
    T get() { return v; }
    T set(T x) { return v = x; }
}

final class DElem extends DRef {
    final double[] a; final int i;
    DElem(double[] a, int i) { this.a = a; this.i = i; }
    double get() { return a[i]; }
    double set(double x) { return a[i] = x; }
}
final class IElem extends IRef {
    final int[] a; final int i;
    IElem(int[] a, int i) { this.a = a; this.i = i; }
    int get() { return a[i]; }
    int set(int x) { return a[i] = x; }
}
final class LElem extends LRef {
    final long[] a; final int i;
    LElem(long[] a, int i) { this.a = a; this.i = i; }
    long get() { return a[i]; }
    long set(long x) { return a[i] = x; }
}
final class FElem extends FRef {
    final float[] a; final int i;
    FElem(float[] a, int i) { this.a = a; this.i = i; }
    float get() { return a[i]; }
    float set(float x) { return a[i] = x; }
}
final class OElem<T> extends ORef<T> {
    final T[] a; final int i;
    OElem(T[] a, int i) { this.a = a; this.i = i; }
    T get() { return a[i]; }
    T set(T x) { return a[i] = x; }
}

interface FloatGetter<T> { float get(T o); }
interface FloatSetter<T> { void set(T o, float x); }

final class DField<T> extends DRef {
    final T o; final ToDoubleFunction<T> g; final ObjDoubleConsumer<T> s;
    DField(T o, ToDoubleFunction<T> g, ObjDoubleConsumer<T> s) { this.o = o; this.g = g; this.s = s; }
    double get() { return g.applyAsDouble(o); }
    double set(double x) { s.accept(o, x); return x; }
}
final class IField<T> extends IRef {
    final T o; final ToIntFunction<T> g; final ObjIntConsumer<T> s;
    IField(T o, ToIntFunction<T> g, ObjIntConsumer<T> s) { this.o = o; this.g = g; this.s = s; }
    int get() { return g.applyAsInt(o); }
    int set(int x) { s.accept(o, x); return x; }
}
final class LField<T> extends LRef {
    final T o; final ToLongFunction<T> g; final ObjLongConsumer<T> s;
    LField(T o, ToLongFunction<T> g, ObjLongConsumer<T> s) { this.o = o; this.g = g; this.s = s; }
    long get() { return g.applyAsLong(o); }
    long set(long x) { s.accept(o, x); return x; }
}
final class FField<T> extends FRef {
    final T o; final FloatGetter<T> g; final FloatSetter<T> s;
    FField(T o, FloatGetter<T> g, FloatSetter<T> s) { this.o = o; this.g = g; this.s = s; }
    float get() { return g.get(o); }
    float set(float x) { s.set(o, x); return x; }
}
final class OField<T, V> extends ORef<V> {
    final T o; final Function<T, V> g; final BiConsumer<T, V> s;
    OField(T o, Function<T, V> g, BiConsumer<T, V> s) { this.o = o; this.g = g; this.s = s; }
    V get() { return g.apply(o); }
    V set(V x) { s.accept(o, x); return x; }
}

interface FloatSupplier { float get(); }
interface FloatConsumer { void accept(float x); }

final class DAcc extends DRef {
    final DoubleSupplier g; final DoubleConsumer s;
    DAcc(DoubleSupplier g, DoubleConsumer s) { this.g = g; this.s = s; }
    double get() { return g.getAsDouble(); }
    double set(double x) { s.accept(x); return x; }
}
final class IAcc extends IRef {
    final IntSupplier g; final IntConsumer s;
    IAcc(IntSupplier g, IntConsumer s) { this.g = g; this.s = s; }
    int get() { return g.getAsInt(); }
    int set(int x) { s.accept(x); return x; }
}
final class LAcc extends LRef {
    final LongSupplier g; final LongConsumer s;
    LAcc(LongSupplier g, LongConsumer s) { this.g = g; this.s = s; }
    long get() { return g.getAsLong(); }
    long set(long x) { s.accept(x); return x; }
}
final class FAcc extends FRef {
    final FloatSupplier g; final FloatConsumer s;
    FAcc(FloatSupplier g, FloatConsumer s) { this.g = g; this.s = s; }
    float get() { return g.get(); }
    float set(float x) { s.accept(x); return x; }
}
final class OAcc<T> extends ORef<T> {
    final Supplier<T> g; final Consumer<T> s;
    OAcc(Supplier<T> g, Consumer<T> s) { this.g = g; this.s = s; }
    T get() { return g.get(); }
    T set(T x) { s.accept(x); return x; }
}

final class CharRef extends IRef {
    final CPtr p; final int i;
    CharRef(CPtr p, int i) { this.p = p; this.i = i; }
    int get() { return p.g(i); }
    int set(int x) { return p.s(i, x); }
}
