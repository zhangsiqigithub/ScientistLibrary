package com.scientist.sp;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import static java.lang.Boolean.parseBoolean;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/9/29
 * Time: 16:58
 * Desc: copy from http://weishu.me/2016/10/13/sharedpreference-advices/
 */
public class MultiProcessSharedPreferences extends ContentProvider implements SharedPreferences {
    private static final String TAG = "Multi_S_Preferences";
    public static final boolean DEBUG = true;

    //实现SharedPreferences接口所需的成员变量
    private Context mContext;
    private String mName;
    private boolean mIsSafeMode;
    private static final Object CONTENT = new Object();
    private final WeakHashMap<OnSharedPreferenceChangeListener, Object> mListeners = new WeakHashMap<>();

    private BroadcastReceiver mReceiver;

    //ContentProvider所需要要权限、路径
    private static String sAuthoriry;
    private static volatile Uri sAuthorityUrl;
    private UriMatcher mUriMatcher;
    private static final String KEY = "value";
    private static final String KEY_NAME = "name";
    private static final String PATH_WILDCARD = "*/";
    private static final String PATH_GET_ALL = "getAll";
    private static final String PATH_GET_STRING = "getString";
    private static final String PATH_GET_INT = "getInt";
    private static final String PATH_GET_LONG = "getLong";
    private static final String PATH_GET_FLOAT = "getFloat";
    private static final String PATH_GET_BOOLEAN = "getBoolean";
    private static final String PATH_CONTAINS = "contains";
    private static final String PATH_APPLY = "apply";
    private static final String PATH_COMMIT = "commit";
    private static final int GET_ALL = 1;
    private static final int GET_STRING = 2;
    private static final int GET_INT = 3;
    private static final int GET_LONG = 4;
    private static final int GET_FLOAT = 5;
    private static final int GET_BOOLEAN = 6;
    private static final int CONTAINS = 7;
    private static final int APPLY = 8;
    private static final int COMMIT = 9;


    // 如果设备处在“安全模式”下，只有系统自带的ContentProvider才能被正常解析使用；
    private boolean isSafeMode(Context context) {
        boolean isSafeMode = false;
        try {
            isSafeMode = context.getPackageManager().isSafeMode(); // 解决崩溃：java.lang.RuntimeException: Package manager has died at android.app.ApplicationPackageManager.isSafeMode(ApplicationPackageManager.java:820)
        } catch (RuntimeException e) {
            if (!isPackageManagerHasDied(e)) {
                throw e;
            }
        }
        return isSafeMode;
    }

    private void checkInitAuthority(Context context) {
        if (sAuthorityUrl == null) {
            synchronized (MultiProcessSharedPreferences.this) {
                if (sAuthorityUrl == null) {
                    PackageInfo packageInfos = null;
                    try {
                        packageInfos = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
                    } catch (PackageManager.NameNotFoundException e) {
                        if (DEBUG) {
                            e.printStackTrace();
                        }
                    } catch (RuntimeException e) {
                        if (!isPackageManagerHasDied(e)) {
                            throw e;
                        }
                    }
                    if (packageInfos != null && packageInfos.providers != null) {
                        for (ProviderInfo providerInfo : packageInfos.providers) {
                            if (providerInfo.name.equals(MultiProcessSharedPreferences.class.getName())) {
                                sAuthoriry = providerInfo.authority;
                                break;
                            }
                        }
                    }
                    if (sAuthoriry == null) {
                        throw new IllegalArgumentException("'AUTHORITY' initialize failed, Unable to find explicit provider class " + MultiProcessSharedPreferences.class.getName() + "; have you declared this provider in your AndroidManifest.xml?");
                    }
                    sAuthorityUrl = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + sAuthoriry);
                    if (DEBUG) {
                        Log.d(TAG, "checkInitAuthority.AUTHORITY = " + sAuthoriry);
                    }
                }
            }
        }
    }

    // java.lang.RuntimeException: Package manager has died at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:80) ... Caused by: android.os.DeadObjectException at android.os.BinderProxy.transact(Native Method) at android.content.pm.IPackageManager$Stub$Proxy.getPackageInfo(IPackageManager.java:1374)
    private boolean isPackageManagerHasDied(Exception e) {
        return e instanceof RuntimeException
                && e.getMessage() != null
                && e.getMessage().contains("Package manager has died")
                && e.getCause() != null
                && e.getCause() instanceof DeadObjectException;
    }


    //无参构造方法，创建ContentProvider实例时会调用
    public MultiProcessSharedPreferences() {

    }

    public MultiProcessSharedPreferences(Context context, String name) {
        mContext = context.getApplicationContext();
        mName = name;
        mIsSafeMode = isSafeMode(mContext);
        checkInitAuthority(getContext());
    }


    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ?> getAll() {
        Map<String, ?> v = (Map<String, ?>) getValue(PATH_GET_ALL, null, null);
        return v != null ? v : new HashMap<String, Object>();
    }

    @Override
    public String getString(String key, String defValue) {
        return (String) getValue(PATH_GET_STRING, key, defValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return (Set<String>) getValue(PATH_GET_STRING, key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return (Integer) getValue(PATH_GET_INT, key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return (Long) getValue(PATH_GET_LONG, key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return (Float) getValue(PATH_GET_FLOAT, key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return (Boolean) getValue(PATH_GET_BOOLEAN, key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return (Boolean) getValue(PATH_CONTAINS, key, null);
    }

    @Override
    public Editor edit() {
        return new EditorImpl();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            mListeners.put(listener, CONTENT);
            if (mReceiver == null) {
                mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String name = intent.getStringExtra(KEY_NAME);
                        @SuppressWarnings("unchecked")
                        List<String> keysModified = (List<String>) intent.getSerializableExtra(KEY);
                        if (mName.equals(name) && keysModified != null) {
                            Set<OnSharedPreferenceChangeListener> listeners = new HashSet<OnSharedPreferenceChangeListener>(mListeners.keySet());
                            for (int i = keysModified.size() - 1; i >= 0; i--) {
                                final String key = keysModified.get(i);
                                for (OnSharedPreferenceChangeListener listener : listeners) {
                                    if (listener != null) {
                                        listener.onSharedPreferenceChanged(MultiProcessSharedPreferences.this, key);
                                    }
                                }
                            }
                        }
                    }
                };
                mContext.registerReceiver(mReceiver, new IntentFilter(makeAction(mName)));
            }
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            if (mListeners != null) {
                mListeners.remove(listener);
                if (mListeners.isEmpty() && mReceiver != null) {
                    mContext.unregisterReceiver(mReceiver);
                }
            }
        }
    }


    public final class EditorImpl implements Editor {
        private final Map<String, Object> mModified = new HashMap<String, Object>();
        private boolean mClear = false;

        @Override
        public Editor putString(String key, String value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putStringSet(String key, Set<String> values) {
            synchronized (this) {
                mModified.put(key, (values == null) ? null : new HashSet<String>(values));
                return this;
            }
        }

        @Override
        public Editor putInt(String key, int value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putLong(String key, long value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putFloat(String key, float value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor remove(String key) {
            synchronized (this) {
                mModified.put(key, null);
                return this;
            }
        }

        @Override
        public Editor clear() {
            synchronized (this) {
                mClear = true;
                return this;
            }
        }

        @Override
        public void apply() {
            setValue(PATH_APPLY);
        }

        @Override
        public boolean commit() {
            return setValue(PATH_COMMIT);
        }

        private boolean setValue(String pathSegment) {
            boolean result = false;
            if (mIsSafeMode) { // 如果设备处在“安全模式”，返回false；
                return result;
            }
            try {
                checkInitAuthority(mContext);
            } catch (RuntimeException e) { // 解决崩溃：java.lang.RuntimeException: Package manager has died at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:77)
                if (isPackageManagerHasDied(e)) {
                    return result;
                } else {
                    throw e;
                }
            }
            String[] selectionArgs = new String[]{String.valueOf(mClear)};
            synchronized (this) {
                Uri uri = Uri.withAppendedPath(Uri.withAppendedPath(sAuthorityUrl, mName), pathSegment);
                ContentValues values = ReflectionUtil.contentValuesNewInstance((HashMap<String, Object>) mModified);
                try {
                    result = mContext.getContentResolver().update(uri, values, null, selectionArgs) > 0;
                } catch (IllegalArgumentException e) { // 解决ContentProvider所在进程被杀时的抛出的异常：java.lang.IllegalArgumentException: Unknown URI content://xxx.xxx.xxx/xxx/xxx at android.content.ContentResolver.update(ContentResolver.java:1312)
                    if (DEBUG) {
                        e.printStackTrace();
                    }
                } catch (RuntimeException e) { // 解决崩溃：java.lang.RuntimeException: Package manager has died at android.app.ApplicationPackageManager.resolveContentProvider(ApplicationPackageManager.java:609) ... at android.content.ContentResolver.update(ContentResolver.java:1310)
                    if (isPackageManagerHasDied(e)) {
                        return result;
                    } else {
                        throw e;
                    }
                } finally {
                    mModified.clear();
                    mClear = false;
                }
            }
            return result;
        }
    }

    private String makeAction(String name) {
        return String.format("%1$s_%2$s", MultiProcessSharedPreferences.class.getName(), name);
    }

    private Object getValue(String pathSegment, String key, Object defValue) {
        Object v = null;
        if (mIsSafeMode) { // 如果设备处在“安全模式”，返回defValue；
            return defValue;
        }
        try {
            checkInitAuthority(mContext);
        } catch (RuntimeException e) { // 解决崩溃：java.lang.RuntimeException: Package manager has died at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:77)
            if (isPackageManagerHasDied(e)) {
                return defValue;
            } else {
                throw e;
            }
        }
        Uri uri = Uri.withAppendedPath(Uri.withAppendedPath(sAuthorityUrl, mName), pathSegment);
        String[] selectionArgs = new String[]{key, defValue == null ? null : String.valueOf(defValue)};
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, null, null, selectionArgs, null);
        } catch (SecurityException e) { // 解决崩溃：java.lang.SecurityException: Permission Denial: reading  uri content://xxx from pid=2446, uid=10116 requires the provider be exported, or grantUriPermission() at android.content.ContentProvider$Transport.enforceReadPermission(ContentProvider.java:332) ...
            if (DEBUG) {
                e.printStackTrace();
            }
        } catch (RuntimeException e) { // 解决崩溃：java.lang.RuntimeException: Package manager has died at android.app.ApplicationPackageManager.resolveContentProvider(ApplicationPackageManager.java:609) ... at android.content.ContentResolver.query(ContentResolver.java:404)
            if (isPackageManagerHasDied(e)) {
                return defValue;
            } else {
                throw e;
            }
        }
        if (cursor != null) {
            Bundle bundle = null;
            try {
                bundle = cursor.getExtras();
            } catch (RuntimeException e) { // 解决ContentProvider所在进程被杀时的抛出的异常：java.lang.RuntimeException: android.os.DeadObjectException at android.database.BulkCursorToCursorAdaptor.getExtras(BulkCursorToCursorAdaptor.java:173) at android.database.CursorWrapper.getExtras(CursorWrapper.java:94)
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
            if (bundle != null) {
                v = bundle.get(KEY);
                bundle.clear();
            }
            cursor.close();
        }
        return v != null ? v : defValue;
    }


    @Override
    public boolean onCreate() {
        checkInitAuthority(getContext());
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(sAuthoriry, PATH_WILDCARD + PATH_GET_ALL, GET_ALL);
        mUriMatcher.addURI(sAuthoriry, PATH_WILDCARD + PATH_GET_STRING, GET_STRING);
        mUriMatcher.addURI(sAuthoriry, PATH_WILDCARD + PATH_GET_INT, GET_INT);
        mUriMatcher.addURI(sAuthoriry, PATH_WILDCARD + PATH_GET_LONG, GET_LONG);
        mUriMatcher.addURI(sAuthoriry, PATH_WILDCARD + PATH_GET_FLOAT, GET_FLOAT);
        mUriMatcher.addURI(sAuthoriry, PATH_WILDCARD + PATH_GET_BOOLEAN, GET_BOOLEAN);
        mUriMatcher.addURI(sAuthoriry, PATH_WILDCARD + PATH_CONTAINS, CONTAINS);
        mUriMatcher.addURI(sAuthoriry, PATH_WILDCARD + PATH_APPLY, APPLY);
        mUriMatcher.addURI(sAuthoriry, PATH_WILDCARD + PATH_COMMIT, COMMIT);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        boolean isMain = Thread.currentThread() == Looper.getMainLooper().getThread();
        Log.d(TAG, "query: "+isMain);
        //这个name 获取的就是xml的文件名,默认取uri的path字段的第一个
        String name = uri.getPathSegments().get(0);
        String key = null;
        String defValue = null;
        //这个selectionArgs也是从客户端传递进来，表明想要查询的字段以及查无结果默认的返回值
        if (selectionArgs != null) {
            key = selectionArgs[0];
            defValue = selectionArgs[1];
        }
        /**
         * 这个是我们获取数据的来源，默认是以Context.MODE_PRIVATE的形式打开名为name的xml文档
         * 下面会根据当前查询的Uri的地址匹配不同的方法，然后去调用preferences对应获取value的方法
         * 拿到数据后，我们需要以游标的形式返回，所以封装了一个BundleCursor，继承于MatrixCursor，
         */
        SharedPreferences preferences = getContext().getSharedPreferences(name, Context.MODE_PRIVATE); //默认采用Context.MODE_PRIVATE
        Bundle bundle = new Bundle();
        switch (mUriMatcher.match(uri)) {
            case GET_ALL:
                bundle.putSerializable(KEY, (HashMap<String, ?>) preferences.getAll());
                break;
            case GET_STRING:
                bundle.putString(KEY, preferences.getString(key, defValue));
                break;
            case GET_INT:
                bundle.putInt(KEY, preferences.getInt(key, Integer.parseInt(defValue)));
                break;
            case GET_LONG:
                bundle.putLong(KEY, preferences.getLong(key, Long.parseLong(defValue)));
                break;
            case GET_FLOAT:
                bundle.putFloat(KEY, preferences.getFloat(key, Float.parseFloat(defValue)));
                break;
            case GET_BOOLEAN:
                bundle.putBoolean(KEY, preferences.getBoolean(key, parseBoolean(defValue)));
                break;
            case CONTAINS:
                bundle.putBoolean(KEY, preferences.contains(key));
                break;
            default:
                throw new IllegalArgumentException("This is Unknown Uri：" + uri);
        }
        return new BundleCursor(bundle);
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("No external call");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("No external insert");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No external delete");
    }

    @SuppressWarnings("unchecked")
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result = 0;
        String name = uri.getPathSegments().get(0);
        /**
         * 数据源还是getSharedPreferences
         */
        SharedPreferences preferences = getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        int match = mUriMatcher.match(uri);
        switch (match) {
            case APPLY:
            case COMMIT:
                boolean hasListeners = mListeners != null && mListeners.size() > 0;
                ArrayList<String> keysModified = null;
                Map<String, Object> map = null;
                if (hasListeners) {
                    keysModified = new ArrayList<>();
                    map = (Map<String, Object>) preferences.getAll();
                }
                Editor editor = preferences.edit();
                /**
                 *  这个clear就是上面setValue()的时候接收的值，true的情况下，会editor.clear()
                 */
                boolean clear = TextUtils.isEmpty(selectionArgs[0]) ? null : parseBoolean(selectionArgs[0]);
                if (clear) {
                    if (hasListeners && !map.isEmpty()) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            keysModified.add(entry.getKey());
                        }
                    }
                    editor.clear();
                }
                for (Map.Entry<String, Object> entry : values.valueSet()) {
                    String k = entry.getKey();
                    Object v = entry.getValue();
                    if (v instanceof EditorImpl || v == null) {
                        editor.remove(k);
                        if (hasListeners && map.containsKey(k)) {
                            keysModified.add(k);
                        }
                    } else {
                        if (hasListeners && (!map.containsKey(k) || (map.containsKey(k) && !v.equals(map.get(k))))) {
                            keysModified.add(k);
                        }
                    }

                    if (v instanceof String) {
                        editor.putString(k, (String) v);
                    } else if (v instanceof Set) {
                        edit().putStringSet(k, (Set<String>) v);
                    } else if (v instanceof Integer) {
                        editor.putInt(k, (Integer) v);
                    } else if (v instanceof Long) {
                        editor.putLong(k, (Long) v);
                    } else if (v instanceof Float) {
                        editor.putFloat(k, (Float) v);
                    } else if (v instanceof Boolean) {
                        editor.putBoolean(k, (Boolean) v);
                    }
                }
                /**
                 * 调用相应的方法apply()或者commit()
                 * 之后，notifyListeners（）通知监听器
                 */
                switch (match) {
                    case APPLY:
                        editor.apply();
                        result = 1;
                        notifyListeners(name, keysModified);
                        break;
                    case COMMIT:
                        if (editor.commit()) {
                            result = 1;
                            notifyListeners(name, keysModified);
                        }
                        break;
                    default:
                        break;
                }

                values.clear();
                break;
            default:
                throw new IllegalArgumentException("This is Unknown Uri：" + uri);
        }
        return result;
    }

    private void notifyListeners(String name, ArrayList<String> keysModified) {
        if (keysModified != null && !keysModified.isEmpty()) {
            Intent intent = new Intent();
            intent.setAction(makeAction(name));
            intent.setPackage(getContext().getPackageName());
            intent.putExtra(KEY_NAME, name);
            intent.putExtra(KEY, keysModified);
            getContext().sendBroadcast(intent);
        }
    }

    private static class ReflectionUtil {

        public static ContentValues contentValuesNewInstance(HashMap<String, Object> values) {
            try {
                Constructor<ContentValues> c = ContentValues.class.getDeclaredConstructor(new Class[]{HashMap.class}); // hide
                c.setAccessible(true);
                return c.newInstance(values);
            } catch (IllegalArgumentException | IllegalAccessException
                    | NoSuchMethodException | InvocationTargetException
                    | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class BundleCursor extends MatrixCursor {
        private Bundle mBundle;

        public BundleCursor(Bundle extras) {
            super(new String[]{}, 0);
            mBundle = extras;
        }

        @Override
        public Bundle getExtras() {
            return mBundle;
        }

        @Override
        public Bundle respond(Bundle extras) {
            mBundle = extras;
            return mBundle;
        }
    }
}
