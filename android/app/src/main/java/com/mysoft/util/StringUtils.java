
package com.mysoft.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    
    public static String BLANK_STRING = "";
    
    public static String BLANK_SPACE = " ";
    
    public static String ABSTRACT_SUFFIX = "…";
    
    public static String STRING_DIVIDER = ";";
    
    public static String DATE_DIVIDER = "-";
    
    public static boolean isNull(String str) {
        return (str == null || BLANK_STRING.equals(str));
    }
    
    // 比较两个字符串，按照字典排序，如果为空串，则认为在前；
    public static long Compare(String str1, String str2) {
        if (isNull(str1) && isNull(str2)) {
            return 0;
        } else if (isNull(str1) && !isNull(str2)) {
            return -1;
        } else if (!isNull(str1) && isNull(str2)) {
            return 1;
        } else {
            return str1.compareTo(str2);
        }
    }
    
    /**
     * 比较两个GUID字符串，按照字典排序，空串或者null最小 目前大小写敏感，最终以明源的文档为准备
     */
    public static long CompareGUID(String str1, String str2) {
        return CompareIgnoreCapitalCase(str1, str2);
    }
    
    // 比较两个字符串，按照字典排序，如果为空串，则认为在前；
    public static long CompareIgnoreCapitalCase(String str1, String str2) {
        if (isNull(str1)) {
            if (isNull(str2)) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (isNull(str2)) {
                return 1;
            }
        }
        return str1.toLowerCase(Locale.US).compareTo(str2.toLowerCase(Locale.US));
    }
    
    public static long compareIgnoreCapitalCase(String str1, String str2) {
        
        if (isNull(str1) && isNull(str2)) {
            return 0;
        }
        if (isNull(str1)) {
            return -1;
        }
        if (isNull(str2)) {
            return 1;
        }
        
        return str1.compareToIgnoreCase(str2);
        
    }
    
    /**
     * 判断指定的intent是否可以被第三方应用解析
     */
    static public List<Intent> canIntentResolved(Context context, Intent i) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        if (!list.isEmpty()) {
            for (ResolveInfo info : list) {
                Intent targeted = new Intent(i);
                ActivityInfo activityInfo = info.activityInfo;
                if (activityInfo.packageName.contains("com.tencent.mobileqq")) {
                    continue;
                }
                targeted.setPackage(activityInfo.packageName);
                targetedShareIntents.add(targeted);
            }
        }
        return targetedShareIntents;
    }
    
    /**
     * 将字符串数组拼接成一个字符串
     * 
     * @param strList 输入字符串数组
     * @param splitter 分隔符
     * @return 字符串，如果输入为空，返回空
     */
    public static String ArrayToString(String[] strList, String splitter) {
        // 检查数据合法性
        if (strList == null || strList.length == 0) {
            return BLANK_STRING;
        }
        
        // 拼接字符串数组和分隔符
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < strList.length; index++) {
            sb.append(strList[index]);
            // 最后一个字符串之后，不再添加分隔符号
            if (index < strList.length - 1) {
                sb.append(splitter);
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 将字符串数组拼接成一个字符串，以缺省分隔符
     * 
     * @param strList 输入字符串数组
     * @return 字符串，如果输入为空，返回空
     */
    public static String ArrayToString(String[] strList) {
        return ArrayToString(strList, STRING_DIVIDER);
    }
    
    /**
     * 将字符串分解成字符串数组，以splitter为分隔符
     * 
     * @param str 输入字符串
     * @param splitter 分隔符
     * @return 字符串数组，如果输入为空，返回空
     */
    public static String[] StringToArray(String str, String splitter) {
        // 检查合法性
        if (isNull(str)) {
            // 原始字符串为空，直接返回null
            return null;
        } else if (isNull(splitter)) {
            // 分解字符串为空，返回原字符串的数组
            return new String[] {str};
        } else {
            // 返回分解结果
            return str.split(splitter);
        }
    }
    
    /**
     * 将字符串分解成字符串数组，以缺省分隔符
     * 
     * @param str 输入字符串
     * @return 字符串数组，如果输入为空，返回空
     */
    public static String[] StringToArray(String str) {
        return StringToArray(str, STRING_DIVIDER);
    }
    
    /**
     * 提取string数组中对应位置的string
     * 
     * @param strarray 字符串数组
     * @param position 位置
     * @return 对应位置的字符串，如果找不到，则返回空串，注意是“”，不是null
     */
    public static String get(String[] strarray, int position) {
        if (strarray == null || strarray.length - 1 < position || position < 0) {
            return BLANK_STRING;
        } else {
            return strarray[position] == null ? BLANK_STRING : strarray[position];
        }
    }
    
    /**
     * 将string转换成非空字符串
     * 
     * @param str
     * @return 如果str为空，则返回空串，否则直接返回原串
     */
    public static String getNoneNullString(String str) {
        return str == null ? BLANK_STRING : str;
    }
    
    /**
     * 对输入字符串中的特殊字符进行规范化转移 "&" : "&amp;" "'" : "&apos;" "\"" : "&quot;" "<" : "&lt;" ">" : "&gt;" "[" : "[[]" "%" :
     * "[%]" "_" : "[_]"
     * 
     * @param str
     * @return 规范化之后的转义
     */
    public static String SPECIAL_CHARS_SOURCE = "&\"'<>";
    
    public static String[] SPECIAL_CHARS_TARGET = new String[] {"&amp;", "&quot;", "&apos;", "&lt;", "&gt;"};
    
    public static String validate(String str) {
        if (isNull(str)) {
            return BLANK_STRING;
        }
        
        StringBuilder result = new StringBuilder();
        
        int index;
        
        for (char tchar : str.toCharArray()) {
            index = SPECIAL_CHARS_SOURCE.indexOf(tchar);
            if (index != -1) {
                result.append(SPECIAL_CHARS_TARGET[index]);
            } else {
                result.append(tchar);
            }
        }
        
        return result.toString();
    }
    
    // 截取文件名
    public static String getBucketPath(String fullPath, String fileName) {
        return fullPath.substring(0, fullPath.lastIndexOf(fileName));
        
    }
    
    /**
     * 将年月日转换成“yyyy-mm-dd”字符串格式
     * 
     * @param cal
     * @return “yyyy-mm-dd”字符串格式
     */
    public static String getFullDateString(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return getFullDateString(year, month, day);
    }
    
    /**
     * 将年月日转换成“yyyy-mm-dd”字符串格式
     * 
     * @param year
     * @param month
     * @param day
     * @return “yyyy-mm-dd”字符串格式
     */
    public static String getFullDateString(int year, int month, int day) {
        StringBuilder result = new StringBuilder();
        result.append(year).append(DATE_DIVIDER);
        if (month < 10) {
            result.append("0");
        }
        result.append(month).append(DATE_DIVIDER);
        if (day < 10) {
            result.append("0");
        }
        result.append(day);
        return result.toString();
    }
    
    /**
     * 将calendar格式转换成年月日
     * 
     * @param cal
     * @return 整数数组[year,month,day]，如果解析错误，返回null
     */
    public static int[] parseFullDateString(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        
        // 月从0开始编号
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return new int[] {year, month, day};
    }
    
    /**
     * 将“yyyy-mm-dd”字符串格式转换成年月日
     * 
     * @param “yyyy-mm-dd”字符串格式
     * @return 整数数组[year,month,day]，如果解析错误，返回null
     */
    public static int[] parseFullDateString(String date) {
        int[] result = null;
        
        if (!isNull(date)) {
            String[] arr = date.split(DATE_DIVIDER);
            if (arr.length == 3) {
                try {
                    int year = Integer.valueOf(arr[0]);
                    int month = Integer.valueOf(arr[1]);
                    int day = Integer.valueOf(arr[2]);
                    
                    // 测试是否合法日期
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, day);
                    
                    result = new int[] {year, month, day};
                } catch (Exception e) {
                    // 可能格式转换错误；
                }
            }
        }
        
        return result;
    }
    
    /**
     * 比较两个日期，如果date1晚于date2，则返回true，否则false 字符串为空，认为无限晚； 如果都为空，则认为date1更晚； 综上，date1只要为空，就一定返回true
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean after(String date1, String date2) {
        int[] d1 = StringUtils.parseFullDateString(date1);
        int[] d2 = StringUtils.parseFullDateString(date2);
        
        if (d1 == null) {
            return true;
        } else if (d2 == null) {
            return false;
        } else if (d1[0] > d2[0]) { // d1年更大，返回true
            return true;
        } else if (d1[0] < d2[0]) { // d1 年更小，返回false
            return false;
        } else if (d1[1] > d2[1]) { // 年相等，d1月更大，返回true
            return true;
        } else if (d1[1] < d2[1]) { // 年相等，d1月更小，返回false
            return false;
        } else if (d1[2] > d2[2]) { // 年相等，月相等，直接比较
            return true;
        } else {
            return false;
        }
    }
    
    /** 明源特殊的编码方式 */
    public static String UrlEncode(String src) {
        String result = StringUtils.BLANK_STRING;
        try {
            result = URLEncoder.encode(src, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 对url中的中文编码
     * 
     * @param url
     * @param enc the encoding scheme to be used
     * @return
     */
    public static String UrlEncodeHanZi(String url, String enc) {
        try {
            Matcher matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(url);
            while (matcher.find()) {
                String tmp = matcher.group();
                url = url.replaceAll(tmp, URLEncoder.encode(tmp, enc));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return url;
    }

    public static String CleanBlank(String str) {
        if (isNull(str)) {
            return BLANK_STRING;
        }
        StringBuffer sb = new StringBuffer();
        char[] sarray = str.toCharArray();
        int s = 0;
        int e = sarray.length;
        while (s < e) {
            switch (sarray[s]) {
                case ' ':
                case '\b':
                case '\n':
                case '\t':
                case '\r': {
                    break;
                }
                default: {
                    sb.append(sarray[s]);
                }
            }
            s++;
        }
        return sb.toString();
    }
    
    // 去掉换行符，指标付，\r
    public static String replaceNewLine(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    
    // 用于对整数表示的国家码、国内码，转换成32进制字符串输出，允许指定固定位数，不足补0，超出截断
    // 把tokenId转换成32进制的字符串jid，最长会有32bit/5bit = 6个字符
    // 0123456789abcd……v
    static public String get32StringFromLong(long value, int fixCharLen) {
        int count = 0;
        long j = value;
        
        // 计算所需要的字符数
        if (fixCharLen == 0) while (j != 0) {
            count++;
            j = j >>> 5;
        }
        else count = fixCharLen;
        // 如果整数为0，也至少要确保转换成为1位字符
        if (count == 0) count = 1;
        
        j = value;
        char[] buffer = new char[count];
        do {
            // 整数不足指定位数，会自然在之前补0
            long t = j & 0x1F;// 取最右边5bit，映射到32进制
            if (t < 10) {
                t += '0';
            } else {
                t = t - 10 + 'a';
            }
            buffer[--count] = (char)t;
            j >>>= 5;
        } while (count > 0);
        
        return new String(buffer);
    }
    
    /**
     * 对src提取子串，已starttag开始，endtag结束，mode表示是
     * 
     * @param src
     * @param startTag 如果stargtag找不到，则从src的0位置开始
     * @param endTag 如果endtag找不到，则认为到src的结束位置；
     * @param mode true:从前往后，false:从后往前
     * @return
     */
    public static String subString(String src, String startTag, String endTag, boolean mode) {
        if (isNull(src)) {
            return BLANK_STRING;
        }
        
        int start, end;
        if (mode) {
            // 从前往后查找开始和结束位置；
            start = startTag == null ? 0 : src.indexOf(startTag);
            end = endTag == null ? src.length() : src.indexOf(endTag);
        } else {
            // 从后往前找开始和结束位置；
            start = startTag == null ? 0 : src.lastIndexOf(startTag);
            end = endTag == null ? src.length() : src.lastIndexOf(endTag);
        }
        
        if (end <= start) {
            // 结束位置在前，返回空串；
            return BLANK_STRING;
        } else {
            // 返回从start到end的字串；
            return src.substring(start + 1, end);
        }
        
    }
    
    /**
     * 将InputStream转换成某种字符编码的String
     * 
     * @param in encoding，指定转换时缺省使用 "ISO-8859-1"编码方式
     * @return
     * @throws UnsupportedEncodingException IOException
     */
    public static String InputStreamTOString(InputStream in)
        throws UnsupportedEncodingException, IOException {
        return InputStreamTOString(in, "ISO-8859-1");
    }
    
    /**
     * 将InputStream转换成某种字符编码的String
     * 
     * @param in
     * @param encoding ，指定转换时使用的编码方式
     * @return
     * @throws UnsupportedEncodingException IOException
     */
    public static String InputStreamTOString(InputStream in, String encoding)
        throws UnsupportedEncodingException, IOException {
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[0x6000];
        int count = -1;
        while ((count = in.read(data, 0, data.length)) != -1)
            outStream.write(data, 0, count);
        
        data = null;
        return new String(outStream.toByteArray(), encoding);
    }
    
    public static String getTimeMillis() {
        Long time = System.currentTimeMillis();
        return time.toString();
    }
    
    public static String trim(String str) {
        if (str != null) {
            return str.trim();
        } else {
            return StringUtils.BLANK_STRING;
        }
    }
    
    // 去除字符串中开头出现的换行符
    public static String removeLineBreakAtFirstOfString(final String str) {
        String retString = str;
        if (isNull(retString)) {
            return BLANK_STRING;
        } else {
            while (retString.startsWith("\n")) {
                retString = str.replaceFirst("\n", "");
            }
        }
        return retString;
    }
    
    public static String getShortAouName(final String aouName) {
        if (isNull(aouName)) {
            return BLANK_STRING;
        }
        String name = aouName.replace(" ", "");
        
        if (isNull(name)) {
            return BLANK_STRING;
        } else {
            int length = name.length();
            int index0 = name.indexOf('(');
            int index1 = name.indexOf('（');
            int index = -1;
            if (index0 != -1) {
                if (index1 != -1) {
                    index = index0 < index1 ? index0 : index1;
                } else {
                    index = index0;
                }
            } else {
                index = index1;
            }
            Pattern pattern = Pattern.compile("^[a-zA-Z]*[a-zA-Z]$");
            Matcher matcher = pattern.matcher(name);
            boolean b = matcher.matches();
            if (index < 1) {// 如果没有(或者(出现在开头
                if (b) {// 英文显示头两个
                    return length > 2 ? name.substring(0, 2) : name;
                }
                return length > 2 ? name.substring(length - 2, length) : name;
            } else {
                int firstIndex = (index - 2) < 0 ? 0 : (index - 2);
                if (b) {// 英文
                    int position = index - 2 >= 0 ? 2 : index;
                    return name.substring(0, position);
                }
                return name.substring(firstIndex, index);
            }
        }
    }
    
    public static String formatKeyValue(final String key, final String value) {
        return "【" + key + ":" + value + "】";
    }
    
    public static String formatPhone(String phone) {
        if (isNull(phone) || phone.length() < 11) {
            return phone;
        } else {
            StringBuilder sbBuilder = new StringBuilder();
            sbBuilder.append(phone.substring(0, 3));
            sbBuilder.append("-");
            sbBuilder.append(phone.substring(3, 7));
            sbBuilder.append("-");
            sbBuilder.append(phone.substring(7, 11));
            return sbBuilder.toString();
        }
    }
    
    public static String indexOf(String content, int index) {
        if (isNull(content) || content.length() <= index) {
            return BLANK_STRING;
        } else {
            return "" + content.charAt(index);
        }
    }
    
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) sb.append(0);
            sb.append(sTemp.toUpperCase(Locale.US));
        }
        return sb.toString();
    }
    
    public static String replaceAllBlank(String str) {
        String dest = "";
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(str);
        dest = m.replaceAll("");
        return getNoneNullString(dest);
    }
    
    public static String sqliteEscape(String keyWord) {
        if (!isNull(keyWord)) {
            keyWord = keyWord.replace("/", "//");
            keyWord = keyWord.replace("'", "''");
            keyWord = keyWord.replace("[", "/[");
            keyWord = keyWord.replace("]", "/]");
            keyWord = keyWord.replace("%", "/%");
            keyWord = keyWord.replace("&", "/&");
            keyWord = keyWord.replace("_", "/_");
            keyWord = keyWord.replace("(", "/(");
            keyWord = keyWord.replace(")", "/)");
        }
        return keyWord;
    }
    
}
