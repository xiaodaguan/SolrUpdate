package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * �ַ���������
 * @author grs
 * @since 2012.5
 */
public class StringUtil {

	/**
	 * ��ȡƥ������
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static String extrator(String str, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			sb.append(m.group().trim());
		}
		return sb.toString();
	}
	/**
	 * ƥ����ʼ�ͽ���λ��֮�������
	 * @param content
	 * @param start
	 * @param end
	 * @return
	 */
	public static String regMatcher(String content, String start, String end) {
		return regMatcher(content, start, end, true);
	}
	/**
	 *  ƥ����ʼ�ͽ���λ��֮������ݣ��Ƿ�̰��ƥ�䣬Ĭ��Ϊ��
	 * @param content
	 * @param start
	 * @param end
	 * @param is
	 * @return
	 */
	public static String regMatcher(String content, String start, String end, boolean is) {
		String mat = null;
		if(is) {
			mat = start + "([\\s\\S]+?)(\\s)?" + end;
		} else {
			mat = start + "([\\s\\S]+)(\\s)?" + end;
		}
		Pattern p = Pattern.compile(mat);
		Matcher m = p.matcher(content);
		if(m.find()) {
			return m.group(1);
		}
		return null;
	}

	/**
	 * ��ʽ���ַ���
	 * @param s
	 * @return
	 */
	public static String format(String s) {
		String result = s;
		if (s != null && s.trim().length()>0){
			while (result.indexOf("\r")!=-1)
				result = result.replaceAll("\r", "");
			while (result.indexOf("\n")!=-1)
				result = result.replaceAll("\n", "");
			while (result.indexOf("\t")!=-1)
				result = result.replaceAll("\t", "");
			while (result.indexOf("  ")!=-1)
				result = result.replaceAll("  ", " ");
			while (result.indexOf("��")!=-1)
				result = result.replaceAll("��", "");
			return result.trim();
		}
		return "";
	}
	
	/**
	 * �ж��Ƿ��������ַ�
	 * @param str
	 * @return
	 */
	public static boolean haveChinese(String str) {
		boolean judge = false;
		if(str==null || str.equals("")) 
			return judge;
		char[] ch = str.toCharArray();
		for(int i = 0; i < ch.length;i++) {
			if(isChinese(ch[i])) {
				judge = true;
				break;
			}
		}
		ch = null;
		return judge;
    }
	private static boolean isChinese(char c) {  
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
            return true;  
        }  
        return false;  
    }
	
	public static String getContent(String filename, String encode) {
		String s = "";
		StringBuffer sb = new StringBuffer();
		java.io.BufferedReader br = null;
		java.io.FileInputStream fis = null;
		java.io.InputStreamReader in = null;
		try {
			fis = new java.io.FileInputStream(filename);
			in = new java.io.InputStreamReader(fis, encode);
			br = new java.io.BufferedReader(in);
			while((s=br.readLine())!=null) 
				sb.append(s).append("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					in = null;
				}
			}
			if(fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					fis = null;
				}
			}
		}
		return sb.toString();
	}
	
	public static String getContent(String filename) {
		String s = "";
		StringBuffer sb = new StringBuffer();
		java.io.BufferedReader br = null;
		try {
			br = new java.io.BufferedReader(new java.io.FileReader(new java.io.File(filename)));
			while((s=br.readLine())!=null) 
				sb.append(s).append("\r\n");
		} catch (Exception e) {
			return null;
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}
		return sb.toString();
	}
	
	public static List<String> contentList(String filename) {
		String s = "";
		List<String> sb = new ArrayList<String>();
		java.io.BufferedReader br = null;
		try {
			br = new java.io.BufferedReader(new java.io.FileReader(new java.io.File(filename)));
			while((s=br.readLine())!=null) 
				sb.add(s);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}
		return sb;
	}
	
	public static List<String> contentList(String filename, String encode) {
		String s = "";
		List<String> sb = new ArrayList<String>();
		java.io.BufferedReader br = null;
		try {
			InputStream in = new FileInputStream(filename);
			br = new java.io.BufferedReader(new java.io.InputStreamReader(in , encode));
			while((s=br.readLine())!=null) 
				sb.add(s);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}
		return sb;
	}

	
	public static void writeFile(String filename, String content) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			fos.write(content.getBytes("utf-8"));
		} catch (FileNotFoundException e) {
			if(filename.indexOf(File.separator) > -1) {
				File f = new File(filename.substring(0, filename.lastIndexOf(File.separator)));
				if(!f.exists()) {
					f.mkdirs();
				}
				writeFile(filename, content, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void writeFile(String filename, String content, String encode) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			fos.write(content.getBytes(encode));
		} catch (FileNotFoundException e) {
			if(filename.indexOf(File.separator) > -1) {
				File f = new File(filename.substring(0, filename.lastIndexOf(File.separator)));
				if(!f.exists()) {
					f.mkdirs();
				}
				writeFile(filename, content, encode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static String getHost(String url) {
		if (url == null) {
			return null;
		}
		url = url.replaceAll("^https?://", "").replaceAll("([^/]+).*", "$1");
		return url;
	}
	public static String getContent(File f) {
		String s = "";
		StringBuffer sb = new StringBuffer();
		java.io.BufferedReader br = null;
		try {
			br = new java.io.BufferedReader(new java.io.FileReader(f));
			while((s=br.readLine())!=null) 
				sb.append(s).append("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}
		return sb.toString();
	}

	public static void writeFile(String filename, byte[] bytes) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			fos.write(bytes);
		} catch (FileNotFoundException e) {
			if(filename.indexOf(File.separator) > -1) {
				File f = new File(filename.substring(0, filename.lastIndexOf(File.separator)));
				if(!f.exists()) {
					f.mkdirs();
				}
				writeFile(filename, bytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
