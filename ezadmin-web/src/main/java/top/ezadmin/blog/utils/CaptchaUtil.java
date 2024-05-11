package top.ezadmin.blog.utils;

import top.ezadmin.common.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CaptchaUtil {

	public static final int LOGIN_MAX_COUNT = 3;

	public static final String LOGIN_FLAGE_FIELD = "LOGIN_FLAGE_FIELD";

	public static final String LOGIN_MAXCOUNT_FLAGE = "LOGIN_MAXCOUNT_FLAGE";

	public static Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

	public static synchronized boolean isSafe(String ip) {
		if (StringUtils.isBlank(ip)) {
			return true;
		}
		if (!map.containsKey(ip)) {
			map.put(ip, new AtomicInteger(0));
			return true;
		}
		if (map.get(ip).get() > 2) {
			return false;
		}
		return true;
	}

	public static void add(String ip) {
		if (!map.containsKey(ip)) {
			map.put(ip, new AtomicInteger(0));
		} else {
			map.get(ip).getAndIncrement();
		}
	}
}
