package com.edhn.commons.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**Ø
 * TimeCounter
 * 执行时间统计工具类
 * @author fengyq
 * @version 1.0
 *
 */
public class TimeCounter {

    private static final Logger logger = LoggerFactory.getLogger(TimeCounter.class);

    private static long warn_threshold = 3000;

    private static long info_threshold = 1000;

    private static long debug_threshold = 500;

    private static boolean printStack = true;

    private long startTime = 0;
    private long startCounterTime = 0;

    /**
     * 构造并自动执行start
     */
    public TimeCounter() {
        this.start();
        this.startCounterTime = this.startTime;
    }

    /**
     * 开始计时
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }

    /**
     * 检查并输出超过阈值的log
     * @param elapse 耗时时长
     * @param logstr logstr
     * @param args   logstr中的{}参数
     */
    private void logSlow(long elapse, StringBuffer logstr, Object... args) {
        if (elapse > debug_threshold || elapse > info_threshold || elapse > warn_threshold) {
            List<Object> fullArgs = new ArrayList<Object>();
            fullArgs.add(elapse);
            if (elapse > warn_threshold) {
                fullArgs.add(warn_threshold);
            } else if (elapse > info_threshold) {
                fullArgs.add(info_threshold);
            } else if (elapse > debug_threshold) {
                fullArgs.add(debug_threshold);
            }
            fullArgs.addAll(Arrays.asList(args));
            if (printStack) {
                logstr.append(", stack={}");
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace.length > 5) {
                	StackTraceElement[] traces = new StackTraceElement[5];
                	System.arraycopy(Thread.currentThread().getStackTrace(), 0, traces, 0, 5);
                	stackTrace = traces;
                }
                fullArgs.add(stackTrace);
            }
            if (elapse > warn_threshold) {
                logger.warn(logstr.toString(), fullArgs.toArray());
            } else if (elapse > info_threshold) {
                logger.info(logstr.toString(), fullArgs.toArray());
            } else if (elapse > debug_threshold) {
                logger.debug(logstr.toString(), fullArgs.toArray());
            }
        }
    }

    /**
     * 记录距离上次startTime的时间，超过阈值时会产生日志，阈值分debug、info、warn共3中级别
     * 每次记录后开始新的startTime
     * @param msg 操作描述，记录到日志
     * @param args，msg中的{}参数
     */
    public void logSlow(String msg, Object... args) {
        long elapse = System.currentTimeMillis() - startTime;

        StringBuffer logstr = new StringBuffer("操作耗时 {}ms 超过阈值{}ms。").append(msg);
        logSlow(elapse, logstr, args);
        start();
    }

    /**
     * 记录距离上次startTime的时间，不检查阈值直接记录
     * 每次记录后开始新的startTime
     * @param msg 操作描述，记录到日志
     * @param args，操作相关参数
     */
    public void logElapse(String msg, Object... args) {
        long elapse = System.currentTimeMillis() - startTime;
        List<Object> fullArgs = new ArrayList<Object>();
        fullArgs.add(elapse);
        fullArgs.addAll(Arrays.asList(args));
        logger.info("操作耗时 {}ms " + msg, fullArgs.toArray());
        start();
    }

    /**
     * 记录距离startCounterTime（即创建TimeCounter）的时间，用于记录总时长
     * @param msg 操作描述，记录到日志
     * @param args，操作相关参数
     */
    public void logTotalSlow(String msg, Object... args) {
        long totalElapse = System.currentTimeMillis() - startCounterTime;

        StringBuffer logstr = new StringBuffer("总耗时 {}ms 超过阈值{}ms。").append(msg);
        logSlow(totalElapse, logstr, args);
    }

    /**
     * @return the warn_threshold
     */
    public static long getWarn_threshold() {
        return warn_threshold;
    }

    /**
     * @param warn_threshold the warn_threshold to set
     */
    public static void setWarn_threshold(long warn_threshold) {
        TimeCounter.warn_threshold = warn_threshold;
    }

    /**
     * @return the info_threshold
     */
    public static long getInfo_threshold() {
        return info_threshold;
    }

    /**
     * @param info_threshold the info_threshold to set
     */
    public static void setInfo_threshold(long info_threshold) {
        TimeCounter.info_threshold = info_threshold;
    }

    /**
     * @return the debug_threshold
     */
    public static long getDebug_threshold() {
        return debug_threshold;
    }

    /**
     * @param debug_threshold the debug_threshold to set
     */
    public static void setDebug_threshold(long debug_threshold) {
        TimeCounter.debug_threshold = debug_threshold;
    }

    /**
     * @param thresArray, 顺序：debug_threshold,info_threshold,warn_threshold
     */
    public static void setThreshold(long...thresArray) {
        if (thresArray.length > 0) {
            debug_threshold = thresArray[0];
        }
        if (thresArray.length > 1) {
            info_threshold = thresArray[1];
        }
        if (thresArray.length > 2) {
            warn_threshold = thresArray[2];
        }
    }

    /**
     * @return the printStack
     */
    public static boolean isPrintStack() {
        return printStack;
    }

    /**
     * @param printStack the printStack to set
     */
    public static void setPrintStack(boolean printStack) {
        TimeCounter.printStack = printStack;
    }

}
