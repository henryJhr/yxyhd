package com.myg.yxy.utils.http.request;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RequestKit {

	private static Logger logger = LoggerFactory.getLogger(RequestBase.class);
	
	
	private static final ConcurrentMap<Class<?>, PropertyDescriptor[]> typeDescriptorCache = new ConcurrentHashMap<Class<?>, PropertyDescriptor[]>();
	
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8){
		private static final long serialVersionUID = -3990873552652201588L;
		{
			put(Boolean.class, boolean.class);
			put(Byte.class, byte.class);
			put(Character.class, char.class);
			put(Double.class, double.class);
			put(Float.class, float.class);
			put(Integer.class, int.class);
			put(Long.class, long.class);
			put(Short.class, short.class);
		}
	};
	
	/**
	 * 关闭OutputStream,忽略异常
	 * @author mdc
	 * @date 2017年6月11日
	 * @param closeable
	 */
	public static void closeQuietly(OutputStream closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
	
	/**
	 * 关闭InputStream,忽略异常
	 * @author mdc
	 * @date 2017年6月11日
	 * @param closeable
	 */
	public static void closeQuietly(InputStream closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}
	
	public static void closeQuietly(ImageInputStream closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}
	
	public static void closeQuietly(ImageOutputStream closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}
	
	public static void closeQuietly(HttpResponse closeable) {
		try {
			if (closeable != null && closeable instanceof CloseableHttpResponse) {
				((CloseableHttpResponse)closeable).close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}
	
	public static void closeQuietly(CloseableHttpClient closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}
	
	
	
	
	/**
	 * 获取LayeredConnectionSocketFactory 使用ssl单向认证
	 * @author mdc
	 * @date 2015年7月17日
	 * @return
	 */
	public static LayeredConnectionSocketFactory getSSLSocketFactory() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();

			return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	

	/**
	 * 把Map参数转换为List
	 * @author mdc
	 * @date 2016年5月12日
	 * @param parameters
	 * @return
	 */
	public static List<NameValuePair> convertNameValuePair(final Map<String, ?> parameters) {
		List<NameValuePair> values = new ArrayList<NameValuePair>(parameters.size());
		
		String value = null;
		for (Entry<String, ?> parameter : parameters.entrySet()) {
			value = parameter.getValue() == null? null : parameter.getValue().toString();
			values.add(new BasicNameValuePair(parameter.getKey(), value));
		}
		return values;
	}
	
	/**
	 * 根据最大次数重试
	 */
	public static HttpRequestRetryHandler getTimesRetryHandler(final int maxTimes) {
		return new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {

				if (executionCount >= maxTimes) {
					// 如果超过最大重试次数，不重试
					return false;
				}

				if (exception instanceof InterruptedIOException) {
					// 超时
					return false;
				}

				if (exception instanceof UnknownHostException) {
					return false;
				}

				if (exception instanceof ConnectTimeoutException) {
					// 连接被拒绝
					return false;
				}

				if (exception instanceof SSLException) {
					// SSL握手异常
					return false;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);

				if (idempotent) {
					// 如果请求被认为是等幂，则重试
					return true;
				}

				return false;
			};
		};
	}
	
	
	/**
	 * Check if the given type represents a "simple" value type:
	 * a primitive, a String or other CharSequence, a Number, a Date,
	 * a URI, a URL, a Locale or a Class.
	 * @param clazz the type to check
	 * @return whether the given type represents a "simple" value type
	 */
	public static boolean isSimpleValueType(Class<?> clazz) {
		return ((clazz.isPrimitive() || primitiveWrapperTypeMap.containsKey(clazz)) || clazz.isEnum() ||
				CharSequence.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) ||
				Date.class.isAssignableFrom(clazz) ||
				URI.class == clazz || URL.class == clazz ||
				Locale.class == clazz || Class.class == clazz);
	}
	
	/**
	 * 获取Class的属性列表
	 * @param clazz
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static final PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws IllegalArgumentException {
		try {
			PropertyDescriptor[] props = typeDescriptorCache.get(clazz);
			if (props != null) {
				return props;
			}
			
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			props = beanInfo.getPropertyDescriptors();
			
			typeDescriptorCache.put(clazz, props);
			return props;
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException(e);
		}
	}
	

	/**
	 * 把Object对象转换为请求参数,以Key/Value形式返回列表
	 * @param object 参数对象,可以是实体类,Map,List<Map> 等
	 * @return ,以Key/Value形式返回列表
	 */
	public static List<NameValuePair> getParameters(final Object object) {
		return getParameters(null, object);
	}
	
	/**
	 * 把Object对象转换为请求参数,以Key/Value形式返回列表
	 * @param name 参数名称
	 * @param object 参数对象,可以是实体类,Map,List<Map> 等
	 * @return ,以Key/Value形式返回列表
	 */
	public static List<NameValuePair> getParameters(final String name, final Object object) {
		List<HttpParamKit.Param> params = HttpParamKit.create(name, object).getParams();
		
		if (params.isEmpty()) {
			return Collections.<NameValuePair>emptyList();
		}
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(params.size());
		for (HttpParamKit.Param param : params) {
			parameters.add(new BasicNameValuePair(param.name, param.value));
		}
		
		return parameters;
	}
}
