package com.edhn.commons.text;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * SimStrUtil
 * @author fengyq
 * @version 1.0
 *
 */
public class SimStrUtil {
    
    private static SimStrUtil instance = new SimStrUtil();
    
    /**
     * 计算字符串海明距离
     * @param s1
     * @param s2
     * @return
     */
    public static int computeDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        
        int[][] diff = new int[len1 + 1][len2 + 1];
        // step1
        if (len1 == 0) {
            return len2;
        } else if (len2 == 0) {
            return len1;
        }
        
        // step2 init first row and col
        for (int i = 0; i <= len1; i++) {
            diff[i][0] = i;
        }
        for (int i = 0; i <= len2; i++) {
            diff[0][i] = i;
        }
        
        // step3 distance 
        int cost;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s1.substring(i-1, i).equals(s2.substring(j-1, j))) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                diff[i][j] = Math.min(
                    Math.min(diff[i - 1][j] + 1, diff[i][j - 1] + 1),
                    diff[i - 1][j - 1] + cost);
//                diff[i][j] = cdMin(diff[i-1][j]+1, diff[i][j-1]+1, diff[i-1][j-1]+cost);
            }
        }
        
        return diff[len1][len2];
    }
    
    /**
     * 从mainStr中分析和str相似的子串
     * @param mainStr mainStr
     * @param str str 用于比较
     * @param result 结果子串
     * @return simstr和text2的距离
     */
    public static int getSubSimStr(String mainStr, String str, StringBuffer result) {
        int len = str.length();
        String simstr = mainStr.substring(0, Math.min(len, mainStr.length()));
        int distance = computeDistance(str, simstr);
        len ++;
        String stemp;
        while (len <= mainStr.length()) {
            stemp = mainStr.substring(0, len);
            int disNew = computeDistance(str, stemp);
            if (disNew < distance) {
                simstr = stemp;
                distance = disNew;
                len++;
            } else
                break;
        }
        
        len = str.length()-1;
        while (len >=0) {
            stemp = mainStr.substring(0, Math.min(len, mainStr.length()));
            int disNew = computeDistance(str, stemp);
            if (disNew < distance) {
                simstr = stemp;
                distance = disNew;
                len--;
            } else
                break;
        }
        result.append(simstr);
        return distance;
    }
    
    /**
     * 从列表中找到最相似的字符串
     * @param strs 待比对的全部字符串
     * @param findStr 要查找的串
     * @param minDistance 最小可能的距离，如果外部已经检查过无精确匹配，那么应传入1，优化性能
     * @return SimStr
     */
    public static SimStr findSimStr(List<String> strs, String findStr, 
            byte minDistance) {
        SimStr ss = instance.new SimStr();
        ss.findStr = findStr;
        ss.distance = Integer.MAX_VALUE;
        for (int i = 0; i < strs.size(); i ++) {
            String s = strs.get(i);
            int dis = computeDistance(s, findStr);
            if (dis < ss.distance) {
                ss.distance = dis;
                ss.simStr = s;
                ss.index = i;
            }
            if (dis == minDistance)
                break;
        }
        return ss;
    }
    
    /**
     * 从Map中找到最相似的字符串 
     * @param strs  待比对的全部字符串，key和value都会比对
     * @param findStr 要查找的串
     * @param minDistance 最小可能的距离，如果外部已经检查过无精确匹配，那么应传入1，优化性能
     * @return SimStr
     */
    public static SimStr findSimStr(Map<String, String> strs, String findStr, 
            int minDistance) {
        Iterator<Entry<String, String>> itE = strs.entrySet().iterator();
        SimStr ss = instance.new SimStr();
        ss.findStr = findStr;
        ss.distance = Integer.MAX_VALUE;
        while (itE.hasNext()) {
            Entry<String, String> en = itE.next();
            String s = en.getKey();
            int dis = computeDistance(s, findStr);
            if (dis < ss.distance) {
                ss.distance = dis;
                ss.simStr = s;
                ss.index = s;
            }
            
            s = en.getValue();
            if (s != null && !"".equals(s)) {
                dis = computeDistance(s, findStr);
                if (dis < ss.distance) {
                    ss.distance = dis;
                    ss.simStr = s;
                }
            }
            if (dis == minDistance)
                break;
        }
        return ss;
    }

    
    
    /**
     * SimStr
     * @author fengyq
     * @version 1.0
     *
     */
    public class SimStr {
        public String findStr;  // 查找的串
        public String simStr;   // 相似的串
        public int distance;    // 距离
        public Object index;    // 如在列表查找则为元素索引，如在map查找则为key
        
    }

    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        String s1 = "中华人民共和国民事诉讼法";
        String s2 = "中华人民共和国民事";
        System.out.println("Distance=" + computeDistance(s1, s2));
    }

}
