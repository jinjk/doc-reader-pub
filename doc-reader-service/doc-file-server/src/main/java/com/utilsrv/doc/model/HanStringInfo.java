package com.utilsrv.doc.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class HanStringInfo {
    private String str;
    private int[] hanCharsInx;
    private int[] groupLengths;
    @Setter(AccessLevel.NONE)
    private List<int[]> groups = new ArrayList<>();

    public boolean hasHanChars() {
        return hanCharsInx != null && hanCharsInx.length > 0;
    }

    public String hanString() {
        char[] chars = new char[hanCharsInx.length];
        for (int i = 0; i < hanCharsInx.length; i++) {
            chars[i] = str.charAt(hanCharsInx[i]);
        }
        String res = new String(chars);
        return res;
    }

    /**
     * 1 5 9 12 24, [2, 3] -> [1, 5], [9, 12, 24]
     */
    public void buildGroups() {
        int pos = 0;
        for (int len : groupLengths) {
            int[] group = new int[len];
            for (int i = 0; i < len; i++) {
                group[i] = hanCharsInx[i + pos];
            }
            groups.add(group);
            pos += len;
        }
    }

    public String toFormatted(String gPrefix) {
        StringBuilder builder = new StringBuilder();
        int pos = 0;
        int gid = 0;
        for (int[] group : groups) {
            for (int i : group) {
                if (pos < i) {
                    builder.append(str, pos, i);
                }
                String sGid = gPrefix + "_" + gid;
                builder.append(String.format("<w grp='%s'>%s</w>", sGid, str.charAt(i)));
                pos = i+1;
            }
            gid++;
        }
        builder.append(str.substring(pos));
        return builder.toString();
    }

    public static void main(String[] args) {
        HanStringInfo hanInfo = new HanStringInfo();
        hanInfo.setStr("<div>你好，这个是测试用的</div>");
        hanInfo.setHanCharsInx(new int[]{5, 6, 8, 9, 10, 11, 12, 13, 14});
        hanInfo.setGroupLengths(new int[]{2, 2, 1, 2, 2});
        hanInfo.buildGroups();
        System.out.println(hanInfo.toFormatted("grp_01"));
    }
}
