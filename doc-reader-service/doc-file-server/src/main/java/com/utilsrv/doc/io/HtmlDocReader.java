package com.utilsrv.doc.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class HtmlDocReader extends BufferedReader {
    public HtmlDocReader(Reader in) {
        super(in);
    }
    private boolean processHead = true;

    @Override
    public String readLine() throws IOException {
        String line = super.readLine();
        boolean returnFromBuilder = false;
        if (line != null) {
            char[] chars = line.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char aChar : chars) {
                if (Character.UnicodeBlock.of(aChar) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                    builder.append("<i class=\"py\">").append(aChar).append("</i>");
                    returnFromBuilder = true;
                }
                else {
                    builder.append(aChar);
                }
            }
            if (processHead) {
                if (line.contains("<head>")) {
                    builder.append("<link rel=\"stylesheet\" href=\"../css/jquery-ui.min.css\">");
                    builder.append("<link rel=\"stylesheet\" href=\"../css/book.css\">");
                    builder.append("<script defer src=\"../js/jquery-3.6.0.min.js\"></script>");
                    builder.append("<script defer src=\"../js/jquery-ui.min.js\"></script>");
                    builder.append("<script defer src=\"../js/book.js\"></script>");
                    returnFromBuilder = true;
                }
                if (line.contains("<title>")) {
                    returnFromBuilder = false;
                }
                if (line.contains("</head>")) {
                    processHead = false;
                }
            }
            if (returnFromBuilder) {
                return builder.toString();
            }
            else {
                return line;
            }
        }
        return null;
    }

}