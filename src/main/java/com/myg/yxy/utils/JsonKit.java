package com.myg.yxy.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

/**
 * JSON转换工具
 * @author mdc
 * @date 2016年9月2日
 * 
 */
public class JsonKit {

	/**
	 * 输出所有属性到Json字符串
	 */
	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		mapper.getFactory().enable(Feature.ALLOW_COMMENTS);
		mapper.getFactory().enable(Feature.ALLOW_SINGLE_QUOTES);
		
		//只输出非空属性到Json字符串
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * 
	 * 把json输入流中的内容转换为指定类型的对象
	 * 
	 * @param jsonInputStream
	 * @param type
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(InputStream jsonInputStream, TypeReference<T> type) throws JSONException {
		try {
			return mapper.readValue(jsonInputStream, type);
		} catch (Exception t) {
			throw new JSONException("把json输入流中的内容转换为指定类型的对象（" + t.getMessage() + "）", t);
		}
	}

	/**
	 * 
	 * 把json输入流中的内容转换为指定类型的对象
	 * 
	 * @param jsonInputStream
	 * @param clazz
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(InputStream jsonInputStream, Class<T> clazz) throws JSONException {
		try {
			return mapper.readValue(jsonInputStream, clazz);
		} catch (Exception t) {
			throw new JSONException("把json输入流中的内容转换为指定类型的对象（" + t.getMessage() + "）", t);
		}
	}

	/**
	 * 
	 * 把json字节数组转换为指定类型的对象
	 * 
	 * @param jsonByteArray
	 * @param type
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(byte[] jsonByteArray, TypeReference<T> type) throws JSONException {
		try {
			return mapper.readValue(jsonByteArray, type);
		} catch (Exception t) {
			throw new JSONException("把json字节数组转换为指定类型的对象是出错（" + t.getMessage() + "）", t);
		}
	}

	/**
	 * 
	 * 把json字节数组转换为指定类型的对象
	 * 
	 * @param jsonByteArray
	 * @param clazz
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(byte[] jsonByteArray, Class<T> clazz) throws JSONException {
		try {
			return mapper.readValue(jsonByteArray, clazz);
		} catch (Exception t) {
			throw new JSONException("把json字节数组转换为指定类型的对象是出错（" + t.getMessage() + "）", t);
		}
	}

	/**
	 * 
	 * 把json字符串转换为指定类型的对象
	 * 
	 * @param jsonString
	 * @param type
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(String jsonString, TypeReference<T> type) throws JSONException {
		try {
			return mapper.readValue(jsonString, type);
		} catch (Exception t) {
			throw new JSONException("把json字符串转换为指定类型的对象出错（" + t.getMessage() + "）", t);
		}
	}

	/**
	 * 
	 * 转换json字符串为指定对象
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(String jsonString, Class<T> clazz) throws JSONException {
		try {
			return mapper.readValue(jsonString, clazz);
		} catch (Exception t) {
			throw new JSONException("把json字符串转换为指定类型的对象出错（" + t.getMessage() + "）", t);
		}
	}

	/**
	 * 
	 * 把obj转换为指定类型的对象
	 * 
	 * @param obj
	 * @param type
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(Object obj, TypeReference<T> type) throws JSONException {
		if (obj instanceof String) {
			return toBean(obj.toString(), type);
		}
		try {
			return mapper.convertValue(obj, type);
		} catch (Exception t) {
			throw new JSONException("把obj转换为指定类型的对象出错（" + t.getMessage() + "）", t);
		}
	}

	/**
	 * 
	 * 把obj转换为指定对象
	 * 
	 * @param obj
	 * @param clazz
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(Object obj, Class<T> clazz) throws JSONException {
		if (obj instanceof String) {
			return toBean(obj.toString(), clazz);
		}
		try {
			return mapper.convertValue(obj, clazz);
		} catch (Exception t) {
			throw new JSONException("把obj转换为指定对象出错（" + t.getMessage() + ")", t);
		}
	}

	/**
	 * 
	 * 从reader中读取json信息并转换为指定对象
	 * 
	 * @param reader
	 * @param type
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(Reader reader, TypeReference<T> type) throws JSONException {
		try {
			return mapper.readValue(reader, type);
		} catch (Exception t) {
			throw new JSONException("从reader中读取json信息并转换为指定对象出错（" + t.getMessage() + "）", t);
		}
	}

	/**
	 * 
	 * 从reader中读取json信息并转换为指定对象
	 * 
	 * @param reader
	 * @param clazz
	 * @return
	 * @throws JSONException
	 */
	public static <T> T toBean(Reader reader, Class<T> clazz) throws JSONException {
		try {
			return mapper.readValue(reader, clazz);
		} catch (Exception t) {
			throw new JSONException("从reader中读取json信息并转换为指定对象出错（" + t.getMessage() + "）", t);
		}
	}

	/**
	 * 
	 * 把obj转换为json字符串
	 * 
	 * @param obj
	 * @return
	 * @throws JSONException
	 */
	public static String toJson(Object obj) throws JSONException {
		if (obj == null) {
			return "{}";
		}
		
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception t) {
			throw new JSONException("把obj转换为json字符串出错（" + t.getMessage() + "）", t);
		}

	}

	/**
	 * 
	 * 把obj转换为json字节数组
	 * 
	 * @param obj
	 * @return
	 * @throws JSONException
	 */
	public static byte[] toJsonBytes(Object obj) throws JSONException {
		if (obj == null) {
			return "{}".getBytes(Charset.forName("UTF-8"));
		}

		try {
			return mapper.writeValueAsBytes(obj);
		} catch (Exception t) {
			throw new JSONException("把obj转换为json字符串出错（" + t.getMessage() + "）", t);
		}

	}
	
	/**
	 * @return the mapper
	 */
	public static ObjectMapper getMapper() {
		return mapper;
	}
}
