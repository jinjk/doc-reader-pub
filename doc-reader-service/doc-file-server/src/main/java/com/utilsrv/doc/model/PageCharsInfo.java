package com.utilsrv.doc.model;

import com.google.common.base.Ascii;

import java.util.ArrayList;
import java.util.List;

public class PageCharsInfo {
    private StringBuilder innerStrBuilder = new StringBuilder();
    // separated by ',', '.', or other separators
    private List<String> hanStrings = null;
    private List<Integer> hanCharsInx = new ArrayList<>();
    private List<int[]> wordSegs;
    private List<Group> groups = new ArrayList<>();
    boolean extracted = false;

    public List<String> hanStrings() {
        if (hanStrings == null || hanStrings.size() == 0) {
            extractHanChars();
        }
        return hanStrings;
    }

    public void setWordSegsAndBuildGroups(List<int[]> wordSegs) {
        this.wordSegs = wordSegs;
        this.buildGroups();
    }

    /**
     * 1 5 9 12 24, [2, 3] -> [1, 5], [9, 12, 24]
     */
    private void buildGroups() {
        if (!extracted) {
            throw new RuntimeException("Not extracted han str yet, please run extractHanChars first");
        }
        int pos = 0;
        if (wordSegs == null) {
            return;
        }
        for (int[] segs : wordSegs) {
            for (int groupLen : segs) {
                int[] idx = new int[groupLen];
                for (int i = 0; i < groupLen; i++) {
                    idx[i] = hanCharsInx.get(i + pos);
                }
                groups.add(new Group(groupLen, idx));
                pos += groupLen;
            }
        }
    }

    public String toFormatted(String gPrefix) {
        StringBuilder builder = new StringBuilder();
        int pos = 0;
        int gid = 0;
        for (Group group : groups) {
            for (int i : group.index) {
                if (pos < i) {
                    builder.append(innerStrBuilder, pos, i);
                }
                String sGid = gPrefix + "_" + gid;
                if (group.hanLength > 1) {
                    String style = "c2";
                    if (i == group.index[0]) style = "c1";
                    else if (i == group.index[group.index.length-1]) style = "c3";
                    builder.append(String.format("<w grp='%s' class='%s'>%s</w>", sGid, style, innerStrBuilder.charAt(i)));
                }
                else {
                    builder.append(String.format("<w grp='%s'>%s</w>", sGid, innerStrBuilder.charAt(i)));
                }
                pos = i + 1;
            }
            gid++;
        }
        builder.append(innerStrBuilder.substring(pos));
        return builder.toString();
    }

    private void extractHanChars() {
        int p = 0;
        this.hanStrings = new ArrayList<>();
        StringBuilder hanStrBuilder = new StringBuilder();
        int gtPos = 0, ltPos = 0;
        for (int i = 0; i < innerStrBuilder.length(); ) {
            char ch = innerStrBuilder.charAt(p);
            if (ch == '>') {
                gtPos = p;
            }
            else if (ch == '<') {
                ltPos = p;
            }

            int codepoint = innerStrBuilder.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                hanCharsInx.add(p);
                hanStrBuilder.append((char) codepoint);
            }
            else if (gtPos > ltPos && (ch != '>' && ch != ' ' && ch != '\r' && ch != '\n')) {
                if (hanStrBuilder.length() > 0) {
                    this.hanStrings.add(hanStrBuilder.toString());
                    hanStrBuilder = new StringBuilder();
                }
            }

            p++;
        }
        if (hanStrBuilder.length() > 0) {
            this.hanStrings.add(hanStrBuilder.toString());
        }
        extracted = true;
    }

    public static void main(String[] args) {
        PageCharsInfo hanInfo = new PageCharsInfo();
        hanInfo.addLine("<div>你好，这个是测试用的一个例</div>");
        hanInfo.addLine("<div>子，还要手动输入group信息</div>");
        List<String> hanStrList = hanInfo.hanStrings();
        // 2, 2, 1, 2, 1, 1, 2, 2
        // 2, 2, 2, 2
        for (String str : hanStrList) {
            System.out.println(str);
        }
        List<int[]> wordSegs = new ArrayList<>();
        wordSegs.add(new int[]{2, 2, 1, 2, 1, 1, 2, 2});
        wordSegs.add(new int[]{2, 2, 2, 2});
        hanInfo.setWordSegsAndBuildGroups(wordSegs);
        System.out.println(hanInfo.toFormatted("grp_01"));
    }

    public void addLine(String line) {
        innerStrBuilder.append(line).append("\n");
    }

    class Group {
        int hanLength = 0;
        int[] index;

        Group(int hanLength, int[] index) {
            this.hanLength = hanLength;
            this.index = index;
        }
    }
}
