package com.myg.yxy.utils.http.request;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * Http请求参数构建工具
 * 针对使用Form表单提交
 * 对于空值不参与拼装参数
 * @author mdc
 * @date 2017年11月30日
 */
public class HttpParamKit {
	
	/**
	 * 组装后的参数列表
	 */
	private List<Param> params = new ArrayList<Param>();
	
	/**
	 * 单个参数,包含参数名称和对应的值
	 * @author mdc
	 * @date 2017年11月30日
	 */
	public static class Param {
		/**
		 * 参数名称
		 */
		public String name;
		/**
		 * 参数值
		 */
		public String value;

		/**
		 * @param name 参数名称
		 * @param value 参数值
		 */
		public Param(String name, Object value) {
			this.name = name;
			this.value = value == null ? "" : value.toString();
		}

		@Override
		public String toString() {
			return "Param [name=" + name + ", value=" + value + "]";
		}
	}
	
	/**
	 * 创建一个HttpParamUtils实例
	 */
	private HttpParamKit() {}
	
	/**
	 * 创建一个HttpParamUtils实例
	 * @param value 请求参数值
	 */
	private HttpParamKit(Object value) {
		this(null, value);
	}
	
	/**
	 * 创建一个HttpParamUtils实例
	 * @param name 请求参数名称
	 * @param value 请求参数值
	 */
	private HttpParamKit(String name, Object value) {
		this.addValue(name, value);
	}
	
	/**
	 * 创建一个HttpParamUtils实例
	 * @return HttpParamUtils
	 */
	public static HttpParamKit create() {
		return new HttpParamKit();
	}
	
	/**
	 * 创建一个HttpParamUtils实例
	 * @param value 请求参数值
	 * @return HttpParamUtils
	 */
	public static HttpParamKit create(Object value) {
		return new HttpParamKit(value);
	}
	
	/**
	 * 创建一个HttpParamUtils实例
	 * @param name 请求参数名称
	 * @param value 请求参数值
	 * @return HttpParamUtils
	 */
	public static HttpParamKit create(String name, Object value) {
		return new HttpParamKit(name, value);
	}
	
	public HttpParamKit addParam(String name, Number value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, CharSequence value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Boolean value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, double[] value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, float[] value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, int[] value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, long[] value) {
		return addValue(name, value);
	}

	public HttpParamKit addParam(String name, Boolean[] value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Double[] value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Float[] value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Integer[] value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Long[] value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, CharSequence[] value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Map<String, ?> value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Iterable<?> value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Collection<?> value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Set<?> value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(String name, Object value) {
		return addValue(name, value);
	}
	
	public HttpParamKit addParam(Object value) {
		return addValue(null, value);
	}
	
	/**
	 * 获取URL参数,形如 key=value1&key2=value2
	 * @return
	 */
	public String getUri() {
		StringBuilder uri = new StringBuilder();
		for (Param param : params) {
			uri.append('&').append(encodeUri(param.name)).append('=').append(encodeUri(param.value));
		}
		
		if (!params.isEmpty()) {
			uri.deleteCharAt(0);
		}
		
		return uri.toString();
	}
	
	/**
	 * 获取name和value转换后的参数列表
	 * @return
	 */
	public List<Param> getParams(){
		return params;
	}

	@Override
	public String toString() {
		return getUri();
	}

	/**
	 * 添加参数
	 * @param name 参数名称 ,可以是一个实体类,但不支持文件形式
	 * @param value 参数值
	 * @return
	 */
	private HttpParamKit addValue(String name, Object value) {
		if (value == null) {
			return this;
		}

		// 简单类型
		Class<?> clazz = value.getClass();
		
		if (RequestKit.isSimpleValueType(clazz)) {
			params.add(new Param(name, value));
			return this;
		}

		if (clazz.isArray()) {
			int length = Array.getLength(value);
			
			// 简单类型,直接组装
			if (RequestKit.isSimpleValueType(clazz.getComponentType())) {
				for (int i = 0; i < length; i++) {
					params.add(new Param(name, Array.get(value, i)));
				}
				return this;
			}

			// 对象类型
			for (int i = 0; i < length; i++) {
				addValue((name == null ? "" : name) + "[" + i + "]", Array.get(value, i));
			}
			return this;
		}

		// 处理Map集合
		if (value instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, ?> mapValue = (Map<String, ?>) value;

			for (Entry<String, ?> item : mapValue.entrySet()) {
				if (item.getValue() == null) {
					continue;
				}

				if (RequestKit.isSimpleValueType(item.getValue().getClass())) {
					params.add(new Param(name == null ? item.getKey() : name + "['" + item.getKey() + "']", item.getValue()));
					continue;
				}

				addValue(name == null ? item.getKey() : name + "['" + item.getKey() + "']", item.getValue());
			}

			return this;
		}

		if (value instanceof Iterable) {
			Iterable<?> listvalue = (Iterable<?>) value;
			Object object;
			int i = 0;

			for (Iterator<?> iterator = listvalue.iterator(); iterator.hasNext();) {
				object = iterator.next();
				if (object == null) {
					continue;
				}

				if (RequestKit.isSimpleValueType(object.getClass())) {
					params.add(new Param((name == null ? "" : name) + "[" + i + "]", object));
					i++;
					continue;
				}

				addValue((name == null ? "" : name) + "[" + i + "]", object);
				i++;
			}

			return this;
		}

		PropertyDescriptor[] descriptors = RequestKit.getPropertyDescriptors(clazz);
		
		Object object = null;
		Method readMethod;

		for (int i = 0; i < descriptors.length; i++) {
			readMethod = descriptors[i].getReadMethod();
			if (readMethod == null || "getClass".equals(readMethod.getName())) {
				continue;
			}

			try {
				if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
					readMethod.setAccessible(true);
				}
				object = readMethod.invoke(value);
			} catch (Throwable ex) {
				throw new IllegalArgumentException("Could not read property'" + descriptors[i].getName() + "' from source to target", ex);
			}
			
			if (object == null) {
				continue;
			}

			if (RequestKit.isSimpleValueType(object.getClass())) {
				params.add(new Param((name == null ? "" : name + ".") + descriptors[i].getDisplayName(), object));
				continue;
			}
			addValue((name == null ? "" : name + ".") + descriptors[i].getDisplayName(), object);
		}
		return this;
	}

	/**
	 * 对参数编码
	 * 
	 * @param string
	 * @return
	 */
	private String encodeUri(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return string;
		}
	}
}
