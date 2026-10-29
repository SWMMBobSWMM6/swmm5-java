// R.java - C runtime layer for the Java port of EPA SWMM 5.2.4.
//
// Reproduces the glibc and x86-64 behaviour SWMM's output depends on: printf formatting with glibc's
// rounding (round half to even on the exact binary value, done here with BigDecimal), strtod/sscanf
// parsing, NUL-terminated byte strings, stdio files with C's positioning and end-of-file rules, and x86's
// float-to-integer conversion. The math functions are in Glibm.java (bit-exact ports of glibc 2.39's FMA
// variants). Written by Claude (Anthropic); see AI-USE.md.
package swmm5;

import java.io.*;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

/** C char*: a byte array plus an offset */
final class CPtr {
    final byte[] b;
    final int o;
    CPtr(byte[] b, int o) { this.b = b; this.o = o; }
    int g(int i) { return b[o + i]; }                      // signed char, as x86 C
    int s(int i, int v) { b[o + i] = (byte) v; return v; }
    CPtr add(int n) { return new CPtr(b, o + n); }
    CPtr add(long n) { return new CPtr(b, o + (int) n); }
}

/** C FILE*: positioned reads and writes over a RandomAccessFile, with C's end-of-file flag */
final class CFile {
    static final int BUFSZ = 1 << 16;
    final RandomAccessFile raf;
    final OutputStream std;
    long pos = 0;
    boolean eof = false;
    byte[] wbuf; int wlen = 0; long wpos = 0;
    byte[] rbuf; long rstart = 0; int rlen = 0;
    CFile(RandomAccessFile raf, OutputStream std) { this.raf = raf; this.std = std; }
    void flush() {
        if (wlen == 0) return;
        try {
            if (std != null) { std.write(wbuf, 0, wlen); std.flush(); }
            else { raf.seek(wpos); raf.write(wbuf, 0, wlen); }
        } catch (IOException e) { throw new UncheckedIOException(e); }
        wlen = 0;
    }
    void write(byte[] src, int off, int n) {
        rlen = 0;
        if (wbuf == null) wbuf = new byte[BUFSZ];
        if (wlen > 0 && pos != wpos + wlen) flush();
        if (wlen == 0) wpos = pos;
        if (wlen + n > BUFSZ) {
            flush();
            if (n > BUFSZ) {
                try {
                    if (std != null) std.write(src, off, n);
                    else { raf.seek(pos); raf.write(src, off, n); }
                } catch (IOException e) { throw new UncheckedIOException(e); }
                pos += n;
                return;
            }
            wpos = pos;
        }
        System.arraycopy(src, off, wbuf, wlen, n);
        wlen += n;
        pos += n;
    }
    int read(byte[] dst, int off, int n) {
        flush();
        int got = 0;
        while (got < n) {
            if (rlen > 0 && pos >= rstart && pos < rstart + rlen) {
                int a = (int) (pos - rstart);
                int m = Math.min(n - got, rlen - a);
                System.arraycopy(rbuf, a, dst, off + got, m);
                got += m; pos += m;
                continue;
            }
            if (rbuf == null) rbuf = new byte[BUFSZ];
            rstart = pos;
            try {
                raf.seek(pos);
                int r = raf.read(rbuf, 0, BUFSZ);
                rlen = r < 0 ? 0 : r;
            } catch (IOException e) { throw new UncheckedIOException(e); }
            if (rlen == 0) break;
        }
        return got;
    }
    int getc() {
        if (wlen == 0 && rlen > 0 && pos >= rstart && pos < rstart + rlen) return rbuf[(int) (pos++ - rstart)] & 255;
        byte[] t = new byte[1];
        return read(t, 0, 1) == 1 ? t[0] & 255 : -1;
    }
    long size() {
        flush();
        try { return raf.length(); } catch (IOException e) { throw new UncheckedIOException(e); }
    }
}

final class R {
    private R() {}

    // ------------------------------------------------------------ arithmetic with C semantics
    static int D2I(double x) {                    // (int)x on x86-64: NaN or out of range gives INT_MIN
        if (!(x > -2147483649.0 && x < 2147483648.0)) return Integer.MIN_VALUE;
        return (int) x;
    }
    static long D2L(double x) {
        if (!(x > -9223372036854775809.0 && x < 9223372036854775808.0)) return Long.MIN_VALUE;
        return (long) x;
    }
    static double post(DRef r, int d) { double o = r.get(); r.set(o + d); return o; }
    static float post(FRef r, int d) { float o = r.get(); r.set(o + d); return o; }
    static long post(LRef r, int d) { long o = r.get(); r.set(o + d); return o; }
    static void discard(Object o) {}
    static <T> T comma(Object a, T b) { return b; }
    static <T> T make(T o, Consumer<T> init) { init.accept(o); return o; }
    static void free(Object o) {}

    // ------------------------------------------------------------ strings and memory
    static CPtr lit(String s) {
        byte[] b = new byte[s.length() + 1];
        for (int i = 0; i < s.length(); i++) b[i] = (byte) s.charAt(i);
        return new CPtr(b, 0);
    }
    static CPtr buf(int n) { return new CPtr(new byte[Math.max(n, 1)], 0); }
    static CPtr buf(long n) { return buf((int) n); }
    static CPtr bufFrom(CPtr src, int n) {
        CPtr p = buf(n);
        int m = Math.min(strlenI(src), n);
        System.arraycopy(src.b, src.o, p.b, 0, m);
        return p;
    }
    static CPtr bufOf(int n, int[] vals) {
        CPtr p = buf(n);
        for (int i = 0; i < vals.length; i++) p.b[i] = (byte) vals[i];
        return p;
    }
    static CPtr[] bufs(int n, int m) { CPtr[] a = new CPtr[n]; for (int i = 0; i < n; i++) a[i] = buf(m); return a; }
    static CPtr copyBuf(CPtr p) { return new CPtr(p.b.clone(), p.o); }
    static <T> T[] fill(T[] a, Supplier<T> mk) { for (int i = 0; i < a.length; i++) a[i] = mk.get(); return a; }
    static double[][] cloneGrid(double[][] g) { double[][] r = new double[g.length][]; for (int i = 0; i < g.length; i++) r[i] = g[i].clone(); return r; }
    static int[][] cloneGrid(int[][] g) { int[][] r = new int[g.length][]; for (int i = 0; i < g.length; i++) r[i] = g[i].clone(); return r; }
    static float[][] cloneGrid(float[][] g) { float[][] r = new float[g.length][]; for (int i = 0; i < g.length; i++) r[i] = g[i].clone(); return r; }
    static long[][] cloneGrid(long[][] g) { long[][] r = new long[g.length][]; for (int i = 0; i < g.length; i++) r[i] = g[i].clone(); return r; }
    static CPtr[] cloneGrid(CPtr[] g) { CPtr[] r = new CPtr[g.length]; for (int i = 0; i < g.length; i++) r[i] = copyBuf(g[i]); return r; }
    @SuppressWarnings("unchecked")
    static <T> T[] copyRecs(T[] a, T[] out) {
        for (int i = 0; i < a.length; i++) {
            try { out[i] = (T) a[i].getClass().getDeclaredMethod("copy").invoke(a[i]); }
            catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
        }
        return out;
    }
    static boolean peq(CPtr a, CPtr b) { return a == b || (a != null && b != null && a.b == b.b && a.o == b.o); }

    static CPtr realloc(CPtr p, int n) {
        CPtr q = buf(n);
        if (p != null) System.arraycopy(p.b, p.o, q.b, 0, Math.min(n, p.b.length - p.o));
        return q;
    }
    static double[] realloc(double[] a, int n) { return a == null ? new double[n] : Arrays.copyOf(a, n); }
    static int[] realloc(int[] a, int n) { return a == null ? new int[n] : Arrays.copyOf(a, n); }
    static long[] realloc(long[] a, int n) { return a == null ? new long[n] : Arrays.copyOf(a, n); }
    static float[] realloc(float[] a, int n) { return a == null ? new float[n] : Arrays.copyOf(a, n); }
    static <T> T[] realloc(T[] a, int n) { return Arrays.copyOf(a, n); }
    static <T> T[] realloc(T[] a, int n, Supplier<T> mk) {
        T[] r = Arrays.copyOf(a, n);
        for (int i = a.length; i < n; i++) r[i] = mk.get();
        return r;
    }
    static void memset(CPtr p, int v, long n) { Arrays.fill(p.b, p.o, p.o + (int) n, (byte) v); }
    static void memset(double[] a, int v, long n) { Arrays.fill(a, 0, (int) Math.min(a.length, n / 8), 0.0); }
    static void memset(int[] a, int v, long n) { Arrays.fill(a, 0, (int) Math.min(a.length, n / 4), 0); }
    static void memset(float[] a, int v, long n) { Arrays.fill(a, 0, (int) Math.min(a.length, n / 4), 0f); }
    static void memset(long[] a, int v, long n) { Arrays.fill(a, 0, (int) Math.min(a.length, n / 8), 0L); }

    static int strlenI(CPtr p) { int e = p.o; byte[] b = p.b; while (b[e] != 0) e++; return e - p.o; }
    static long strlen(CPtr p) { return strlenI(p); }
    static byte[] bytesOf(CPtr p) { return Arrays.copyOfRange(p.b, p.o, p.o + strlenI(p)); }
    static String strOf(CPtr p) { return new String(p.b, p.o, strlenI(p), StandardCharsets.ISO_8859_1); }
    static CPtr strcpy(CPtr d, CPtr s) { System.arraycopy(s.b, s.o, d.b, d.o, strlenI(s) + 1); return d; }
    static CPtr strcat(CPtr d, CPtr s) { strcpy(d.add(strlenI(d)), s); return d; }
    static int strcmp(CPtr a, CPtr b) {
        int i = a.o, j = b.o;
        for (;;) {
            int c = a.b[i] & 255, d = b.b[j] & 255;
            if (c != d) return c - d;
            if (c == 0) return 0;
            i++; j++;
        }
    }
    static CPtr strchr(CPtr s, int c) {
        c &= 255;
        for (int i = s.o; ; i++) {
            if ((s.b[i] & 255) == c) return new CPtr(s.b, i);
            if (s.b[i] == 0) return null;
        }
    }
    static CPtr strrchr(CPtr s, int c) {
        c &= 255;
        int e = s.o + strlenI(s);
        if (c == 0) return new CPtr(s.b, e);
        for (int i = e - 1; i >= s.o; i--) if ((s.b[i] & 255) == c) return new CPtr(s.b, i);
        return null;
    }
    static CPtr strstr(CPtr h, CPtr n) {
        int hl = strlenI(h), nl = strlenI(n);
        outer:
        for (int i = 0; i + nl <= hl; i++) {
            for (int k = 0; k < nl; k++) if (h.b[h.o + i + k] != n.b[n.o + k]) continue outer;
            return new CPtr(h.b, h.o + i);
        }
        return null;
    }
    static long strcspn(CPtr s, CPtr rej) {
        byte[] sb = bytesOf(s), rb = bytesOf(rej);
        for (int i = 0; i < sb.length; i++) for (byte r : rb) if (sb[i] == r) return i;
        return sb.length;
    }
    private static CPtr tokNext;
    private static boolean inSet(byte c, byte[] set) { for (byte x : set) if (x == c) return true; return false; }
    static CPtr strtok(CPtr s, CPtr delim) {
        if (s != null) tokNext = s;
        CPtr p = tokNext;
        if (p == null) return null;
        byte[] ds = bytesOf(delim);
        byte[] b = p.b;
        int i = p.o;
        while (b[i] != 0 && inSet(b[i], ds)) i++;
        if (b[i] == 0) { tokNext = null; return null; }
        int start = i;
        while (b[i] != 0 && !inSet(b[i], ds)) i++;
        if (b[i] != 0) { b[i] = 0; tokNext = new CPtr(b, i + 1); }
        else tokNext = null;
        return new CPtr(b, start);
    }
    static int toupper(int c) { return c >= 97 && c <= 122 ? c - 32 : c; }

    static boolean isSpace(int c) { return c == 32 || (c >= 9 && c <= 13); }
    static int low(int c) { return c >= 65 && c <= 90 ? c | 0x20 : c; }
    static final double DEFAULT_NAN = Double.longBitsToDouble(0xfff8000000000000L);
    static final double POS_NAN = Double.longBitsToDouble(0x7ff8000000000000L);

    /** parse a C floating number at b[i..] (at most maxw chars); sets scanEnd (-1: no number) */
    private static int scanEnd;
    private static String scanText;
    private static double scanDouble(byte[] b, int i, int maxw, boolean skipSpace) {
        int n = b.length;
        if (skipSpace) while (i < n && isSpace(b[i])) i++;
        final int lim = maxw < 0 ? n : i + maxw;
        IntUnaryOperator g = k -> (k < lim && k < n ? b[k] & 255 : 0);
        boolean neg = false;
        if (g.applyAsInt(i) == 43 || g.applyAsInt(i) == 45) { neg = g.applyAsInt(i) == 45; i++; }
        if (low(g.applyAsInt(i)) == 105 && low(g.applyAsInt(i + 1)) == 110 && low(g.applyAsInt(i + 2)) == 102) {
            i += 3;
            if (low(g.applyAsInt(i)) == 105 && low(g.applyAsInt(i + 1)) == 110 && low(g.applyAsInt(i + 2)) == 105
                && low(g.applyAsInt(i + 3)) == 116 && low(g.applyAsInt(i + 4)) == 121) i += 5;
            scanEnd = i; scanText = null;
            return neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }
        if (low(g.applyAsInt(i)) == 110 && low(g.applyAsInt(i + 1)) == 97 && low(g.applyAsInt(i + 2)) == 110) {
            i += 3;
            if (g.applyAsInt(i) == 40) {
                int k = i + 1;
                while (Character.isLetterOrDigit(g.applyAsInt(k)) || g.applyAsInt(k) == 95) k++;
                if (g.applyAsInt(k) == 41) i = k + 1;
            }
            scanEnd = i; scanText = null;
            return neg ? DEFAULT_NAN : POS_NAN;
        }
        int ms = i, nd = 0;
        while (g.applyAsInt(i) >= 48 && g.applyAsInt(i) <= 57) { i++; nd++; }
        if (g.applyAsInt(i) == 46) { i++; while (g.applyAsInt(i) >= 48 && g.applyAsInt(i) <= 57) { i++; nd++; } }
        if (nd == 0) { scanEnd = -1; return 0.0; }
        if (low(g.applyAsInt(i)) == 101) {
            int k = i + 1;
            if (g.applyAsInt(k) == 43 || g.applyAsInt(k) == 45) k++;
            if (g.applyAsInt(k) >= 48 && g.applyAsInt(k) <= 57) { while (g.applyAsInt(k) >= 48 && g.applyAsInt(k) <= 57) k++; i = k; }
        }
        scanText = (neg ? "-" : "") + new String(b, ms, i - ms, StandardCharsets.ISO_8859_1);
        scanEnd = i;
        double v = Double.parseDouble(scanText);    // correctly rounded, as glibc's strtod
        return v;
    }
    static double strtod(CPtr s, ORef<CPtr> end) {
        double v = scanDouble(s.b, s.o, -1, true);
        if (end != null) end.set(scanEnd < 0 ? s : new CPtr(s.b, scanEnd));
        return scanEnd < 0 ? 0.0 : v;
    }
    static double atof(CPtr s) { return strtod(s, null); }
    static int atoi(CPtr s) {
        byte[] b = s.b;
        int i = s.o;
        while (b[i] != 0 && isSpace(b[i])) i++;
        boolean neg = false;
        if (b[i] == 43 || b[i] == 45) { neg = b[i] == 45; i++; }
        long v = 0;
        boolean over = false;
        while (b[i] >= 48 && b[i] <= 57) {
            if (v > (Long.MAX_VALUE - (b[i] - 48)) / 10) over = true;
            else v = v * 10 + (b[i] - 48);
            i++;
        }
        if (over) v = neg ? Long.MIN_VALUE : Long.MAX_VALUE;
        else if (neg) v = -v;
        return (int) v;                          // strtol's long, then (int): keeps the low 32 bits
    }

    // ------------------------------------------------------------ sscanf
    static int sscanf(CPtr s, CPtr fmt, Object... args) {
        byte[] b = s.b;
        int i = s.o;
        byte[] f = bytesOf(fmt);
        int k = 0, ai = 0, count = 0;
        boolean anyInput = false;
        int n = b.length, nf = f.length;
        while (k < nf) {
            int fc = f[k] & 255;
            if (isSpace(fc)) {
                while (k < nf && isSpace(f[k])) k++;
                while (ch(b, i) != 0 && isSpace(ch(b, i))) i++;
                continue;
            }
            if (fc != 37) {
                if (ch(b, i) == 0) return anyInput || count > 0 ? count : -1;
                if (ch(b, i) != fc) return count;
                i++; k++;
                continue;
            }
            k++;
            if (f[k] == 37) {
                while (ch(b, i) != 0 && isSpace(ch(b, i))) i++;
                if (ch(b, i) != 37) return count;
                i++; k++;
                continue;
            }
            boolean suppress = false;
            if (f[k] == 42) { suppress = true; k++; }
            int width = 0;
            while (k < nf && f[k] >= 48 && f[k] <= 57) { width = width * 10 + f[k] - 48; k++; }
            int lmod = 0;
            while (k < nf && (f[k] == 108 || f[k] == 104 || f[k] == 76)) { if (f[k] == 108) lmod++; k++; }
            int conv = f[k] & 255;
            k++;
            if (conv != 99 && conv != 91 && conv != 110) while (ch(b, i) != 0 && isSpace(ch(b, i))) i++;
            if (ch(b, i) == 0 && conv != 110) return count == 0 && !anyInput ? -1 : count;
            if (conv == 100 || conv == 105 || conv == 117) {
                int j = i;
                int lim = width != 0 ? i + width : n;
                boolean neg = false;
                if (j < lim && (ch(b, j) == 43 || ch(b, j) == 45)) { neg = ch(b, j) == 45; j++; }
                int ds = j;
                long v = 0;
                boolean over = false;
                while (j < lim && ch(b, j) >= 48 && ch(b, j) <= 57) {
                    int dgt = ch(b, j) - 48;
                    if (v > (Long.MAX_VALUE - dgt) / 10) over = true; else v = v * 10 + dgt;
                    j++;
                }
                if (j == ds) return count;
                i = j;
                anyInput = true;
                if (over) v = neg ? Long.MIN_VALUE : Long.MAX_VALUE;
                else if (neg) v = -v;
                if (!suppress) {
                    Object a = args[ai++];
                    if (a instanceof IRef) ((IRef) a).set((int) v);
                    else if (a instanceof LRef) ((LRef) a).set(v);
                    else throw new IllegalArgumentException("sscanf %d target " + a);
                    count++;
                }
            } else if (conv == 102 || conv == 101 || conv == 103) {
                double v = scanDouble(b, i, width != 0 ? width : -1, false);
                if (scanEnd < 0) return count;
                i = scanEnd;
                anyInput = true;
                if (!suppress) {
                    Object a = args[ai++];
                    if (a instanceof DRef) ((DRef) a).set(v);
                    else if (a instanceof FRef) ((FRef) a).set(scanText != null ? Float.parseFloat(scanText) : (float) v);
                    else throw new IllegalArgumentException("sscanf %f target " + a);
                    count++;
                }
            } else if (conv == 115) {
                int j = i;
                int w = width != 0 ? width : 1 << 30;
                int m = 0;
                while (ch(b, j) != 0 && !isSpace(ch(b, j)) && m < w) { j++; m++; }
                if (!suppress) {
                    CPtr d = (CPtr) args[ai++];
                    System.arraycopy(b, i, d.b, d.o, m);
                    d.b[d.o + m] = 0;
                    count++;
                }
                i = j;
                anyInput = true;
            } else if (conv == 99) {
                int cnt = width != 0 ? width : 1;
                if (ch(b, i) == 0) return count == 0 && !anyInput ? -1 : count;
                Object a = suppress ? null : args[ai++];
                int t = 0;
                while (t < cnt && ch(b, i) != 0) {
                    if (!suppress) {
                        if (a instanceof CPtr) ((CPtr) a).b[((CPtr) a).o + t] = b[i];
                        else ((IRef) a).set(b[i]);
                    }
                    i++; t++;
                }
                anyInput = true;
                if (!suppress) count++;
            } else {
                throw new IllegalArgumentException("sscanf %" + (char) conv);
            }
        }
        return count;
    }
    private static int ch(byte[] b, int j) { return j < b.length ? b[j] & 255 : 0; }

    // ------------------------------------------------------------ printf
    private static String fixedDigits(double a, int prec, boolean alt) {
        String s = new BigDecimal(a).setScale(prec, RoundingMode.HALF_EVEN).toPlainString();
        if (prec == 0 && alt) s += ".";
        return s;
    }
    /** digits of a rounded to prec+1 significant digits, and the decimal exponent */
    private static String expRaw(double a, int prec, int[] X) {
        if (a == 0.0) { X[0] = 0; return "0".repeat(prec + 1); }
        BigDecimal r = new BigDecimal(a).round(new MathContext(prec + 1, RoundingMode.HALF_EVEN));
        String d = r.unscaledValue().toString();
        int e = d.length() - 1 - r.scale();
        if (d.length() < prec + 1) d = d + "0".repeat(prec + 1 - d.length());
        else if (d.length() > prec + 1) d = d.substring(0, prec + 1);
        X[0] = e;
        return d;
    }
    private static String expDigits(double a, int prec, boolean alt, boolean upper) {
        int[] X = new int[1];
        String d = expRaw(a, prec, X);
        String mant = d.substring(0, 1) + (prec > 0 ? "." + d.substring(1) : (alt ? "." : ""));
        int ax = Math.abs(X[0]);
        return mant + (upper ? "E" : "e") + (X[0] < 0 ? "-" : "+") + (ax < 10 ? "0" + ax : String.valueOf(ax));
    }
    private static String formatFloat(double a, int conv, int prec, boolean alt) {
        int lc = conv | 0x20;
        if (lc == 'f') return fixedDigits(a, prec, alt);
        if (lc == 'e') return expDigits(a, prec, alt, conv == 'E');
        int P = prec == 0 ? 1 : prec;
        int[] X = new int[1];
        if (a != 0.0) expRaw(a, P - 1, X);
        String s = (P > X[0] && X[0] >= -4) ? fixedDigits(a, P - 1 - X[0], alt) : expDigits(a, P - 1, alt, conv == 'G');
        if (!alt) {
            int ep = Math.max(s.indexOf('e'), s.indexOf('E'));
            String mant = ep >= 0 ? s.substring(0, ep) : s;
            String ex = ep >= 0 ? s.substring(ep) : "";
            if (mant.indexOf('.') >= 0) {
                int e = mant.length();
                while (e > 0 && mant.charAt(e - 1) == '0') e--;
                if (e > 0 && mant.charAt(e - 1) == '.') e--;
                mant = mant.substring(0, e);
            }
            s = mant + ex;
        }
        return s;
    }
    private static long toLong(Object a) {
        if (a instanceof Integer) return (Integer) a;
        if (a instanceof Long) return (Long) a;
        if (a instanceof Double) return (long) (double) (Double) a;
        if (a instanceof Float) return (long) (float) (Float) a;
        throw new IllegalArgumentException("printf integer arg " + a);
    }
    private static double toDouble(Object a) {
        if (a instanceof Double) return (Double) a;
        if (a instanceof Float) return (Float) a;
        if (a instanceof Integer) return (Integer) a;
        if (a instanceof Long) return (Long) a;
        throw new IllegalArgumentException("printf float arg " + a);
    }
    static String format(CPtr fmt, Object[] args) {
        byte[] f = fmt.b;
        int k = fmt.o, ai = 0;
        StringBuilder o = new StringBuilder();
        while (f[k] != 0) {
            if (f[k] != 37) { o.append((char) (f[k] & 255)); k++; continue; }
            k++;
            if (f[k] == 37) { o.append('%'); k++; continue; }
            boolean left = false, plus = false, space = false, alt = false, zero = false;
            for (;; k++) {
                int c = f[k];
                if (c == '-') left = true;
                else if (c == '+') plus = true;
                else if (c == ' ') space = true;
                else if (c == '#') alt = true;
                else if (c == '0') zero = true;
                else break;
            }
            int width = 0, prec = -1;
            if (f[k] == '*') { width = (int) toLong(args[ai++]); if (width < 0) { left = true; width = -width; } k++; }
            else while (f[k] >= '0' && f[k] <= '9') { width = width * 10 + f[k] - '0'; k++; }
            if (f[k] == '.') {
                k++; prec = 0;
                if (f[k] == '*') { prec = (int) toLong(args[ai++]); if (prec < 0) prec = -1; k++; }
                else while (f[k] >= '0' && f[k] <= '9') { prec = prec * 10 + f[k] - '0'; k++; }
            }
            while (f[k] == 'l' || f[k] == 'h' || f[k] == 'L' || f[k] == 'z' || f[k] == 'j' || f[k] == 't' || f[k] == 'q') k++;
            int conv = f[k];
            k++;
            String body, sign = "";
            boolean numeric = true;
            switch (conv) {
                case 'd': case 'i': {
                    long v = toLong(args[ai++]);
                    if (v < 0) sign = "-"; else if (plus) sign = "+"; else if (space) sign = " ";
                    body = v == Long.MIN_VALUE ? "9223372036854775808" : String.valueOf(Math.abs(v));
                    if (prec >= 0) { zero = false; if (prec == 0 && v == 0) body = ""; while (body.length() < prec) body = "0" + body; }
                    break;
                }
                case 'u': case 'x': case 'X': case 'o': {
                    long v = toLong(args[ai++]);
                    if (args[ai - 1] instanceof Integer) v &= 0xFFFFFFFFL;
                    body = conv == 'u' ? Long.toUnsignedString(v) : conv == 'o' ? Long.toOctalString(v) : conv == 'x' ? Long.toHexString(v) : Long.toHexString(v).toUpperCase();
                    if (prec >= 0) { zero = false; if (prec == 0 && v == 0) body = ""; while (body.length() < prec) body = "0" + body; }
                    break;
                }
                case 'c': {
                    numeric = false;
                    body = String.valueOf((char) (toLong(args[ai++]) & 255));
                    break;
                }
                case 's': {
                    numeric = false;
                    CPtr p = (CPtr) args[ai++];
                    String s = p == null ? "(null)" : strOf(p);
                    if (prec >= 0 && s.length() > prec) s = s.substring(0, prec);
                    body = s;
                    break;
                }
                case 'f': case 'F': case 'e': case 'E': case 'g': case 'G': {
                    double v = toDouble(args[ai++]);
                    if (prec < 0) prec = 6;
                    if ((Double.doubleToRawLongBits(v) >>> 63) != 0) sign = "-"; else if (plus) sign = "+"; else if (space) sign = " ";
                    if (Double.isNaN(v) || Double.isInfinite(v)) {
                        body = Double.isNaN(v) ? "nan" : "inf";
                        if (conv == 'F' || conv == 'E' || conv == 'G') body = body.toUpperCase();
                        zero = false;
                    } else body = formatFloat(Math.abs(v), conv, prec, alt);
                    break;
                }
                default:
                    throw new IllegalArgumentException("printf %" + (char) conv);
            }
            int len = sign.length() + body.length();
            if (len >= width) o.append(sign).append(body);
            else if (left) o.append(sign).append(body).append(" ".repeat(width - len));
            else if (zero && numeric) o.append(sign).append("0".repeat(width - len)).append(body);
            else o.append(" ".repeat(width - len)).append(sign).append(body);
        }
        return o.toString();
    }
    static int fprintf(CFile f, CPtr fmt, Object... args) {
        String s = format(fmt, args);
        if (f == null) return -1;
        byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
        f.write(b, 0, b.length);
        return b.length;
    }
    static int snprintf(CPtr b, int n, CPtr fmt, Object... args) {
        String s = format(fmt, args);
        if (n > 0) {
            int m = Math.min(s.length(), n - 1);
            for (int i = 0; i < m; i++) b.b[b.o + i] = (byte) s.charAt(i);
            b.b[b.o + m] = 0;
        }
        return s.length();
    }

    // ------------------------------------------------------------ files
    static final CFile stdout = new CFile(null, new FileOutputStream(FileDescriptor.out));

    static CFile fopen(CPtr name, CPtr mode) {
        String fn = strOf(name);
        String m = strOf(mode).replace("t", "").replace("b", "");
        try {
            File file = new File(fn);
            if (m.startsWith("r") && !file.isFile()) return null;
            RandomAccessFile raf = new RandomAccessFile(file, m.equals("r") ? "r" : "rw");
            CFile f = new CFile(raf, null);
            if (m.startsWith("w")) raf.setLength(0);
            if (m.startsWith("a")) f.pos = raf.length();
            return f;
        } catch (IOException e) {
            return null;
        }
    }
    static int fclose(CFile f) {
        if (f == null) return -1;
        f.flush();
        if (f.raf != null) { try { f.raf.close(); } catch (IOException e) { return -1; } }
        return 0;
    }
    static int fflush(CFile f) { if (f != null) f.flush(); return 0; }
    static int feof(CFile f) { return f.eof ? 1 : 0; }
    static void rewind(CFile f) { f.flush(); f.pos = 0; f.eof = false; }
    static int fseek(CFile f, long off, int whence) {
        f.flush();
        long p = whence == 0 ? off : whence == 1 ? f.pos + off : f.size() + off;
        if (p < 0) return -1;
        f.pos = p;
        f.eof = false;
        return 0;
    }
    static long ftell(CFile f) { return f.pos; }
    static CPtr fgets(CPtr b, int n, CFile f) {
        int m = 0;
        while (m < n - 1) {
            int c = f.getc();
            if (c < 0) { f.eof = true; break; }
            b.b[b.o + m++] = (byte) c;
            if (c == 10) break;
        }
        if (m == 0) return null;
        b.b[b.o + m] = 0;
        return b;
    }
    static int remove(CPtr name) {
        try { Files.delete(Paths.get(strOf(name))); return 0; } catch (IOException e) { return -1; }
    }
    static int mkstemp(CPtr template) {
        int n = strlenI(template);
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = new Random();
        for (int t = 0; t < 1000; t++) {
            for (int i = n - 6; i < n; i++) template.s(i, chars.charAt(rnd.nextInt(chars.length())));
            try {
                Files.createFile(Paths.get(strOf(template)));
                return 3;
            } catch (FileAlreadyExistsException e) {
                continue;
            } catch (IOException e) {
                return -1;
            }
        }
        return -1;
    }
    static String realpathOf(String p) {
        try { return new File(p).toPath().toRealPath().toString(); }
        catch (IOException e) { return new File(p).getAbsolutePath(); }
    }

    // binary I/O: one value (its first `size` bytes, little-endian) or an array
    private static final byte[] io = new byte[8];
    private static void put(long bits, int n) { for (int i = 0; i < n; i++) io[i] = (byte) (bits >>> (8 * i)); }
    private static long get(int n) { long v = 0; for (int i = 0; i < n; i++) v |= (io[i] & 255L) << (8 * i); return v; }
    static long fwrite1(double v, int size, CFile f) { put(Double.doubleToRawLongBits(v), 8); f.write(io, 0, size); return 1; }
    static long fwrite1(float v, int size, CFile f) { put(Float.floatToRawIntBits(v) & 0xFFFFFFFFL, 4); f.write(io, 0, size); return 1; }
    static long fwrite1(int v, int size, CFile f) { put(v, 4); f.write(io, 0, size); return 1; }
    static long fwrite1(long v, int size, CFile f) { put(v, 8); f.write(io, 0, size); return 1; }
    static long fread1(DRef r, int size, CFile f) {
        put(Double.doubleToRawLongBits(r.get()), 8);
        if (f.read(io, 0, size) < size) { f.eof = true; return 0; }
        r.set(Double.longBitsToDouble(get(8)));
        return 1;
    }
    static long fread1(FRef r, int size, CFile f) {
        put(Float.floatToRawIntBits(r.get()), 4);
        if (f.read(io, 0, size) < size) { f.eof = true; return 0; }
        r.set(Float.intBitsToFloat((int) get(4)));
        return 1;
    }
    static long fread1(IRef r, int size, CFile f) {
        put(r.get(), 4);
        if (f.read(io, 0, size) < size) { f.eof = true; return 0; }
        r.set((int) get(4));
        return 1;
    }
    static long fread1(LRef r, int size, CFile f) {
        put(r.get(), 8);                           // a short read keeps the upper bytes of the old value
        if (f.read(io, 0, size) < size) { f.eof = true; return 0; }
        r.set(get(8));
        return 1;
    }
    static long fwrite(double[] a, int off, int n, CFile f) { for (int i = 0; i < n; i++) fwrite1(a[off + i], 8, f); return n; }
    static long fwrite(float[] a, int off, int n, CFile f) { for (int i = 0; i < n; i++) fwrite1(a[off + i], 4, f); return n; }
    static long fwrite(int[] a, int off, int n, CFile f) { for (int i = 0; i < n; i++) fwrite1(a[off + i], 4, f); return n; }
    static long fwrite(long[] a, int off, int n, CFile f) { for (int i = 0; i < n; i++) fwrite1(a[off + i], 8, f); return n; }
    static long fwrite(CPtr p, int off, int n, CFile f) { f.write(p.b, p.o + off, n); return n; }
    static long fread(double[] a, int off, int n, CFile f) {
        for (int i = 0; i < n; i++) { if (f.read(io, 0, 8) < 8) { f.eof = true; return i; } a[off + i] = Double.longBitsToDouble(get(8)); }
        return n;
    }
    static long fread(float[] a, int off, int n, CFile f) {
        for (int i = 0; i < n; i++) { if (f.read(io, 0, 4) < 4) { f.eof = true; return i; } a[off + i] = Float.intBitsToFloat((int) get(4)); }
        return n;
    }
    static long fread(int[] a, int off, int n, CFile f) {
        for (int i = 0; i < n; i++) { if (f.read(io, 0, 4) < 4) { f.eof = true; return i; } a[off + i] = (int) get(4); }
        return n;
    }
    static long fread(long[] a, int off, int n, CFile f) {
        for (int i = 0; i < n; i++) { if (f.read(io, 0, 8) < 8) { f.eof = true; return i; } a[off + i] = get(8); }
        return n;
    }
    static long fread(CPtr p, int off, int n, CFile f) {
        int got = f.read(p.b, p.o + off, n);
        if (got < n) f.eof = true;
        return got;
    }

    // ------------------------------------------------------------ time
    static long time() { return System.currentTimeMillis() / 1000; }
    static CPtr ctime(long t) {
        java.time.ZonedDateTime d = java.time.Instant.ofEpochSecond(t).atZone(java.time.ZoneId.systemDefault());
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] mons = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return lit(String.format("%s %s %2d %02d:%02d:%02d %d\n", days[d.getDayOfWeek().getValue() - 1], mons[d.getMonthValue() - 1],
                                 d.getDayOfMonth(), d.getHour(), d.getMinute(), d.getSecond(), d.getYear()));
    }
}
