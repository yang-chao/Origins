/*
 * Created on 2004-5-23 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package com.huasheng.travel.core.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {
    static StringUtils instance = new StringUtils();

    private StringUtils() {
    }

    public static StringUtils getInstance() {
        if (instance == null) {
            instance = new StringUtils();
        }
        return instance;
    }

    /*
     * 转为半角字符串
     */
    public static String toBanjiao(final String qjstr) {
        if (qjstr == null) {
            return qjstr;
        }
        int length = qjstr.length();
        StringBuilder outStr = new StringBuilder();
        String Tstr = "";
        byte[] b = null;
        for (int i = 0; i < length; i++) {
            try {
                Tstr = qjstr.substring(i, i + 1);
                b = Tstr.getBytes("unicode");
            } catch (java.io.UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (b[3] == -1) {
                b[2] = (byte) (b[2] + 32);
                b[3] = 0;
                try {
                    outStr.append(new String(b, "unicode"));
                } catch (java.io.UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                outStr.append(Tstr);
            }
        }
        return outStr.toString();
    }

    /**
     * 将传进来的字符串的html标签删除
     *
     * @param str
     * @return
     */
    public static String stripTags(final String str) {
        try {
            return str.replaceAll("<\\p{Alnum}+?>", "");
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 将传进来的字符串的换行符替成 <br/>
     *
     * @param str
     * @return
     */
    public static String nl2br(final String str) {
        if (str == null) {
            return null;
        }
        return str.replace("\r\n", "<br>").replace("\n", "<br>");
    }

    public static boolean containCNWords(final String body) {
        if (body == null) {
            return false;
        }
        int length = body.length();
        for (int i = 0; i < length; i++) {
            if (body.charAt(i) > 255) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串查找
     *
     * @param str
     * @param s1
     * @return
     */
    public static int find(final String str, final String s1) {
        try {
            int findx = 0;
            Pattern pt = Pattern.compile("(" + s1 + ")", Pattern.CASE_INSENSITIVE);
            Matcher m = pt.matcher(str);
            while (m.find()) {
                findx++;
            }
            return findx;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 得到字符串的md5哈希
     *
     * @param input
     * @return
     */
    public static String md5(String input) {
        try {
            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = input.getBytes();
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }


    /**
     * 将字符串str按splitlit切分，1.4已经有方法
     *
     * @param str
     * @param splitlit
     * @return 一个数组
     */
    public static String[] split(final String str, final String splitlit) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(str, splitlit);
            String[] out = new String[stringtokenizer.countTokens()];
            int i = 0;
            while (stringtokenizer.hasMoreTokens()) {
                out[i] = stringtokenizer.nextToken();
                i++;
            }
            return out;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 过滤HTML字符
     *
     * @param source
     * @return
     */
    public static String htmlFilter(String source) {
        if (source == null) {
            return "";
        }
        return source.replaceAll("&quot;", "\"")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&nbsp;", " ");
    }

    /**
     * 转换html特殊字符为html码
     *
     * @param str
     * @return
     */
    public static String htmlSpecialChars(final String str) {
        try {
            if (str.trim() == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            int length = str.length();
            char ch = ' ';
            for (int i = 0; i < length; i++) {
                ch = str.charAt(i);
                if (ch == '&') {
                    sb.append("&amp;");
                } else if (ch == '<') {
                    sb.append("&lt;");
                } else if (ch == '>') {
                    sb.append("&gt;");
                } else if (ch == '"') {
                    sb.append("&quot;");
                } else {
                    sb.append(ch);
                }
            }
            String value = sb.toString();
            if (value.replace("&nbsp;", "").replace("　", "").trim().length() == 0) {
                return "";
            }
            return value;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 转换特殊字符
     *
     * @param str
     * @return
     */
    public static String changeChar(final String str1) {
        try {
            if (str1.trim() == null) {
                return "";
            }
            String str = "_" + str1;
            StringBuilder sb = new StringBuilder();
            char ch = ' ';
            for (int i = 0; i < str.length(); i++) {
                ch = str.charAt(i);
                if (ch == '#' && str.charAt(i - 1) == '-' && str.charAt(i + 1) == 'i') {
                    sb.append("\\#");
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString().substring(1);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 删除字符串中的所有空格和换行
     *
     * @param str
     * @return
     */
    public static String stripSpace(final String str) {
        try {
            String newStr = str;
            newStr = newStr.replace("&nbsp;", "");
            newStr = newStr.replace(" ", "");
            newStr = newStr.replace("\r", "");
            newStr = newStr.replace("\n", "");
            return newStr;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 在长数字前补零
     *
     * @param num    数字
     * @param length 输出位数
     */
    public static String addzero(final long num, final int length) {
        StringBuilder sb = new StringBuilder();
        int max = (length - Long.toString(num).length());
        if (num < Math.pow(10, length - 1)) {
            for (int i = 0; i < max; i++) {
                sb.append("0");
            }
        }
        sb.append(num);
        return sb.toString();
    }

    /**
     * 在数字前补零
     *
     * @param num    数字
     * @param length 输出位数
     */
    public static String addzero(final int num, final int length) {
        StringBuilder sb = new StringBuilder();
        int max = (length - Integer.toString(num).length());
        if (num < Math.pow(10, length - 1)) {
            for (int i = 0; i < max; i++) {
                sb.append("0");
            }
        }
        sb.append(num);
        return sb.toString();
    }

    /**
     * 判断字符串是否一个数字
     *
     * @param str
     * @return
     */
    public static boolean isNum(final String str) {
        try {
            long num = Long.parseLong(str);
            if ((Long.toString(num)).equals(str)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 字符串数组相加
     *
     * @param a
     * @param b
     * @return
     */
    public static String[] addStrings(final String[] a, final String[] b) {
        try {
            if (b == null) {
                return a;
            }
            if (a == null) {
                return b;
            }
            String[] temp = new String[a.length + b.length];
            for (int i = 0; i < a.length; i++) {
                temp[i] = a[i];
            }
            for (int i = 0; i < b.length; i++) {
                temp[i + a.length] = b[i];
            }
            return temp;
        } catch (Exception e) {
            return a;
        }
    }

    /**
     * 字符串数组加一个字符串
     *
     * @param a
     * @param b
     * @return
     */
    public static String[] addString(final String[] a, final String b) {
        try {
            if (b == null) {
                return a;
            }
            if (a == null) {
                String[] s = new String[1];
                s[0] = b;
                return s;
            }
            String[] s = new String[a.length + 1];
            for (int i = 0; i < a.length; i++) {
                s[i] = a[i];
            }
            s[a.length] = b;
            return s;
        } catch (Exception e) {
            return a;
        }
    }

    /**
     * 判断字符串是否正常
     *
     * @param str
     * @return
     */
    public static boolean isFine(final String str) {
        try {
            if (str == null || str.trim().length() == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断一组字符串是否都正常
     *
     * @param str
     * @return
     */
    public static boolean isFine(final String[] str) {
        try {
            for (int i = 0; i < str.length; i++) {
                if (!isFine(str[i])) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAllFine(String... strs) {
        try {
            for (String s : strs) {
                if (!isFine(s)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查关键字
     *
     * @param words
     * @param str
     * @param tostr
     * @return
     */
    public static boolean haveWord(final String[] words, final String str) {
        for (int i = 0; i < words.length; i++) {
            if (str.indexOf(words[i]) > -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * s中的s1替换成s2
     *
     * @param s
     * @param s1
     * @param s2
     * @return
     */
    public static String replace(final String str, final String from, final String to) {
        char[] chars = str.toCharArray();
        char[] to_chars = to.toCharArray();
        char[] from_chars = from.toCharArray();
        StringBuilder sb = new StringBuilder();
        char from_first = from_chars[0];
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == from_first) {
                boolean isok = true;
                for (int k = 1; k < from_chars.length; k++) {
                    if (i + k < chars.length && chars[i + k] != from_chars[k]) {
                        isok = false;
                        break;
                    }
                }
                if (isok) {
                    for (int j = 0; j < to_chars.length; j++) {
                        sb.append(to_chars[j]);
                    }
                    i += from_chars.length - 1;
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 替换第一个出现的词语
     *
     * @param str
     * @param from
     * @param to
     * @return
     */
    public static String replaceFirst(final String str, final String from, final String to) {
        char[] chars = str.toCharArray();
        char[] to_chars = to.toCharArray();
        char[] from_chars = from.toCharArray();
        StringBuilder sb = new StringBuilder();
        char from_first = from.charAt(0);
        boolean replaceok = false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == from_first && replaceok) {
                boolean isok = true;
                for (int k = 0; k < from_chars.length; k++) {
                    if (chars[i + k] != from_chars[k]) {
                        isok = false;
                        break;
                    }
                }
                //System.out.println(from_first);
                if (isok) {
                    for (int j = 0; j < to_chars.length; j++) {
                        sb.append(to_chars[j]);
                    }
                    replaceok = true;
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 随机字符串
     *
     * @param i
     * @return
     */
    public static String randomString(final int i) {
        if (i < 1) {
            return null;
        }
        Random randGen = new Random();
        char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                .toCharArray();
        char[] ac = new char[i];
        for (int j = 0; j < ac.length; j++) {
            ac[j] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(ac);
    }

    /**
     * 得到后缀名
     *
     * @param filename
     * @return
     */
    public static String getExt(final String filename) {
        int last_point_position = filename.lastIndexOf(".");
        if (last_point_position > 0) {
            return filename.substring(last_point_position);
        }
        return "";
    }

    /**
     * URL解码
     *
     * @param str
     * @return
     */
    public static String urlEncode(final String str) {
        try {
            return URLDecoder.decode(str, "GBK");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * URL编码
     *
     * @param str
     * @return
     */
    public static String urlEncode(final String str, String type) {
        try {
            return URLEncoder.encode(str, type);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * URL解码
     *
     * @param str
     * @return
     */
    public static String URLDecode(String str, String type) {
        try {
            return URLDecoder.decode(str, type);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * BASE64 编码
     */
    public static String base64Encode(final String s) {
        /*
		 * try { return (new sun.misc.BASE64Encoder()).encode(s.getBytes()); }
		 * catch (Exception e) { return s; }
		 */
        return "";
    }

    /**
     * 通用编码
     *
     * @param s
     * @return
     */
    public static String encode(final String s) {
		/*
		 * try { s = BASE64encode(s); return URLEncode(s); } catch (Exception e)
		 * { return s; }
		 */
        return "";
    }

    /**
     * 通用解码
     *
     * @param s
     * @return
     */
    public static String decode(final String s) {
		/*
		 * try { s = URLDecode(s); return BASE64decode(s); } catch (Exception e)
		 * { return s; }
		 */
        return "";
    }

    /**
     * 得到一个规则的网址
     */
    public static String removeAuthorisation(final String uri) {
        if (uri.indexOf("@") != -1 && (uri.startsWith("ftp://") || uri.startsWith("http://"))) {
            return uri.substring(0, uri.indexOf(":") + 2) + uri.substring(uri.indexOf("@") + 1);
        }
        return uri;
    }

    /**
     * 得到没有javascript代码,css代码等等的安全HTML
     *
     * @param html
     * @return
     */
    public static String safeHTML(final String html) {
        return "";
    }

    /**
     * 转换字符串成boolean值
     *
     * @param s
     * @return
     */
    public static boolean toBoolean(final String s) {
        if (s == null || s.length() == 0 || s.equals("false") || s.equals("0")) {
            return false;
        }
        return true;
    }

    /**
     * 转换数字成boolean值
     *
     * @param i
     * @return
     */
    public static boolean toBoolean(final int i) {
        if (i <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 截取中文字符串
     *
     * @param str
     * @param start
     * @param length
     * @return
     */
    public static String subString(final String str, int start, final int length) {
        try {
            if (str.getBytes("GBK").length <= length * 2) {
                return str;
            }
            if (length <= 0) {
                return "";
            }
            byte[] bytes = str.getBytes("GBK");
            int check = 1;
            for (int i = 0; i < start * 2; i++) {
                check = check * bytes[i];
                if (check > 1000) {
                    check = 1;
                }
                if (check < -1000) {
                    check = -1;
                }
            }
            if (check < 0 && bytes[start * 2] < 0) {
                start--;
            }
            byte[] newbytes = new byte[length * 2];
            check = 1;
            for (int i = 0; i < newbytes.length; i++) {
                newbytes[i] = bytes[start + i];
                check = check * bytes[start + i];
                if (check > 1000) {
                    check = 1;
                }
                if (check < -1000) {
                    check = -1;
                }
            }
            if (check < 0 && newbytes[newbytes.length - 1] < 0) {
                newbytes[newbytes.length - 1] = 32;
            }
            return new String(newbytes, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 得到全文搜索字符串
     */
    public static String getIntString(final String str) {
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder();
        int iscn = 0;
        for (int i = 0; i < bytes.length; i++) {
            int j = bytes[i];
            if (bytes[i] < 0) {
                j = j * (-1);
                if (j < 10) {
                    sb.append('0');
                }
                sb.append(j);
                iscn++;
                if (iscn == 2) {
                    sb.append(' ');
                    iscn = 0;
                }
            } else {
                sb.append(new String(new byte[]{bytes[i]}));
                if (i <= bytes.length - 2 && bytes[i + 1] < 0) {
                    sb.append(' ');
                }
            }
        }
        return sb.toString();
    }

    public static String removeLetterOrDigit(final String body) {
        StringBuilder sb = new StringBuilder();

        int length = body.length();
        for (int i = 0; i < length; i++) {
            char c = body.charAt(i);
            // 非空格，字符，数字，其他一律过滤
            // if(!Character.isLetterOrDigit(c) && !Character.isSpaceChar(c)){
            if (!Character.isLetterOrDigit(c)) {
                continue;
            }
            sb.append(c);

        }
        return sb.toString();
    }

    /**
     * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
     *
     * @param char c, 需要判断的字符
     * @return boolean, 返回true,Ascill字符
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param String s ,需要得到长度的字符串
     * @return int, 得到的字符串长度
     */
    private static int length(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    public static String getCutStr(String origin, int len) {
        return getCutStr(origin, len, "...");
    }

    /**
     * 截取一段总长度为len位的字符串(一个中文占1位,两个英文占1位),总长字符串中,包括more符号<br>
     *
     * @param String origin, 原始字符串
     * @param int    len, 截取长度(一个汉字长度按2算的)
     * @return String, 返回的字符串
     * @author patriotlml
     */
    public static String getCutStr(String origin, int len, String more) {
        int len2 = len * 2;
        int moreLength = more == null ? 0 : more.trim().length();
        if (origin == null || origin.equals("") || len2 < 1)
            return "";

        byte[] strByte = new byte[len2];
        if (len2 >= length(origin)) {
            return origin;
        }

        len2 = len2 - moreLength;
        System.arraycopy(origin.getBytes(), 0, strByte, 0, len2);
        int count = 0;
        for (int i = 0; i < len2; i++) {
            int value = (int) strByte[i];
            if (value < 0) {
                count++;
            }
        }
        if (count % 2 != 0) {
            len2 = (len2 == 1) ? ++len2 : --len2;
        }
        return new String(strByte, 0, len2) + more.trim();
    }

    /**
     * 数字格式化，number>10000则显示成X.X万
     */
    public static String getNumberText(int number) {
        String ret = String.valueOf(number);
        try {
            float count = Float.parseFloat(ret);
            if (count >= 10000) {
                count = count / 10000;
                DecimalFormat decimalFormat = new DecimalFormat(".0");
                ret = decimalFormat.format(count) + "万";
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 格式化为00:00:00
     *
     * @param duration
     * @return
     */
    public static String getDuration(int duration) {
        int seconds = duration % 60;
        int minutes = (duration / 60) % 60;
        int hours = duration / 3600;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
