# swmm5-java

EPA SWMM 5.2.4 in Java, written by a translator that reads EPA's C, and judged against EPA itself:
the binary `.out` file byte for byte, and the `.rpt` report line for line except the three lines that
print the wall clock.

**Result: 480 of 480 models identical.** Median CPU time 9.18 times EPA's (4.28 to 93.97 over 12 models, 5 interleaved repetitions in a run of their own; EPA against itself spans 0.87 to 1.32 in that run).

It is not a new engine. Every number it writes is EPA 5.2.4's, including the results of EPA's own defects
wherever EPA's behaviour is defined (see `lessons/02-epa-defects-found.md`).

## The parity contract

- Reference: EPA SWMM 5.2.4 built from the [USEPA v5.2.4 tag](https://github.com/USEPA/Stormwater-Management-Model/tree/v5.2.4)
  with gcc 13.3 `-O2` and `_FORTIFY_SOURCE` off (`ref/Makefile`), on x86-64 Linux with glibc 2.39.
- Each model runs in its own folder, as EPA's command line runs it.
- Pass = `.out` byte-identical (when EPA writes one) and `.rpt` identical after removing the lines
  `Analysis begun on`, `Analysis ended on` and `Total elapsed time`. When an input error stops EPA before it
  writes results, the engine must not write a results file either. Nothing else is excused.
- Corpus: [SWMMBobSWMM6/1729-SWMM5-Models-2030](https://github.com/SWMMBobSWMM6/1729-SWMM5-Models-2030),
  folders EPA, Hydraulics, Hydrology, LID, OWA_EXTRAN and OWA_ROUTING. The gate set is 480 of the 518 distinct models (distinct by content) in six folders of the corpus, which holds 4,540 .inp files. The other 38 were left out by an early timing survey with a 12-second limit, whose records were not kept. The cut is rough. Re-timed for this essay on a busy machine, 13 of the 38 ran past a 30-second limit and the rest took 5.8 to 29.6 seconds, 4 of them within 12; and 3 gate models took longer than 12 seconds in the reference run (attenuation_KW_High_CE.inp, 13 s, SCANLON.inp, 35 s, 72_H&H_Elements(2).inp, 39 s).
- 49 of the 480 stop on an input error. For 39 of them EPA writes no
  results file and the report is the whole comparison; the other 10 write a short one, which is compared.

| Engine | Gate result on the same 480 models |
|---|---|
| C++ swmm5-cplus | 480 of 480 identical |
| Rust swmm5-rust | 480 of 480 identical |
| Fortran | 480 of 480 identical |
| C# | 480 of 480 identical |
| Python | 465 identical (451 fast models, then a 14-model sample of the 29 slow ones) |
| TypeScript | 480 of 480 identical |
| Java | 480 of 480 identical |

Controls: EPA run twice gives 40 of 40 identical files (null control). The Fortran
engine rebuilt with `-ffp-contract=fast -mfma` in place of `-ffp-contract=off` (`gate/build_fma_control.sh`)
differs from EPA on 87 of 480 models (positive control): the gate catches a
one-rounding-step change, and those 87 models are the ones that noticed it.

## Build and run

Requires a JDK, 17 or newer (tested with OpenJDK 21).

```sh
java/build.sh
java/runswmm model.inp model.rpt model.out
```

## How it was made

`tools/c2x/front.py` parses EPA's 54 C files with libclang, builds the program's tables and classifies all
2,083 pointer and array declarations by kind (array, single object, string, reference to a number, file,
`void*`, function). `tools/c2x/emit_java.py` writes the Java engine (44,855 lines, generated: `java/src/swmm5/F_*.java`); a
hand-written runtime (1,078 lines) supplies the C library pieces SWMM uses and hand ports of `odesolve.c`,
`mempool.c` and six helper functions. When the port disagreed with EPA, the fix went into the translator or the
runtime, never into the generated file.

- One class `F_<file>` per C file, records and function-pointer interfaces in `Types.java`, constants and string literals in `Consts.java`; `R.java`, `Refs.java` and `Overrides.java` are hand-written.
- Java has no pointers to numbers, so a C `double *` parameter becomes a `DRef` (get/set): a box for a local whose address is taken, an element reference for `&a[i]`, a field reference built from lambdas for `&s->x`.
- unsigned int is a `long` masked to 32 bits; every double-to-int conversion goes through `R.D2I`, because Java's cast saturates where x86 gives INT_MIN; printf rounds with BigDecimal and HALF_EVEN, which is glibc's rule.
- The math functions are not Java's: `Glibm.java` is glibc 2.39's FMA build of exp, log, log10, pow, sin, cos, tan, asin, acos and atan, translated from GCC's GIMPLE by `tools/libm/gim2x.py` (fused multiply-adds as `Math.fma`) and checked bit for bit against the system libm.

To regenerate: `python3 tools/c2x/emit_java.py java/src/swmm5` from the repository root (needs `pip install libclang==18.1.1`
and the clang 18 headers at `/usr/lib/llvm-18/lib/clang/18/include`).

## The math library

EPA calls glibc for `exp`, `log`, `log10`, `pow`, `sin`, `cos`, `tan`, `asin`, `acos` and `atan` (1,524,961,124 calls
over the gate models; `tools/libm/census.c`), and on x86-64 glibc picks, at start-up, versions compiled with fused
multiply-add when the processor has FMA and AVX2. Java's own math functions round differently, so this port carries a
translation of glibc 2.39's FMA routines: `tools/libm/build_glibm.sh` compiles glibc's sources with glibc's flags and
dumps GCC's optimized GIMPLE, `tools/libm/extract_tables.py` copies the constant tables out of the objects, and
`tools/libm/gim2x.py` translates the GIMPLE (`tools/libm/make_libm.sh` runs all three). Checked against the system
libm by `tools/libm/test/run_tests.sh`: 21,992,590 arguments (random, census samples and edge cases), 0 differences.

Positive control (`gate/run_java_native_control.sh`): the same engine with Java's own `Math` in place of the translations differs from EPA on 1 of the 40 control models and on 38 of all 480.

This makes the port match EPA run on a processor with FMA and AVX2. EPA itself, run on glibc's generic routines (as on
an older processor), changes 43 of 480 models (`gate/run_libm_generic_control.sh`); this port would not follow it there.

## Re-running the gate

```sh
make -C ref                                   # EPA 5.2.4 reference build
SWMM_CORPUS=/path/to/1729-SWMM5-Models-2030 gate/make_ref.sh       # reference outputs (about 1.2 GB)
SWMM_CORPUS=/path/to/1729-SWMM5-Models-2030 python3 gate/gate.py mine "$PWD/java/runswmm"
```

`gate/gate.py` prints a summary and writes `gate/results/mine.json`; the stored runs behind the numbers above
are in `results/`.

## The eight-engine app and the essay

`app/eight-engines.html` compares EPA 5.2.4, swmm5-cplus, swmm5-rust (safe Rust, built from its repository at de9fbf1 and gated here), and the Fortran, C#, Python,
TypeScript and Java ports on the stored corpus runs: gate results per model, report statistics, graphs with each
engine's difference from EPA, the benchmark, the controls, the math-library census and checks, and the defect
register. Its Doc tab is the essay *Eight Engines, One Answer*, also in
`docs/eight-engines.docx`. Rebuild both with
`python3 tools/app/build_aux_data.py && python3 tools/app/build_data.py && python3 tools/app/build_app.py` and `node tools/app/doc/build_docx.js docs/eight-engines.docx`.

## What this does not show

- That SWMM is right: identical to EPA means EPA's answers, good or bad. No observed data enter anywhere.
- Other platforms: the math library differs on Windows and macOS, and a port matches EPA built there only if its
  math library matches too. On x86-64 Linux, EPA itself changes 43 of 480 models when glibc picks its generic (non-FMA) math
  routines; the ports that call the system libm should follow EPA there (not tested), the TypeScript and Java ports would not.
- Paths no gate model exercises (counting only models that ran without an input error):
  Streets and inlets, Climate file, USE HOTSTART, USE INFLOWS (interface), USE RUNOFF. Street and inlet hydraulics appear in one deck, which stops on
  input errors; snowmelt runs in 4.
- Speed ratios come from 12 short models (EPA 0.01 to 0.72 s); long runs were not timed.
- The stepping API (`swmm_open`/`swmm_step`/...) is translated but only the command line was gated.
- No person has reviewed the generated code line by line; the evidence is the gate and the controls.

## Licence and credit

EPA SWMM is a work of the U.S. Environmental Protection Agency and is in the public domain; its engine is Lewis
Rossman's design, and this port keeps every algorithm as he wrote it. No licence has been chosen yet for the
translator and the runtime. The math routines `java/src/swmm5/Glibm.java` and `java/src/swmm5/GlibmRt.java` (and their sources in `tools/libm/`) are translations of glibc 2.39 code and remain under glibc's licence, the GNU Lesser General Public License 2.1 or later (`LICENSES/LGPL-2.1.txt`).

## AI use

See `AI-USE.md`.
