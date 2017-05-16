package com.pharbers.aqll.common.alFileHandler.alCsvOpt.java;

import java.io.Closeable;
import java.io.IOException;

public interface LineReader extends Closeable {

    String readLineWithTerminator() throws IOException;
}

