package com.edhn.commons.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RegexUtil
 * @author fengyq
 * @version 1.0
 *
 */
public class RegexUtil {
    
    private static Logger log = LoggerFactory.getLogger(RegexUtil.class);
    
    /**  是否使用缓存   *     */
    private static boolean useCache = true;
    
    /**  记录执行时间的日志的阈值   *     */
    private static int threshold = -1;
    
    /**  Pattern缓存   *     */
    private static Map<String, Pattern> patternCache = new ConcurrentHashMap<String, Pattern>(1<<8);
    
    /**
     * 先从缓存获取Pattern，没有则新创建
     * @param regex
     * @return
     */
    private static Pattern getPattern(String regex) {
        Pattern pat = null;
        if (useCache && regex.length() > 10) {
            pat = patternCache.get(regex);
        }
        if (pat == null) {
            try {
                pat = Pattern.compile(regex,Pattern.MULTILINE);
                if (useCache) {
                    patternCache.put(regex, pat);
                }
            } catch (PatternSyntaxException e) {
                log.error("pattern compile error! pattern={}", new Object[] {regex}, e);
            }
        }
        return pat;
    }
    
    protected static Matcher doMatch(Pattern p, String content) {
        long time = System.currentTimeMillis();
        Matcher m = p.matcher(content);
        time = System.currentTimeMillis() - time;
        if (threshold > 0 && time > threshold) {
            log.warn("pattern “{}” , elapse {}ms over threshold {}ms", new Object[]{p.pattern(), time, threshold});
        } else if (threshold == -1 && time > content.length()) {
            log.warn("pattern “{}” , elapse {}ms over dynamic threshold {}ms", new Object[]{p.pattern(), time, content.length()});
        }
        return m;
    }
    
    /**
     * 匹配第一个结果
     * @param regex
     * @param index
     * @param content
     * @return
     */
    public static String matchStr(String regex, int index, String content) {
        Pattern pat = getPattern(regex);
        return matchStr(pat, index, content);
    }
    
    /**
     * 匹配第一个结果
     * @param pat
     * @param index
     * @param content
     * @return
     */
    public static String matchStr(Pattern pat, int index, String content) {
        if (pat != null) {
            Matcher mat = doMatch(pat, content);
            if (mat.find()) {
                if (mat.group(index) != null)
                    return mat.group(index);
            }
        }
        return "";
    }
    
    /**
     * 仅检查是否能匹配到内容
     * @param regex
     * @param index
     * @param content
     * @return
     */
    public static boolean match(String regex, int index, String content) {
        return !"".equals(matchStr(regex, index, content));
    }
    
    /**
     * 仅检查是否能匹配到内容
     * @param pat
     * @param index
     * @param content
     * @return
     */
    public static boolean match(Pattern pat, int index, String content) {
        return !"".equals(matchStr(pat, index, content));
    }

    /**
     * 匹配全部结果
     * @param regex
     * @param index
     * @param content
     * @return
     */
    public static List<String> matchStrList(String regex, int index, String content) {
        Pattern pat = getPattern(regex);
        return matchStrList(pat, index, content);
    }
    
    /**
     * 匹配全部结果
     * @param regex
     * @param index
     * @param content
     * @return
     */
    public static List<String> matchStrList(Pattern pat, int index, String content) {
        List<String> list = new ArrayList<String>();
        if (pat != null) {
            Matcher mat = doMatch(pat, content);
            while (mat.find()) {
                if (mat.group(index) != null)
                    list.add(mat.group(index));
            }
        }
        return list;
    }
    
    /**
     * 匹配结果，返回MatchResult对象
     * @param regex
     * @param content
     * @return
     */
    public static MatchResult matchResult(String regex, String content) {
        Pattern pat = getPattern(regex);
        return matchResult(pat, content);
    }
    
    /**
     * 匹配结果，返回MatchResult对象
     * @param regex
     * @param content
     * @return
     */
    public static MatchResult matchResult(Pattern pat, String content) {
        Matcher mat = doMatch(pat, content);
        if (mat.find()) {
            return mat.toMatchResult();
        }
        return null;
    }

    /**
     * 匹配全部结果，返回MatchResult对象的列表
     * @param regex
     * @param content
     * @return
     */
    public static List<MatchResult> matchResultList(String regex, String content) {
        Pattern pat = getPattern(regex);
        return matchResultList(pat, content);
    }
    
    /**
     * 匹配全部结果，返回MatchResult对象的列表
     * @param regex
     * @param content
     * @return
     */
    public static List<MatchResult> matchResultList(Pattern pat, String content) {
        List<MatchResult> list = new ArrayList<MatchResult>();
        if (pat != null) {
            Matcher mat = doMatch(pat, content);
            while (mat.find()) {
                list.add(mat.toMatchResult());
            }
        }
        return list;
    }
    
    /**
     * 返回matcher
     * @param regex
     * @param content
     * @return
     */
    public static Matcher getMatcher(String regex, String content) {
        Pattern pat = getPattern(regex);
        if (pat == null)
            return null;
        Matcher mat = doMatch(pat, content);
        return mat;
    }
    
    /**
     * 返回matcher
     * @param p
     * @param content
     * @return
     */
    public static Matcher getMatcher(Pattern p, String content) {
        Matcher mat = doMatch(p, content);
        return mat;
    }
    
    /**
     * 替换内容
     * @param regex
     * @param content
     * @param replaceMethod
     * @return
     */
    public static String replace(String regex, String content, IRegexReplaceMethod replaceMethod) {
        Matcher m = getMatcher(regex, content);
        StringBuffer result = new StringBuffer();
        while (m.find()) {
        	if (replaceMethod == null) {
        		m.appendReplacement(result, "");
        	} else {
        		m.appendReplacement(result, replaceMethod.getReplacement(m));
        	}
        }
        m.appendTail(result);
        return result.toString();
    }
    
    /**
     * 替换内容时的回调方法
     * IRegexReplaceMethod
     * @author fengyq
     * @version 1.0
     *
     */
    public interface IRegexReplaceMethod {
        
        /**
         * 返回替换后的内容
         * @param m
         * @return
         */
        public String getReplacement(Matcher m);
    }
    
    /**
     * @param regex
     * @return
     */
    public static String escapeRegex(String regex, boolean matchBlankSim) {
        if (regex == null) {
            return "";
        }
        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            switch (c) {
                case '$':
                case '^':
                case '{':
                case '}':
                case '(':
                case ')':
                case '[':
                case ']':
                case '|':
                case '*':
                case '+':
                case '?':
                case '.':
                case '\\':
                    sbuilder.append('\\');
            }
            sbuilder.append(c);

            if (matchBlankSim && i != regex.length() - 1) {
                sbuilder.append("[\u00A0\u0020]*");
            }
        }
        return sbuilder.toString();
    }
    
    /**
     * pcre格式的正则转换为java形式
     * @param regex
     * @return
     */
    public static String pcre2javaRegex(String regex) {
        String r = "\\\\x[{](\\w{4})[}]";
        Pattern pat = Pattern.compile(r);
        Matcher mat = pat.matcher(regex);
        StringBuffer newRegex = new StringBuffer();
        while (mat.find()) {
            String replace = Matcher.quoteReplacement("\\u") + mat.group(1);
            mat.appendReplacement(newRegex, replace);
        }
        mat.appendTail(newRegex);
        return newRegex.toString();
    }

	public static boolean isUseCache() {
		return useCache;
	}

	public static void setUseCache(boolean useCache) {
		RegexUtil.useCache = useCache;
	}

	public static int getThreshold() {
		return threshold;
	}

	public static void setThreshold(int threshold) {
		RegexUtil.threshold = threshold;
	}

}
