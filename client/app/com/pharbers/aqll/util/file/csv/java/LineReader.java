package com.pharbers.aqll.util.file.csv.java;

import java.io.Closeable;
import java.io.IOException;

public interface LineReader extends Closeable {

    String readLineWithTerminator() throws IOException;
}

