package com.alotuser.address.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONReader;
/**
 * JsonUtil
 * @author I6view
 *
 */
public class JsonUtil {

	/**
	 * Parses the json stream of the url as a {@link JSONArray}.
     * Returns {@code null} if received {@link URL} is {@code null}.
	 * @param url the specified url to be parsed
	 * @param type the specified actual class of {@link T}
	 * @return
	 */
	public static <T> List<T> parseArray(URL url, Class<T> type) {

		if (url == null) {
			return null;
		}

		try (InputStream is = url.openStream()) {
			return parseArray(is, type);
		} catch (IOException e) {
			throw new JSONException("JSON#parseArray cannot parse '" + url + "' to '" + JSONArray.class + "'", e);
		}

	}
	/**
	 * Parses the json stream as a {@link JSONArray}. Returns {@code null}
     * if received {@link InputStream} is {@code null} or its content is null.
	 * @param in the specified stream to be parsed
	 * @param type the specified actual class of {@link T}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> parseArray(InputStream in, Class<T> type) {
		if (in == null) {
			return null;
		}

		final JSONReader.Context context = JSONFactory.createReadContext();
		try (JSONReader reader = JSONReader.of(in, StandardCharsets.UTF_8, context)) {
			if (reader.nextIfNull()) {
				return null;
			}
			List<T> list = reader.readArray(type);
			reader.handleResolveTasks(list);
			return list;
		}
	}

	/**
     * Parses the json string as a list of {@link T}. Returns
     * {@code null} if received {@link String} is {@code null} or empty.
     *
     * @param text the specified string to be parsed
     * @param type the specified actual class of {@link T}
     * @return {@link List} or {@code null}
     */
	public static <T> List<T> parseArray(String text, Class<T> type) {
		return JSON.parseArray(text, type);
	}
	
	
	/**
	 * 转换为JSON字符串
	 *
	 * @param obj 被转为JSON的对象
	 * @return JSON字符串
	 */
	public static String toJsonStr(Object obj) {
		return JSON.toJSONString(obj);
	}
	
}
