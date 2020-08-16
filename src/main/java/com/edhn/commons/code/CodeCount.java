package com.edhn.commons.code;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计同一个目录下指定扩展名文件中的代码，包括空行数，注解数量，有效代码数
 * 根据网上流传的修改，加入了多种扩展名文件的统计
 * CodeCount
 * @author fengyq
 * @version 1.0
 *
 */
public class CodeCount {


    static CodeCounter[] counters;

    /**
     * @param ext
     * @param counters
     * @return
     */
    private static CodeCounter getCounterByExt(String ext,
            CodeCounter[] counters) {
        for (int i = 0; i < counters.length; i++) {
            if (ext.equals(counters[i].ext))
                return counters[i];
        }
        return null;
    }
    
    /**
     * @param sFileName
     * @return
     */
    private static String getFileExt(String sFileName){
        int n = sFileName.indexOf('.');
        if (n != -1)
            return sFileName.substring(n, sFileName.length());
        else
            return "";
    }
	
	/**
	 * @param dir
	 * @param counters
	 */
	private static void countCodeInDir(String dir, CodeCounter[] counters){
		File fMain = new File(dir);
		if (!fMain.exists()){
			System.out.println("dir not exist.");
		}
		File[] codeFile = fMain.listFiles();
		int i=0;
		while (i<codeFile.length){
			File fChild = codeFile[i];
			if (fChild.isDirectory())
				countCodeInDir(fChild.getAbsolutePath(), counters);
			else {
	            CodeCounter c = getCounterByExt(getFileExt(fChild.getName()), counters);
//			else if (fChild.getName().endsWith(ext)){
//			else if (fChild.getName().matches(".*\\.java$")){
	            if (c!=null)
	                parse(fChild, c);
			}
			i++;
		}
	}
	
	/**
	 * @param hintMsg
	 * @return
	 */
	private static String readOneInput(String hintMsg) {
        String input = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        System.out.println(hintMsg);
        try {
            input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    /**
	     * 公司代码权重
            100:*.java;*.pas;*.js;summer-url.properties;summer-query.properties;*.txt;*.as;
            60:*.jsp;*.php;*.html;*.htm;*.mxml;
            20:*.sql;*.css;
            6:*.xml;
	     * */
		String dir = "";
		List<String> excludeKeys = new ArrayList<String>();
		
		String excludeFileName = String.valueOf(System.getProperty("CodeCount.Exclude"));
		File f = new File(excludeFileName);
		if (f.exists()) {
		    System.out.println("using config=" + excludeFileName);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(f)));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("#")) {
                        continue;
                    }
                    excludeKeys.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
		
        counters = new CodeCounter[]{
                new CodeCounter(".java", "//", "/*", "*/", 1),
                new CodeCounter(".pas", "//", "{", "}", 1),
                new CodeCounter(".js", "//", "/*", "*/", 1),
                new CodeCounter(".jsp", null, "<!--", "-->", 0.6),
                new CodeCounter(".htm", null, "{", "}", 0.6),
                new CodeCounter(".xml", null, "{", "}", 0.06),
                new CodeCounter(".sql", "--", "/*", "*/", 0.2),
                new CodeCounter(".properties", "#", null, null, 0.2)
                };

      StringBuilder supportExts = new StringBuilder();
      for (int i = 0; i < counters.length; i++){
          counters[i].setExcludesPathKey(excludeKeys);
          // exclude key
//          counters[i].AddExcludePathKey("artery"); 
//          counters[i].AddExcludePathKey("summer");
          supportExts.append(counters[i].getExt());
          if (i < counters.length - 1) {
              supportExts.append(", ");
          }
          
      }
		if (args.length != 0){
			dir = args[0];
		}else{
	        System.out.println("this tool can count code of source files in a dir ");
	        System.out.println("supported ext including " + supportExts.toString());
		    dir = readOneInput("please input a source dir:");
		}
		System.out.println("counting code in dir:" + dir);
		
		countCodeInDir(dir, counters);

		// show unfound exts
        for (int i = 0; i < counters.length; i++) {
            if (counters[i].getFileCount() == 0) {
                System.out.println("未找到扩展名为" + counters[i].ext + "的文件。");
            }
        }
        // show found exts
        for (int i = 0; i < counters.length; i++) {
            if (counters[i].getFileCount() > 0) {
                System.out.println("扩展名为" + counters[i].ext + "的文件中代码统计结果如下：");
                System.out.println("代码行:= " + counters[i].getNormalLines() + "（系数折算行：" + counters[i].getFacorNormalLines() + "）");
                System.out.println("注释行:= " + counters[i].commenLines);
                System.out.println("空白行:= " + counters[i].whiteLines);
                System.out.println("总计:= " + counters[i].getTotalLines());
                System.out.println("统计文件个数:= " + counters[i].getFileCount());
            }
        }

	}

	/**
	 * 统计代码文件中的注释行、空行、有效行。
	 * 注释行、空行有规律可循，有效行即统计非前两种的情况
	 * @param f 文件操作对象
	 * @param counter 一个计数对象，参照类定义
	 */
	private static void parse(File f, CodeCounter counter) {
		BufferedReader br = null;
		boolean comment = false;
		if (counter.isExcludePath(f.getAbsolutePath()))
		    return;
		try {
			br = new BufferedReader(new FileReader(f));
			String line = "";
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.matches("^[\\s&&[^\\n]]*$")) {
				    counter.whiteLines++;
				} else if (counter.hasMultiLineComment() 
				        && line.startsWith(counter.multiLineCommentBegin) 
				        && !line.endsWith(counter.multiLineCommentEnd)) {
				    counter.commenLines++;
					comment = true;
				}else if (counter.hasMultiLineComment() 
                        && line.startsWith(counter.multiLineCommentBegin) 
				        && line.endsWith(counter.multiLineCommentEnd)) {
				    counter.commenLines++;
				}
				else if (true == comment) {
				    counter.commenLines++;
					if (counter.hasMultiLineComment() 
					  && line.endsWith(counter.multiLineCommentEnd)) {
						comment = false;
					}
				} else if (counter.hasSingleComment() 
				         && line.startsWith(counter.singleLineComment)){
				    counter.commenLines++;
				}
				else {
				    counter.normalLines++;
				}
			}
			counter.setFileCount(counter.getFileCount() + 1);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

    public static class CodeCounter {
        // 扩展名
        private String ext;
        /** 必须为每种Counter指定注释关键字 */
        public String singleLineComment;
        public String multiLineCommentBegin;
        public String multiLineCommentEnd;
        public List<String> excludesPathKey;
        
        /** 与标准代码相比的代码计算系数，比如jsp使用0.6 */
        private double factor = 1;
        
        /** 每种Counter有自己的计数器，即下面的3个属性*/
        public long normalLines = 0;
        public long commenLines = 0;
        public long whiteLines = 0;

        private int fileCount;

        public CodeCounter(String ext, String singleLineComment,
                String multiLineCommentBegin, String multiLineCommentEnd,
                double factor) {
            this.ext = ext;
            this.singleLineComment = singleLineComment;
            this.multiLineCommentBegin = multiLineCommentBegin;
            this.multiLineCommentEnd = multiLineCommentEnd;
            this.factor = factor;
            excludesPathKey = new ArrayList<String>();
        }
        
        public double getTotalLines() {
            return normalLines + commenLines + whiteLines;
        }

        public double getNormalLines() {
            return normalLines;
        }

        public double getFacorNormalLines() {
            return normalLines * factor;
        }

        public boolean hasMultiLineComment() {
            return (multiLineCommentBegin != null)
                    && (multiLineCommentEnd != null);
        }

        public boolean hasSingleComment() {
            return (singleLineComment != null);
        }

        public boolean isExcludePath(String sPath) {
            for (int i = 0; i < excludesPathKey.size(); i++) {
                String key = excludesPathKey.get(i);
                // 路径包含key表示被排除
                if (sPath.indexOf(key) != -1)
                    return true;
            }
            return false;
        }

        /**
         * @return the fileCount
         */
        public int getFileCount() {
            return fileCount;
        }

        /**
         * @param fileCount the fileCount to set
         */
        public void setFileCount(int fileCount) {
            this.fileCount = fileCount;
        }

        /**
         * @return the ext
         */
        public String getExt() {
            return ext;
        }

        /**
         * @param ext the ext to set
         */
        public void setExt(String ext) {
            this.ext = ext;
        }

        /**
         * @return the commenLines
         */
        public long getCommenLines() {
            return commenLines;
        }

        /**
         * @param commenLines the commenLines to set
         */
        public void setCommenLines(long commenLines) {
            this.commenLines = commenLines;
        }

        /**
         * @return the excludesPathKey
         */
        public List<String> getExcludesPathKey() {
            return excludesPathKey;
        }

        /**
         * @param excludesPathKey the excludesPathKey to set
         */
        public void setExcludesPathKey(List<String> excludesPathKey) {
            this.excludesPathKey = excludesPathKey;
        }
    }

}
