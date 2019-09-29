package com.cn.ceshi.cache;

import com.cn.ceshi.model.RsaInfoConfig;
import com.cn.ceshi.model.UrlProxyConfig;
import com.cn.ceshi.util.FileHelper;
import com.cn.ceshi.util.LooperThread;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class DiskCache {
    private static final String CACHE_DB_NAME = "cache_db";
    private static final String CACHE_TABLE_RSA = "t_rsa";
    private static final String CACHE_TABLE_URL_PROXY = "t_url_proxy";
    private static ConcurrentHashMap<String, RsaInfoConfig> rsaInfoMap;
    private static ConcurrentHashMap<String, UrlProxyConfig> urlProxyConfigMap;
    private static final String CACHE_PATH;
    private static SyncCacheDBThread syncCacheDBThread;

    static {
        String curPath = DiskCache.class.getResource("/").getPath();
        File file = new File(curPath);
        File tomcatHome = file.getParentFile().getParentFile().getParentFile().getParentFile();
        File cacheFile = new File(tomcatHome, "cache");
        if (cacheFile.exists() || !cacheFile.isDirectory()) {
            cacheFile.mkdirs();
        }
        CACHE_PATH = cacheFile.getAbsolutePath();
        System.out.println("wenjun test CACHE_PATH: " + CACHE_PATH);

        File cacheFolder = new File(CACHE_PATH);
        File dbFile = new File(cacheFolder, CACHE_DB_NAME);
        HashMap<String, Object> dbMap = new HashMap<>();
        if (dbFile.exists()) {
            dbMap = (HashMap<String, Object>) FileHelper.readFromFile(dbFile.getAbsolutePath());
        }
        if (dbMap == null) {
            dbMap = new HashMap<>();
        }

        rsaInfoMap = (ConcurrentHashMap<String, RsaInfoConfig>) dbMap.get(CACHE_TABLE_RSA);
        if (rsaInfoMap == null) {
            rsaInfoMap = new ConcurrentHashMap<>();
        }
        urlProxyConfigMap = (ConcurrentHashMap<String, UrlProxyConfig>) dbMap.get(CACHE_TABLE_URL_PROXY);
        if (urlProxyConfigMap == null) {
            urlProxyConfigMap = new ConcurrentHashMap<>();
        }

        syncCacheDBThread = new SyncCacheDBThread();
        syncCacheDBThread.start();
    }

    public static boolean saveRsaInfo(String key, RsaInfoConfig info) {
        rsaInfoMap.put(key, info);
        syncCacheDBThread.saveToCacheDelay(CACHE_TABLE_RSA, rsaInfoMap, 60 * 1000);
        return true;
    }

    public static RsaInfoConfig getRsaInfo(String key) {
        return rsaInfoMap.get(key);
    }

    public static HashMap<String, RsaInfoConfig> getAllRsaInfo() {
        HashMap<String, RsaInfoConfig> map = new HashMap<>();
        map.putAll(rsaInfoMap);
        return map;
    }

    public static boolean saveUrlProxyConfig(String key, UrlProxyConfig proxy) {
        urlProxyConfigMap.put(key, proxy);
        syncCacheDBThread.saveToCacheDelay(CACHE_TABLE_URL_PROXY, urlProxyConfigMap, 60 * 1000);
        return true;
    }

    public static UrlProxyConfig getUrlProxyConfig(String key) {
        return urlProxyConfigMap.get(key);
    }

    public static HashMap<String, UrlProxyConfig> getAllUrlProxyConfig() {
        HashMap<String, UrlProxyConfig> map = new HashMap<>();
        map.putAll(urlProxyConfigMap);
        return map;
    }

    private static class SyncCacheDBThread extends LooperThread {
        private HashMap<String, Object> map = new HashMap<>();
        private LinkedBlockingQueue<Long> delayQueue = new LinkedBlockingQueue<Long>();
        private File rsaFile = new File(CACHE_PATH, CACHE_DB_NAME);

        public synchronized void saveToCacheDelay(String cacheName, Object obj, long delay) {
            if (cacheName == null) {
                return;
            }
            map.put(cacheName, obj);
            try {
                delayQueue.put(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void loop() throws Throwable {
            long interval = delayQueue.take();
            Thread.sleep(interval);
            synchronized (this) {
                FileHelper.saveToFile(rsaFile.getAbsolutePath(), map);
                delayQueue.clear();
            }
        }
    }


}
