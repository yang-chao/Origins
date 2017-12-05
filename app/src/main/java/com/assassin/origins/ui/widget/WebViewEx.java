
package com.assassin.origins.ui.widget;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 这个类解决了Android 4.2以下的WebView注入Javascript对象引发的安全漏洞。
 * 
 * 重载了addJavascriptInterface方法，如果版本>=4.2，则直接使用原有的方法。如果版本<4.2，则使用map缓存待注入对象
 * 
 */
public class WebViewEx extends WebView {
    
    private static final String TAG = "WebViewEx";
    
    private static final boolean DEBUG = true;
    
    private static final String VAR_ARG_PREFIX = "arg";
    private static final String MSG_PROMPT_HEADER = "MyApp:";
    private static final String KEY_INTERFACE_NAME = "obj";
    private static final String KEY_FUNCTION_NAME = "func";
    private static final String KEY_ARG_ARRAY = "args";
    /**
     * 需过滤的Object方法
     */
    private static final String[] filteredMethods = {
        "getClass",
        "hashCode",
        "notify",
        "notifyAll",
        "equals",
        "toString",
        "wait",
        "finalize",
        "clone"
    };

    /**
     * 用于缓存addJavascriptInterface的注册对象
     */
    private HashMap<String, Object> mJsInterfaceMap = new HashMap<String, Object>();
    /**
     * 缓存注入到JavaScript Context的js脚本
     */
    private String mJsStringCache = null;

    private Object mJavascriptInterface;

    private boolean mUseLocalHtml = false;
    
    public WebViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public WebViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WebViewEx(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        // 删除掉Android默认注册的JS接口
        removeSystemExtraJavascriptInterfaces();
    }

    public void useLocalHtml(boolean useLocalHtml) {
        mUseLocalHtml = useLocalHtml;
    }
    
    /**
     * 如果版本>=4.2，则直接基类的addJavascriptInterface。如果版本<4.2，则使用map缓存待注入对象
     */
    @Override
    public void addJavascriptInterface(Object obj, String interfaceName) {
        if (TextUtils.isEmpty(interfaceName) || obj == null) {
            return;
        }
        
        // 如果在4.2以上，直接调用基类的方法来注册
        if (hasJellyBeanMR1() || mUseLocalHtml) {
            super.addJavascriptInterface(obj, interfaceName);
        } else {
            mJsInterfaceMap.put(interfaceName, obj);
        }
        mJavascriptInterface = obj;
    }

    public Object getJavascriptInterface() {
        return mJavascriptInterface;
    }
    
    /**
     * 删除待注入对象，如果版本>=4.2，则使用父类的removeJavascriptInterface。如果版本 < 4.2，则从缓存map中删除注入对象
     */
    @Override
    public void removeJavascriptInterface(String interfaceName) {
        if (hasJellyBeanMR1() || mUseLocalHtml) {
            super.removeJavascriptInterface(interfaceName);
        } else {
            mJsInterfaceMap.remove(interfaceName);
            mJsStringCache = null;
            injectJavascriptInterfaces();
        }
    }

    /**
     * 删除系统已经注册了的JavascriptInterface
     * 
     * 1、[SDK 3.0(API 11), SDK 4.2(API 17))之间的版本需要移除searchBoxJavaBridge_对象
     * 2、设置 -- 辅助功能，启用第三方应用“服务”，会注入accessibility和accessibilityTraversal，需要移除
     * 
     * @return
     */
    private void removeSystemExtraJavascriptInterfaces() {
        if (hasHoneycomb() && !hasJellyBeanMR1()) {
            super.removeJavascriptInterface("searchBoxJavaBridge_");
        }

        if (hasHoneycomb()) {
            // 这里其实没太多作用，因为accessibility本身就是通过 重载之后的addJavascriptInterface注册的
            super.removeJavascriptInterface("accessibility");
            super.removeJavascriptInterface("accessibilityTraversal");
        }
    }
    
    /**
     * 向JavaScript Context注入对象，一般不直接调用此方法，而是调用{@link #injectJavascriptInterfaces(WebView)}
     */
    private void injectJavascriptInterfaces() {
        if (!TextUtils.isEmpty(mJsStringCache)) {
            loadJavascriptInterfaces(mJsStringCache);
            return;
        }
        
        String jsString = genJavascriptInterfacesString();
        mJsStringCache = jsString;
        loadJavascriptInterfaces(mJsStringCache);
    }
    
    /**
     * 如果webView是WebViewEx类型，则向JavaScript Context注入对象
     */
    protected void injectJavascriptInterfaces(WebView webView) {
        if (webView instanceof WebViewEx) {
            injectJavascriptInterfaces();
        } else {
            Log.e(TAG, "出现了未知的WebView" + webView);
        }
    }
    
    /**
     * 使用loadUrl方法向JavaScript Context注入java对象
     */
    private void loadJavascriptInterfaces(String jsStringCache) {
        if (!TextUtils.isEmpty(jsStringCache)) {
            this.loadUrl(jsStringCache);
        }
    }
    
    /**
     * 根据缓存的待注入java对象，生成映射的JavaScript代码，也就是桥梁
     * @return
     */
    private String genJavascriptInterfacesString() {
        if (mJsInterfaceMap.size() == 0) {
            mJsStringCache = null;
            return null;
        }
        
        /*
         * 注入的js语句格式，xxx为对象名，yyy为方法名 
         * 
         * javascript:(function JsAddJavascriptInterface_(){
         *   if(typeof(window.xxx) != 'undefined'){
         *       console.log('window.xxx is exist!!');
         *   }else{
         *       window.xxx = {
         *           yyy:function(arg0,arg1){
         *               return prompt('MyApp:'+JSON.stringify({obj:'xxx',func:'yyy',args:[arg0,arg1]}));
         *           },
         *       };
         *   }
         * })()
         */
        
        Iterator<Entry<String, Object>> iterator = mJsInterfaceMap.entrySet().iterator();
        StringBuilder script = new StringBuilder();
        script.append("javascript:(function JsAddJavascriptInterface_(){");
        
        // 遍历待注入java对象，生成相应的js对象
        try {
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                String interfaceName = entry.getKey();
                Object obj = entry.getValue();
                // 生成相应的js对象
                createJsInstance(interfaceName, obj, script);
            }
        } catch (Exception e) {
            Log.e(TAG, "创建js对象出现异常", e);
        }
        
        script.append("})()");
        
        return script.toString();
    }
    
    /**
     * 根据待注入的java对象，生成js对象
     * 
     * @param interfaceName 对象名 
     * @param obj 待注入的java对象
     * @param script js代码
     */
    private void createJsInstance(String interfaceName, Object obj, StringBuilder script) {
        if (TextUtils.isEmpty(interfaceName) || (null == obj) || (null == script)) {
            return;
        }
        
        Class<? extends Object> objClass = obj.getClass();
        
        script.append("if(window.").append(interfaceName + "!= null && typeof(window.").append(interfaceName).append(")!='undefined'){");
        if (DEBUG) {
            script.append("    console.log('window." + interfaceName + " is exist!!');");
        }
        
        script.append("}else {");
        if (DEBUG) {
            script.append("    console.log('inject window." + interfaceName + "');");
        }
        script.append("    window.").append(interfaceName).append("={");
        
        // 通过反射机制，添加java对象的方法
        Method[] methods = objClass.getMethods();
        for (Method method : methods) {
            // 过滤不是public的方法
            if (!isJavaScriptInterfaceMethod(method)) {
                continue ;
            }
            String methodName = method.getName();
            // 过滤掉Object类的方法
            if (isFilteredMethod(methodName)) {
                continue;
            }
            
            script.append("        ").append(methodName).append(":function(");
            // 添加方法的参数
            int argCount = method.getParameterTypes().length;
            if (argCount > 0) {
                int maxCount = argCount - 1;
                for (int i = 0; i < maxCount; ++i) {
                    script.append(VAR_ARG_PREFIX).append(i).append(",");
                }
                script.append(VAR_ARG_PREFIX).append(argCount - 1);
            }
            
            script.append(") {");
            
            // 添加js回调java对象的方法
            // 首先是 return prompt('MyAPP:'
            if (method.getReturnType() != void.class) {
                script.append("            return ").append("prompt('").append(MSG_PROMPT_HEADER).append("'+");
            } else {
                script.append("            prompt('").append(MSG_PROMPT_HEADER).append("'+");
            }
            
            // 接着是 json串
            script.append("JSON.stringify({");
            script.append(KEY_INTERFACE_NAME).append(":'").append(interfaceName).append("',");
            script.append(KEY_FUNCTION_NAME).append(":'").append(methodName).append("',");
            script.append(KEY_ARG_ARRAY).append(":[");
            // 添加参数到JSON串中
            if (argCount > 0) {
                int max = argCount - 1;
                for (int i = 0; i < max; i++) {
                    script.append(VAR_ARG_PREFIX).append(i).append(",");
                }
                script.append(VAR_ARG_PREFIX).append(max);
            }
            
            // json串 结束
            script.append("]})");
            // prompt方法 结束
            script.append(");");
            // function 结束
            script.append("        }, ");
        }
        
        // End of obj
        script.append("    };");
        // End of if or else
        script.append("}");
    }
    
    /**
     * 判断method是否符合注册到JavaScript Context的要求，目前仅限制必须是public方法
     * 
     * @param method
     * @return
     */
    private boolean isJavaScriptInterfaceMethod(Method method) {
        if (method == null) {
            return false;
        }
        try {
            int modifiers = method.getModifiers();
            return Modifier.isPublic(modifiers);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 解析JavaScript调用prompt的参数message，提取出对象名、方法名，以及参数列表，再利用反射，调用java对象的方法。
     * 
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result
     * @return
     */
    protected boolean handleJsInterface(WebView view, String url, String message, String defaultValue,
            JsPromptResult result) {
        String prefix = MSG_PROMPT_HEADER;
        if (!message.startsWith(prefix)) {
            return false;
        }
        
        String jsonStr = message.substring(prefix.length());
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String interfaceName = jsonObj.getString(KEY_INTERFACE_NAME);
            String methodName = jsonObj.getString(KEY_FUNCTION_NAME);
            methodName = methodName != null ? methodName.trim() : methodName;
            JSONArray argsArray = jsonObj.getJSONArray(KEY_ARG_ARRAY);
            Object[] args = null;
            if (null != argsArray) {
                int count = argsArray.length();
                if (count > 0) {
                    args = new Object[count];
                    
                    for (int i = 0; i < count; ++i) {
                        args[i] = argsArray.get(i);
                        // 如果js进来是null，则需要置为null
                        if (args[i] == JSONObject.NULL) {
                            args[i] = null;
                        }
                    }
                }
            }
            
            if (invokeJSInterfaceMethod(result, interfaceName, methodName, args)) {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "解析prompt.message发生异常", e);
        }
        
        result.cancel();
        return false;
    }
    
    /**
     * 利用反射，调用java对象的方法。
     * 
     * 从缓存中取出key=interfaceName的java对象，并调用其methodName方法
     * 
     * @param result
     * @param interfaceName 对象名
     * @param methodName 方法名
     * @param args 参数列表
     * @return
     */
    private boolean invokeJSInterfaceMethod(JsPromptResult result,
            String interfaceName, String methodName, Object[] args) {
        
        boolean succeed = false;
        final Object obj = mJsInterfaceMap.get(interfaceName);
        if (null == obj) {
            result.cancel();
            return false;
        }
        
        Class<?>[] parameterTypes = null;
        int count = 0;
        if (args != null) {
            count = args.length;
        }
        
        if (count > 0) {
            parameterTypes = new Class[count];
            for (int i = 0; i < count; ++i) {
                parameterTypes[i] = getClassFromJsonObject(args[i]);
            }
        }
        
        try {
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            // 执行接口调用
            Object returnObj = method.invoke(obj, args); 
            boolean isVoid = returnObj == null || returnObj.getClass() == void.class;
            String returnValue = isVoid ? "" : returnObj.toString();
            // 通过prompt返回调用结果
            result.confirm(returnValue);
            succeed = true;
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "js调用java对象" + obj + "." + methodName + "(" + args +")方法不存在", e);
        } catch (Exception e) {
            Log.e(TAG, "js调用java对象" + obj + "." + methodName + "(" + args +")发生异常", e);
        }
        
        result.cancel();
        return succeed;
    }
    
    /**
     * 解析出参数类型
     * 
     * @param obj
     * @return
     */
    private Class<?> getClassFromJsonObject(Object obj) {
        if (obj == null) {
            return String.class;
        }
        Class<?> cls = obj.getClass();
        
        // js对象只支持int double boolean string 几种类型，JSON解析的时候，没有出现Long 和 Float
        if (cls == Integer.class) {
            cls = Integer.TYPE;
        } else if (cls == Double.class) {
            cls = Double.TYPE;
        } else if (cls == Boolean.class) {
            cls = Boolean.TYPE;
        } else {
            cls = String.class;
        }
        
        return cls;
    }
    
    /**
     * 检查是否是被过滤的方法
     * @param methodName
     * @return
     */
    private boolean isFilteredMethod(String methodName) {
        for (String method : filteredMethods) {
            if (method.equals(methodName)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查SDK版本是否 >= 3.0 (API 11)
     * @return
     */
    private boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
    
    /**
     * 检查SDK版本是否 >= 4.2 (API 17)
     * @return
     */
    private boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }
}
