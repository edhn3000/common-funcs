package com.edhn.commons.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


/**
 * FileBatchProcessor
 * 批量处理文件抽象类
 * @author fengyq
 * @version 1.0
 *
 */
public abstract class FileBatchProcessor {
    
    protected String inputRoot;
    protected String outputRoot;
    protected boolean update = false;
    private Charset encoding = Charset.defaultCharset();
    private ProcessStats stats;
    
    /**
     * 获得输出文件名
     * @param f
     * @param ext
     * @return
     * @throws IOException
     */
    protected File getOutputFile(File f, String ext) {
        String fileName;
        try {
            fileName = f.getCanonicalPath();
            String outPath = f.getParent() + File.separator;
            if (!StringUtils.isBlank(outputRoot)) {
                outPath = outPath.replace(inputRoot, outputRoot);
            }
            ext = StringUtils.isBlank(ext) ? ".txt" : ext;
            String outfileName = outPath + new File(fileName).getName().replaceAll("\\.[^.]+$", "") + ext;
            return new File(outfileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 处理文件
     * @param f
     * @param index
     */
    public abstract void processFile(File f, int index);
    
    /**
     * update模式下返回文件更是否需要更新
     * @param f
     * @param index
     * @return
     */
    protected boolean needUpdate(File f, int index) {
        return true;
    }
    
    static final Pattern FILE_EXT_PATTERN = Pattern.compile("\\.([^.]+)$");
    
    /**
     * @param path 文件夹路径
     * @param exts 扫描的文件扩展名，都用小写
     */
    protected void processDirInner(String path, final Set<String> exts) {
        File dir = new File(path);
        // create dest dirs
        if (!StringUtils.isBlank(outputRoot)) {
            String outPath = dir.getAbsolutePath().replace(inputRoot, outputRoot);
            File outDir = new File(outPath);
            if (!outDir.exists())
                outDir.mkdirs();
        }
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isFile()) {
                    String name = pathname.getName();
                    Matcher m = FILE_EXT_PATTERN.matcher(name);
                    String ext = m.find() ? m.group(1).toLowerCase() : "";
                    return exts.contains(ext);
                }
                return false;
            }
        });
        
        if (files.length > 0) {
            // process files
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isHidden())
                    continue;
                stats.total ++;
                stats.index ++;
                // update模式如果输出的txt文件时间比源文件晚，说明不用更新
                if (update) {
                    if (!needUpdate(f, stats.index)) {
                        continue;
                    }
                }
                processFile(f, stats.index);
                stats.processed ++;
            }
        }
        
        // 子目录
        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() && !pathname.isHidden();
            }
        });
        for (int i = 0; i < dirs.length; i++) {
            File f = dirs[i];
            try {
                processDirInner(f.getAbsolutePath(), exts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @param path 文件夹路径
     * @param exts 扫描的文件扩展名，都用小写
     */
    public void processDir(String path, Set<String> exts) {
        stats = new ProcessStats();
        this.processDirInner(path, exts);
        System.out.println(String.format("【统计】共检测到%s个文件，处理%s个，跳过%s个", stats.total, stats.processed, stats.total - stats.processed));
    }

    /**
     * @return the inputRoot
     */
    public String getInputRoot() {
        return inputRoot;
    }

    /**
     * @param inputRoot the inputRoot to set
     */
    public void setInputRoot(String inputRoot) {
        this.inputRoot = inputRoot;
    }

    /**
     * @return the outputRoot
     */
    public String getOutputRoot() {
        return outputRoot;
    }

    /**
     * @param outputRoot the outputRoot to set
     */
    public void setOutputRoot(String outputRoot) {
        this.outputRoot = outputRoot;
    }

    /**
     * @return the update
     */
    public boolean isUpdate() {
        return update;
    }

    /**
     * @param update the update to set
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }

    /**
     * @return the encoding
     */
    public Charset getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }


    /**
     * ProcessStats
     * 
     * @author fengyq
     * @version 1.0
     * @date 2019-12-04
     * 
     */
    public static class ProcessStats {
        int index = 0;
        int processed;
        int total;
    }

}
