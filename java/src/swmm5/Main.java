// Main.java - command-line runner for the Java port: same arguments and console messages as EPA's
// run/main.c.   java -jar swmm5.jar <input file> <report file> [output file]
// Written by Claude (Anthropic); see AI-USE.md.
package swmm5;

public final class Main {
    private static void out(String s) {
        byte[] b = s.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        R.stdout.write(b, 0, b.length);
        R.stdout.flush();
    }

    public static void main(String[] argv) {
        int version = F_swmm5.swmm_getVersion();
        int vMajor = version / 10000;
        int vMinor = (version - 10000 * vMajor) / 1000;
        int vRelease = version - 10000 * vMajor - 1000 * vMinor;
        if (argv.length == 0) out("\nNot Enough Arguments (See Help --help)\n\n");
        else if (argv.length == 1) {
            String a = argv[0];
            if (a.equals("--help") || a.equals("-h"))
                out("\n\nSTORMWATER MANAGEMENT MODEL (SWMM) HELP\n\nCOMMANDS:\n\t--help (-h)       SWMM Help\n"
                    + "\t--version (-v)    Build Version\n\nRUNNING A SIMULATION:\n"
                    + "\t runswmm <input file> <report file> <optional output file>\n\n");
            else if (a.equals("--version") || a.equals("-v")) out("\n" + vMajor + "." + vMinor + "." + vRelease + "\n\n");
            else out("\nUnknown Argument (See Help --help)\n\n");
        } else {
            long start = System.currentTimeMillis();
            out("\n... EPA SWMM " + vMajor + "." + vMinor + " (Build " + vMajor + "." + vMinor + "." + vRelease + ") [Java port]\n");
            F_swmm5.swmm_run(R.lit(argv[0]), R.lit(argv[1]), R.lit(argv.length > 2 ? argv[2] : ""));
            String msg = String.format("\n\n... EPA SWMM completed in %.2f seconds.", (System.currentTimeMillis() - start) / 1000.0);
            CPtr err = R.buf(128);
            if (F_swmm5.swmm_getError(err, 127) > 0) msg += " There are errors.\n";
            else if (F_swmm5.swmm_getWarnings() > 0) msg += " There are warnings.\n";
            else msg += "\n";
            out(msg);
        }
        R.stdout.flush();
    }
}
