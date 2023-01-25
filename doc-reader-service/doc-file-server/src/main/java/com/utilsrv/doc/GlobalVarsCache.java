package com.utilsrv.doc;


import com.utilsrv.doc.batch.convert.PDF2HtmlTaskDef;
import org.apache.commons.collections4.map.LRUMap;

public class GlobalVarsCache {
    private static LRUMap<String, Object> lruMap = new LRUMap<>(1000);

    public static void add(String key, Object value) {
        lruMap.put(key, value);
    }

    public static PDF2HtmlTaskDef getTaskDef(String key) {
        PDF2HtmlTaskDef taskDef = (PDF2HtmlTaskDef) lruMap.get(key);
        if (taskDef == null) {
            taskDef = new PDF2HtmlTaskDef(DocManager.docFileInfo(key));
        }
        return taskDef;
    }
}
