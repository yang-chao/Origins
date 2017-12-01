
package com.huasheng.travel.core.util;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huasheng.travel.BaseApplication;

public class NetUtils {

    //wap网络汇总
    public static final String CTWAP = "ctwap";
    public static final String CMWAP = "cmwap";
    public static final String WAP_3G = "3gwap";
    public static final String UNIWAP = "uniwap";
    /**
     * 获取网络类型
     * 
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null) {
                return activeNetInfo.getTypeName();
            } else {
                return "other";
            }
        } catch (Exception e) {
            return "other";
        }
    }

    /**
     * 检查当前网络是否可用
     * 
     * @return
     */
    public static boolean checkNetwork() {
        boolean flag = false;
        try {
            ConnectivityManager cwjManager = (ConnectivityManager) BaseApplication.getInstance()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cwjManager.getActiveNetworkInfo() != null) {
                flag = cwjManager.getActiveNetworkInfo().isConnected();//.isAvailable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断当前网络是否是wifi
     * 
     * @return
     */
    public static boolean isWifi() {
        
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.getInstance()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断当前网络是否是wap
     * 
     * @param context
     * @return
     */
    public static boolean isCMWAPMobileNet(Context context) {

        if (null == context) {
            return false;
        }

        if (isWifi()) {
            return false;
        } else {
            ConnectivityManager mag = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (null != mag) {
                NetworkInfo mobInfo = mag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (null != mobInfo) {
                    String extrainfo = mobInfo.getExtraInfo();
                    if (null != extrainfo) {
                        extrainfo = extrainfo.toLowerCase();
                        if (extrainfo.contains("wap")) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }

                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }

    /**
     * 获取wap网络条件下的代理服务器，如果为非wap返回为空对象
     * 
     * @param context
     * @return
     */
    public static String getHostbyWAP(Context context) {

        if (null == context) {
            return null;
        }

        if (isWifi()) {
            return null;
        }

        try {
            String result = null;
            ConnectivityManager mag = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != mag) {
                NetworkInfo mobInfo = mag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (null != mobInfo) {
                    
                    String extrainfo = mobInfo.getExtraInfo();
                    if(null!=extrainfo){
                        extrainfo = extrainfo.toLowerCase();
                        if(extrainfo.equals(CMWAP) || extrainfo.equals(WAP_3G)|| extrainfo.equals(UNIWAP)){//移动 or 联通wap代理
                            result = "10.0.0.172";
                        }else{
                            //电信WAP判断
                            Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
                            final Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null); 
                            if (c != null) { 
                                c.moveToFirst(); 
                                final String user = c.getString(c.getColumnIndex("user")); 
                                if (!TextUtils.isEmpty(user)) { 
                                    if (user.toLowerCase().startsWith(CTWAP)) {
                                        result = "10.0.0.200";
                                    }
                                }
                                c.close(); 
                            }
                            
                        }
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            return null;
        }

    }
    
    //IP正则表达式判断
    public static boolean isboolIp(String ipAddress){
        
        try {
            String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";   
            Pattern pattern = Pattern.compile(ip);   
            Matcher matcher = pattern.matcher(ipAddress);   
            return matcher.matches();
        } catch (Exception e) {
            return false;
        }      
    }
   
    /**
     * 设置proxy 在systemProperties中设置参数
     * 
     * @param proxy
     * @param port
     */
    public static void setHttpProxy(String proxy, String port) {
        Properties systemProperties = System.getProperties();
        systemProperties.setProperty("http.proxyHost", proxy);
        systemProperties.setProperty("http.proxyPort", port);
    }
    
    /**
	 * 从url获取含有key的参数值
	 * 
	 * @param url
	 * @param key
	 * @return
	 */
	public static String parseURL(String url, String key) {

		String rst = "";

		if (null == key || "".equalsIgnoreCase(key)) {

		} else {

			int i = url.indexOf("?");
			if (i + 1 <= url.length()) {

				String param = url.substring(i + 1, url.length());
				String[] values = param.split("&");
				if (null != values) {

					for (int index = 0; index < values.length; index++) {

						if (values[index].contains(key)) {

							String[] keys = values[index].split("=");

							if (keys.length > 1) {
								rst = keys[1];
								break;
							}
						}
					}
				}

			}
		}

		return rst;
	}
	
    /**
     * 获得URL的host
     * @param url
     * @return
     */
    public static String getHost(String url){
        if(null==url || "".equals(url)){
            return "";
        }else{
            try {
                String newurl = url.replace("http://", "");
                newurl = newurl.substring(0,newurl.indexOf("/"));
                return newurl;
            } catch (Exception e) {
                return "";
            }
        }
    }

    public static String getUserAgent(boolean web) {
        return String.format(Locale.getDefault(), "Travel%s/%s Build%d Android/%s (%s)", web ? "Web" : "",
                SysUtils.getAppVersion(), SysUtils.getAppVersionCode(),
                SysUtils.getBuildVersionRelease(), SysUtils.getBuildModel());
    }
}
