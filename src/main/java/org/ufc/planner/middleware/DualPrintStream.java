package org.ufc.planner.middleware;

import java.io.OutputStream;
import java.io.PrintStream;

public class DualPrintStream extends PrintStream {
    private final PrintStream second;

    public DualPrintStream(OutputStream main, PrintStream second) {
        super(main, true); // autoFlush
        this.second = second;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        second.write(buf, off, len);
    }

    @Override
    public void flush() {
        super.flush();
        second.flush();
    }
}