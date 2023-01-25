package com.utilsrv.doc.util;

import com.utilsrv.doc.model.HanStringInfo;

import java.util.ArrayList;
import java.util.List;

public class DocUtil {
    public static HanStringInfo extractHanChars(String str) {
        HanStringInfo hanStringInfo = new HanStringInfo();
        List<Integer> chars = new ArrayList<>();
        int p = 0;
        for (int i = 0; i < str.length(); ) {
            int codepoint = str.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                chars.add(p);
            }
            p++;
        }
        hanStringInfo.setStr(str);
        hanStringInfo.setHanCharsInx(chars.stream().mapToInt(Integer::intValue).toArray());
        return hanStringInfo;
    }
}
