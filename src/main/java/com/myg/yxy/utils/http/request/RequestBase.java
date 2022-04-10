package com.myg.yxy.utils.http.request;

import com.myg.yxy.utils.http.HttpKit;
import com.myg.yxy.utils.http.ResponseWrap;
import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Lookup;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

/**
 * Http请求的抽象类
 * @author mdc
 * @date 2017年6月10日
 */
public abstract class RequestBase {
	
	/**
	 * 日志
	 */
	protected static Logger logger = LoggerFactory.getLogger(RequestBase.class);
	
	/**
	 * 默认的SSLSocketFactory
	 */
	public static final LayeredConnectionSocketFactory DEFAULT_SSL_SOCKET_FACTORY = RequestKit.getSSLSocketFactory();
	
	
	/**
	 * 请求对象
	 */
	protected HttpRequestBase request;
	
	/**
	 * 连接管理器
	 */
	private HttpClientConnectionManager connManager;

	/**
	 * form表单
	 */
	protected FormPart form;
	
	/**
	 * 是否是https请求
	 */
	private boolean isHttps;
	
	/**
	 * 用户令牌
	 */
	private Object userToken;
	
	/**
	 * cookie存储器
	 */
	private CookieStore cookieStore;
	/**
	 * 请求的相关配置
	 */
	private Builder config;
	
	/**
	 * 请求上下文
	 */
	private HttpClientContext context;
	
	private HttpClientBuilder clientBuilder; //构建httpclient
	
	private CloseableHttpClient httpClient;
	
	private LayeredConnectionSocketFactory socketFactory; //连接工厂
	
	/**
	 * 构造Request
	 * @param request
	 */
	public RequestBase(HttpRequestBase request){
		
		this.request = request;
		this.context = HttpClientContext.create();
		this.clientBuilder = HttpClientBuilder.create().setConnectionManager(connManager);
		
		this.config = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT);
		this.cookieStore = new BasicCookieStore();
		this.isHttps = request.getURI().getScheme().equalsIgnoreCase("https");
	}
	
	/**
	 * 构造Request,会根据已有的AbstractRequest,
	 * 复制config, cookieStore, Header
	 * @param request
	 * @param prevReq
	 */
	public RequestBase(HttpRequestBase request, RequestBase prevReq){
		this(request);
		
		if(prevReq != null) {
			this.config = prevReq.config;
			this.cookieStore = prevReq.cookieStore;
			this.request.setHeaders(prevReq.getAllHeaders());
		}
	}
	
	/**
	 * 构造Request,会根据已有的AbstractRequest,
	 * 复制config, cookieStore, Header
	 * @param request
	 * @param req
	 */
	public RequestBase(HttpRequestBase request, RequestBase req, HttpClientConnectionManager manager){
		this(request, req);
		this.connManager = manager;
	}
	
	/**
	 * 设置参数列表,会覆盖之前设置的参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public abstract RequestBase setParameters(final NameValuePair ...parameters);
	
	/**
	 * 设置参数列表,会覆盖之前设置的参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public abstract RequestBase setParameters(final List<NameValuePair> parameters);

	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public abstract RequestBase setParameters(final Map<String, ?> parameters);
	
	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param file
	 * @return
	 */
	public RequestBase setParameter(final File file) {
		throw new UnsupportedOperationException("不支持File参数");
	}
	
	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param binary
	 * @return
	 */
	public RequestBase setParameter(final byte[] binary) {
		throw new UnsupportedOperationException("不支持byte[]参数");
	}
	
	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param serializable
	 * @return
	 */
	public RequestBase setParameter(final Serializable serializable) {
		throw new UnsupportedOperationException("不支持Serializable参数");
	}

	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @param object Object类型的参数,例如 简单类型,Map,List,数组,实体等会自动解析为请求参数
	 * @return
	 */
	public abstract RequestBase setParameter(final Object object);

	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param text
	 * @return
	 */
	public abstract RequestBase setParameter(final String text);
	
	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param stream
	 * @return
	 */
	public RequestBase setParameter(final InputStream stream) {
		throw new UnsupportedOperationException("不支持InputStream参数");
	}
	
	/**
	 * 设置参数为Json对象
	 * @author mdc
	 * @date 2015年7月27日
	 * @param parameter 参数对象
	 * @return
	 */
	public RequestBase setParameterJson(final Object parameter) {
		throw new UnsupportedOperationException("不支持Json对象参数");
	}
	

	/**
	 * 添加参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param name
	 * @param value
	 * @return
	 */
	public abstract RequestBase addParameter(final String name, final String value);
	
	/**
	 * 添加参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param name
	 * @param value
	 * @return
	 */
	public abstract RequestBase addParameter(final String name, final Object value);
	
	/**
	 * 添加参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public abstract RequestBase addParameters(final NameValuePair ...parameters);
	
	/**
	 * 添加参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public abstract RequestBase addParameters(final List<NameValuePair> parameters);
	
	/**
	 * 添加参数
	 * @author mdc
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public abstract RequestBase addParameters(final Map<String, ?> parameters);
	

	/**
	 * 获取请求Url
	 * @author mdc
	 * @date 2015年7月17日
	 * @return
	 */
	public URI getURI(){
		return request.getURI();
	}
	
	/**
	 * 设置请求Url
	 * @author mdc
	 * @date 2015年7月17日
	 * @return
	 */
	public RequestBase setURI(final URI uri){
		request.setURI(uri);
		return this;
	}
	
	/**
	 * 设置请求Url
	 * @author mdc
	 * @date 2015年7月17日
	 * @return
	 */
	public RequestBase setURI(final String uri){
		return setURI(URI.create(uri));
	}

	/**
	 * @return 获取{@link #cookieStore}
	 */
	public CookieStore getCookieStore() {
		return cookieStore;
	}

	/**
	 * @param cookieStore 设置cookieStore
	 */
	public RequestBase setCookieStore(final CookieStore cookieStore) {
		this.cookieStore = cookieStore;
		return this;
	}

	/**
	 * @return 获取{@link #isHttps}
	 */
	public boolean isHttps() {
		return isHttps;
	}
	
	/**
	 * 设置内容编码
	 * @author mdc
	 * @date 2015年7月17日
	 * @param encoding
	 * @return
	 */
	public RequestBase setContentEncoding(final String encoding) {
		throw new UnsupportedOperationException("不支持设置编码");
	}
	
	/**
	 * 设置ContentType
	 * @author mdc
	 * @date 2015年7月17日
	 * @param contentType
	 * @return
	 */
	public RequestBase setContentType(final ContentType contentType) {
		throw new UnsupportedOperationException("不支持设置ContentType");
	}
	
	/**
	 * 设置ContentType
	 * @author mdc
	 * @date 2015年7月17日
	 * @param mimeType
	 * @param charset 内容编码
	 * @return
	 */
	public RequestBase setContentType(final String mimeType, final Charset charset) {
		throw new UnsupportedOperationException("不支持设置ContentType");
	}
	
	/**
	 * 添加Header
	 * @author mdc
	 * @date 2015年7月17日
	 * @param name
	 * @param value
	 * @return
	 */
	public RequestBase addHeader(String name, String value) {
		request.addHeader(name, value);
		return this;
	}
	
	/**
	 * 添加Header
	 * @author mdc
	 * @date 2015年7月17日
	 * @param headers
	 * @return
	 */
	public RequestBase addHeaders(Map<String, String> headers) {
		for (Entry<String, String> header : headers.entrySet()) {
			request.addHeader(header.getKey(), header.getValue());
		}
		
		return this;
	}
	
	/**
	 * 设置Header,会覆盖所有之前的Header
	 * @author mdc
	 * @date 2015年7月17日
	 * @param headers
	 * @return
	 */
	public RequestBase setHeaders(Map<String, String> headers) {
		Header [] headerArray = new Header[headers.size()];
		int i = 0;
		
		for (Entry<String, String> header : headers.entrySet()) {
			headerArray[i++] = new BasicHeader(header.getKey(), header.getValue());
		}
		
		request.setHeaders(headerArray);
		return this;
	}
	
	public RequestBase setHeaders(Header [] headers) {
		request.setHeaders(headers);
		return this;
	}
	
	public RequestBase setHeader(Header header) {
		request.setHeader(header);
		return this;
	}
	
	public RequestBase setHeader(String key, String name) {
		request.setHeader(new BasicHeader(key, name));
		return this;
	}
	
	/**
	 * 获取所有Header
	 * @author mdc
	 * @date 2015年7月17日
	 * @return
	 */
	public Header[] getAllHeaders() {
		return request.getAllHeaders();
	}
	
	/**
	 * 移除指定name的Header列表
	 * @author mdc
	 * @date 2015年7月17日
	 * @param name
	 */
	public RequestBase removeHeaders(String name){
		request.removeHeaders(name);
		return this;
	}
	
	/**
	 * 移除指定的Header
	 * @author mdc
	 * @date 2015年7月17日
	 * @param header
	 */
	public RequestBase removeHeader(Header header){
		request.removeHeader(header);
		return this;
	}
	
	/**
	 * 移除指定的Header
	 * @author mdc
	 * @date 2015年7月17日
	 * @param name
	 * @param value
	 */
	public RequestBase removeHeader(String name, String value){
		request.removeHeader(new BasicHeader(name, value));
		return this;
	}
	
	/**
	 * 是否存在指定name的Header
	 * @author mdc
	 * @date 2015年7月17日
	 * @param name
	 * @return
	 */
	public boolean containsHeader(String name){
		return request.containsHeader(name);
	}
	
	/**
	 * 获取Header
	 * @author mdc
	 * @date 2017年6月11日
	 * @param name Header 名称
	 * @return
	 */
	public Header[] getHeaders(String name){
		return request.getHeaders(name);
	}
	
	/**
	 * 获取第一个Header
	 * @author mdc
	 * @date 2017年6月11日
	 * @param name Header 名称
	 * @return
	 */
	public Header getFirstHeader(String name){
		return request.getFirstHeader(name);
	}
	
	/**
	 * 获取最后一个Header
	 * @author mdc
	 * @date 2017年6月11日
	 * @param name Header 名称
	 * @return
	 */
	public Header getLastHeader(String name){
		return request.getLastHeader(name);
	}
	
	/**
	 * 获取Header的迭代器
	 * @author mdc
	 * @date 2015年7月17日
	 * @return
	 */
	public HeaderIterator headerIterator(){
		return request.headerIterator();
	}
	
	/**
	 * 获取协议版本信息
	 * @author mdc
	 * @date 2015年7月17日
	 * @return
	 */
	public ProtocolVersion getProtocolVersion(){
		return request.getProtocolVersion();
	}
	

	/**
	 * @return 获取{@link #request}
	 */
	public HttpRequestBase getRequest() {
		return request;
	}

	/**
	 * @return 获取{@link #config}
	 */
	public Builder getConfig() {
		return config;
	}
	

	/**
	 * @return 获取默认的{@link #context},不能获取到用户自定义的HttpClientContext
	 */
	public HttpClientContext getContext() {
		return context;
	}

	/**
	 * @return 获取{@link #clientBuilder}
	 */
	public HttpClientBuilder getClientBuilder() {
		return clientBuilder;
	}

	/**
	 * 添加Cookie
	 * @author mdc
	 * @date 2015年7月18日
	 * @return
	 */
	public RequestBase addCookie(Cookie ...cookies){
		if(cookies == null) return this;
		
		for (int i = 0; i < cookies.length; i++) {
			cookieStore.addCookie(cookies[i]);
		}
		return this;
	}
	
	/**
	 * 设置网络代理
	 * @author mdc
	 * @date 2015年7月17日
	 * @param hostname
	 * @param port
	 * @return
	 */
	public RequestBase setProxy(String hostname, int port) {
		HttpHost proxy = new HttpHost(hostname, port);
		return setProxy(proxy);
	}
	
	/**
	 * 设置网络代理
	 * @author mdc
	 * @date 2015年7月17日
	 * @param hostname
	 * @param port
	 * @param schema
	 * @return
	 */
	public RequestBase setProxy(String hostname, int port, String schema) {
		HttpHost proxy = new HttpHost(hostname, port, schema);
		return setProxy(proxy);
	}
	
	/**
	 * 设置网络代理
	 * @author mdc
	 * @date 2015年7月17日
	 * @param address
	 * @return
	 */
	public RequestBase setProxy(InetAddress address) {
		HttpHost proxy = new HttpHost(address);
		return setProxy(proxy);
	}
	
	/**
	 * 设置网络代理
	 * @author mdc
	 * @date 2015年7月17日
	 * @param host
	 * @return
	 */
	public RequestBase setProxy(HttpHost host) {
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(host);
		clientBuilder.setRoutePlanner(routePlanner);
		return this;
	}
	
	/**
	 * 设置双向认证的JKS
	 * @author mdc
	 * @date 2015年7月17日
	 * @param jksFilePath jks文件路径
	 * @param password 密码
	 * @return
	 */
	public RequestBase setJKS(String jksFilePath, String password) {
		return setJKS(new File(jksFilePath), password);
	}
	
	/**
	 * 设置双向认证的JKS
	 * @author mdc
	 * @date 2015年7月17日
	 * @param jksFile jks文件
	 * @param password 密码
	 * @return
	 */
	public RequestBase setJKS(File jksFile, String password) {
		InputStream instream = null ;
		try {
			instream = new FileInputStream(jksFile);
			return setJKS(instream, password);
		} 
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		finally {
			RequestKit.closeQuietly(instream);
		}
	}
	
	/**
	 * 设置双向认证的JKS, 不会关闭InputStream
	 * @author mdc
	 * @date 2015年7月17日
	 * @param instream jks流
	 * @param password 密码
	 * @return
	 */
	public RequestBase setJKS(InputStream instream, String password) {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
			keyStore.load(instream, password.toCharArray());
			return setJKS(keyStore, password);
			
		} 
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		finally {
			RequestKit.closeQuietly(instream);
		}
	}
	
	/**
	 * 设置双向认证的JKS,使用当前的秘钥库作为可信任证书库
	 * @param keyStore 密钥库
	 * @return
	 */
	public RequestBase setJKS(KeyStore keyStore, String password) {
		return setJKS(HttpKit.TLS, keyStore, password, keyStore, null);
	}
	
	/**
	 * 设置双向认证的JKS,使用当前的秘钥库作为可信任证书库
	 * @param protocol 使用的协议
	 * @param keyStore 密钥库
	 * @return
	 */
	public RequestBase setJKS(String protocol, KeyStore keyStore, String password) {
		return setJKS(protocol, keyStore, password, keyStore, null);
	}
	
	/**
	 * 设置双向认证的JKS
	 * @param protocol 使用的协议
	 * @param keyStore 密钥库
	 * @param password 密码
	 * @param truststore 可信任证书库
	 * @return
	 */
	public RequestBase setJKS(String protocol, KeyStore keyStore, String password, KeyStore truststore) {
		return setJKS(protocol, keyStore, password, truststore, null);
	}
	
	/**
	 * 设置双向认证的JKS
	 * @param protocol 使用的协议
	 * @param keyStore 密钥库
	 * @param password 密码
	 * @param truststore 可信任证书库
	 * @param trustStrategy 建立证书的可信任的策略
	 * @return
	 */
	public RequestBase setJKS(String protocol, KeyStore keyStore, String password, KeyStore truststore, TrustStrategy trustStrategy) {
		try {
			
			SSLContext sslContext = SSLContexts.custom().useProtocol(protocol)
					.loadKeyMaterial(keyStore, password.toCharArray())
					.loadTrustMaterial(truststore, trustStrategy).build();
			
			socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		
		return this;
	}
	
	/**
	 * 设置Socket超时时间,单位:ms
	 * @author mdc
	 * @date 2015年7月18日
	 * @param socketTimeout
	 * @return
	 */
	public RequestBase setSocketTimeout(int socketTimeout){
		config.setSocketTimeout(socketTimeout);
		return this;
	}
	
	/**
	 * 设置连接超时时间,单位:ms
	 * @author mdc
	 * @date 2015年7月18日
	 * @param connectTimeout
	 * @return
	 */
	public RequestBase setConnectTimeout(int connectTimeout) {
		config.setConnectTimeout(connectTimeout);
		return this;
	}
	
	/**
	 * 设置请求超时时间,单位:ms
	 * @author mdc
	 * @date 2015年7月18日
	 * @param connectionRequestTimeout
	 * @return
	 */
	public RequestBase setConnectionRequestTimeout(int connectionRequestTimeout) {
		config.setConnectionRequestTimeout(connectionRequestTimeout);
		return this;
	}
	
	/**
	 * 设置是否允许服务端循环重定向
	 * @author mdc
	 * @date 2015年7月18日
	 * @param circularRedirectsAllowed
	 * @return
	 */
	public RequestBase setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
		config.setCircularRedirectsAllowed(circularRedirectsAllowed);
		return this;
	}
	
	/**
	 * 设置是否启用跳转
	 * @author mdc
	 * @date 2015年7月18日
	 * @param redirectsEnabled
	 * @return
	 */
	public RequestBase setRedirectsEnabled(boolean redirectsEnabled) {
		config.setRedirectsEnabled(redirectsEnabled);
		return this;
	}
	
	/**
	 * 设置重定向策略
	 * HttpClient会自动处理所有类型的重定向
	 * @author mdc
	 * @date 2017年6月11日
	 * @param redirectStrategy
	 * @return
	 */
	public RequestBase setRedirectStrategy(final RedirectStrategy redirectStrategy) {
		clientBuilder.setRedirectStrategy(redirectStrategy);
		return this;
	}
	
	
	/**
	 * 设置请求失败后重试次数
	 * @author mdc
	 * @date 2017年6月11日
	 * @param maxTimes 最大重试次数
	 * @return
	 */
	public RequestBase setRetryTimes(final int maxTimes){
		clientBuilder.setRetryHandler(RequestKit.getTimesRetryHandler(maxTimes));
		return this;
	}
	
	/**
	 * 设置请求失败后重试处理器
	 * @author mdc
	 * @date 2017年6月11日
	 * @param retryHandler 重试处理器
	 * @return
	 */
	public RequestBase setRetryHandler(final HttpRequestRetryHandler retryHandler){
		clientBuilder.setRetryHandler(retryHandler);
		return this;
	}
	
	/**
	 * 设置KeepAlive策略
	 * @author mdc
	 * @date 2017年6月11日
	 * @param keepAliveStrategy KeepAlive策略接口
	 * @return
	 */
	public RequestBase setKeepAliveStrategy(final ConnectionKeepAliveStrategy keepAliveStrategy){
		clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
		return this;
	}
	
	/**
	 * 设置每个路由值的最大连接数
	 * @author mdc
	 * @date 2017年6月11日
	 * @param maxConnPerRoute 大连接数
	 * @return
	 */
	public RequestBase setMaxConnPerRoute(final int maxConnPerRoute){
		clientBuilder.setMaxConnPerRoute(maxConnPerRoute);
		return this;
	}
	
	/**
	 * 设置用户令牌处理器
	 * @author mdc
	 * @date 2017年6月11日
	 * @param userTokenHandler 用户令牌处理器
	 * @return
	 */
	public RequestBase setUserTokenHandler(final UserTokenHandler userTokenHandler){
		clientBuilder.setUserTokenHandler(userTokenHandler);
		return this;
	}
	
	/**
	 * 设置用户令牌
	 * @author mdc
	 * @date 2017年6月11日
	 * @param userToken 用户令牌
	 * @return
	 */
	public RequestBase setUserToken(final Object userToken){
		this.userToken = userToken;
		return this;
	}
	
	/**
	 * 添加请求拦截器
	 * @author mdc
	 * @date 2017年6月11日
	 * @param itcp 请求拦截器
	 * @return
	 */
	public RequestBase addInterceptorLast(final HttpRequestInterceptor itcp){
		clientBuilder.addInterceptorLast(itcp);
		return this;
	}
	
	/**
	 * 添加响应拦截器
	 * @author mdc
	 * @date 2017年6月11日
	 * @param itcp 响应拦截器
	 * @return
	 */
	public RequestBase addInterceptorLast(final HttpResponseInterceptor itcp){
		clientBuilder.addInterceptorLast(itcp);
		return this;
	}
	
	/**
	 * 设置重定向的次数
	 * @author mdc
	 * @date 2015年7月18日
	 * @param maxRedirects
	 * @return
	 */
	public RequestBase setMaxRedirects(int maxRedirects){
		config.setMaxRedirects(maxRedirects);
		return this;
	}
	

	/**
	 * 设置UserAgent
	 * @author mdc
	 * @date 2017年6月11日
	 * @param userAgent
	 * @return
	 */
	public RequestBase setUserAgent(String userAgent) {
		clientBuilder.setUserAgent(userAgent);
		return this;
	}
	
	/**
	 * 设置最大连接数
	 * @author mdc
	 * @date 2017年6月12日
	 * @param maxConnTotal 最大连接数
	 * @return
	 */
	public RequestBase setMaxConnTotal(final int maxConnTotal){
		clientBuilder.setMaxConnTotal(maxConnTotal);
		return this;
	}
	
	/**
	 * 设置认证方案provider,目前有
	 * <ul>
	 * <li>Basic: Basic认证方案是在RFC2617号文档中定义的。这种授权方案用明文来传输凭证信息，所以它是不安全的</li>
	 * <li>Digest: Digest（摘要）认证方案是在RFC2617号文档中定义的。Digest认证方案比Basic方案安全的多</li>
	 * <li>NTLM: NTLM认证方案是个专有的认证方案，由微软开发，并且针对windows平台做了优化。NTLM被认为比Digest更安全。</li>
	 * <li>SPNEGO/Kerberos : SPNEGO(Simple and Protected GSSAPI Negotiation Mechanism)是GSSAPI的一个“伪机制”，它用来协商真正的认证机制。SPNEGO最明显的用途是在微软的HTTP协商认证机制拓展上。可协商的子机制包括NTLM、Kerberos。当双方均不知道对方能使用/提供什么协议的情况下，可以使用SP认证协议。目前，HttpCLient只支持Kerberos机制。</li>
	 * </ul>
	 * @author mdc
	 * @date 2017年6月11日
	 * @param credentialsProvider 认证方案provider
	 * @return
	 */
	public RequestBase setCredentials(CredentialsProvider credentialsProvider){
		context.setCredentialsProvider(credentialsProvider);
		return this;
	}
	
	/**
	 * 设置用户凭证,使用默认BasicCredentialsProvider实现
	 * @author mdc
	 * @date 2017年6月11日
	 * @param authscope 认证范围
	 * @param credentials 凭证
	 * @return
	 */
	public RequestBase setCredentials(AuthScope authscope, Credentials credentials){
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(authscope, credentials);
		return this.setCredentials(credsProvider);
	}
	
	/**
	 * 设置用户凭证,使用认证范围为AuthScope.ANY
	 * @author mdc
	 * @date 2017年6月11日
	 * @param credentials 凭证
	 * @return
	 */
	public RequestBase setCredentials(Credentials credentials){
		return this.setCredentials(AuthScope.ANY, credentials);
	}
	
	/**
	 * 设置用户凭证,使用认证范围为AuthScope.ANY,使用Basic认证方案
	 * @author mdc
	 * @date 2017年6月11日
	 * @param userName 用户名
	 * @param password 密码
	 * @return
	 */
	public RequestBase setCredentials(final String userName, final String password){
		return this.setCredentials(new UsernamePasswordCredentials(userName, password));
	}
	
	/**
	 * 设置windows系统用户凭证,使用认证范围为AuthScope.ANY, Credentials的实现NTCredentials
	 * @author mdc
	 * @date 2017年6月11日
	 * @param usernamePassword 用户名密码,字符串格式为 domain/username:password
	 * @return
	 */
	public RequestBase setCredentials4NT(final String usernamePassword){
		
		Args.notNull(usernamePassword, "Username:password string");
        final String username;
        final String password;
        final NTUserPrincipal principal;
        
        final int atColon = usernamePassword.indexOf(':');
        if (atColon >= 0) {
            username = usernamePassword.substring(0, atColon);
            password = usernamePassword.substring(atColon + 1);
        } else {
            username = usernamePassword;
            password = null;
        }
        
        final int atSlash = username.indexOf('/');
        if (atSlash >= 0) {
            principal = new NTUserPrincipal(username.substring(0, atSlash).toUpperCase(Locale.ROOT), username.substring(atSlash + 1));
        } else {
            principal = new NTUserPrincipal(null, username.substring(atSlash + 1));
        }
        
		return this.setCredentials4NT(principal.getUsername(), password, null, principal.getDomain());
	}
	
	/**
	 * 设置windows系统用户凭证,使用认证范围为AuthScope.ANY, Credentials的实现NTCredentials
	 * @author mdc
	 * @date 2017年6月11日
	 * @param userName 用户名
	 * @param password 密码
	 * @param workstation 工作站
	 * @param domain 域
	 * @return
	 */
	public RequestBase setCredentials4NT(
			final String userName,
            final String password,
            final String workstation,
            final String domain){
		
		return this.setCredentials(new NTCredentials(userName, password, workstation, domain));
	}
	
	/**
	 * 设置认证方案
	 * @author mdc
	 * @date 2017年6月11日
	 * @param lookup 认证方案
	 * @return
	 */
	public RequestBase setAuthSchemeRegistry(final Lookup<AuthSchemeProvider> lookup){
		context.setAuthSchemeRegistry(lookup);
		return this;
	}
	
	/**
	 * 设置目标主机
	 * @author mdc
	 * @date 2017年6月11日
	 * @param host 目标主机
	 * @return
	 */
	public RequestBase setTargetHost(final HttpHost host){
		context.setTargetHost(host);
		return this;
	}
	
	
	
	/**
	 * 设置认证数据的缓存
	 * @author mdc
	 * @date 2017年6月11日
	 * @param authCache 认证数据缓存
	 * @return
	 */
	public RequestBase setAuthCache(final AuthCache authCache){
		context.setAuthCache(authCache);
		return this;
	}
	
	/**
	 * 设置连接管理器
	 * @author mdc
	 * @date 2017年6月12日
	 * @param manager 连接管理器
	 * @return
	 */
	public RequestBase setConnectionManager(HttpClientConnectionManager manager){
		this.connManager = manager;
		return this;
	}

	/**
	 * @return 获取{@link #connManager}
	 */
	public HttpClientConnectionManager getConnectionManager() {
		return connManager;
	}

	/**
	 * 使用浏览器模式上传文件,提交参数
	 * @author mdc
	 * @date 2017年6月11日
	 * @return
	 */
	public RequestBase useForm(FormPart form){
		this.form = form;
		return this;
	}
	
	/**
	 * 构建请求
	 * @author mdc
	 * @date 2017年6月11日
	 * @param context 请求上下文
	 */
	protected abstract void settingRequest(HttpClientContext context);

	/**
	 * 设置请求配置项
	 * @author mdc
	 * @date 2017年6月11日
	 */
	private HttpClientContext settings(HttpContext context){
		
		if (isHttps) {
			if(socketFactory != null) {
				clientBuilder.setSSLSocketFactory(socketFactory);
			}
			else {
				clientBuilder.setSSLSocketFactory(DEFAULT_SSL_SOCKET_FACTORY);
			}
		}
		
		clientBuilder.setDefaultCookieStore(cookieStore);
		request.setConfig(config.build());
		httpClient = clientBuilder.build();
		
		HttpClientContext clientContext = null;
		if(context == null){
			clientContext = this.context ;
		} else {
			clientContext = buildContext(context);
		}
		
		clientContext.setCookieStore(cookieStore);
		clientContext.setRequestConfig(request.getConfig());
		
		if(userToken != null) {
			clientContext.setUserToken(userToken);
		}
		
		this.settingRequest(clientContext);
		return clientContext;
	}

	/**
	 * 设置HttpContext
	 * @author mdc
	 * @date 2017年6月12日
	 * @param context HttpContext
	 * @return
	 */
	private HttpClientContext buildContext(HttpContext context) {
		HttpClientContext clientContext = HttpClientContext.adapt(context);
			
		if (this.context.getCredentialsProvider() != null) {
			clientContext.setCredentialsProvider(this.context.getCredentialsProvider());
		}
		
		if (this.context.getAuthCache() != null) {
			clientContext.setAuthCache(this.context.getAuthCache());
		}
		
		if (this.context.getAuthSchemeRegistry() != null) {
			clientContext.setAuthSchemeRegistry(this.context.getAuthSchemeRegistry());
		}
		
		if (this.context.getTargetHost() != null) {
			clientContext.setTargetHost(this.context.getTargetHost());
		}
		
		this.context = clientContext;
		
		return clientContext;
	}
	
	/**
	 * 执行同步请求,使用默认的HttpContext
	 * 带有连接池管理的HttpClient
	 * @author mdc
	 * @date 2017年6月11日
	 * @return 返回一个包装了HttpResponse的对象
	 */
	public ResponseWrap execute(){
		return execute((HttpContext)null);
	}
	
	/**
	 * 执行同步请求
	 * 带有连接池管理的HttpClient
	 * @author mdc
	 * @date 2017年6月11日
	 * @param context httpContext可以包含任意类型的对象，因此如果在多线程中共享上下文会不安全。推荐每个线程都只包含自己的http上下文
	 * @return 返回一个包装了HttpResponse的对象
	 */
	public ResponseWrap execute(HttpContext context){
		try {
			
			HttpClientContext clientContext = settings(context);
			CloseableHttpResponse response = httpClient.execute(request, clientContext);
			return new ResponseWrap(httpClient, request, response, clientContext);

		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 执行同步请求
	 * 带有连接池管理的HttpClient
	 * @author mdc
	 * @date 2017年6月11日
	 * @param responseHandler 使用HttpResponse自定义返回值
	 * @return 返回由ResponseHandler处理后的结果
	 */
	public <T> T execute(final ResponseHandler<T> responseHandler){
		return execute(responseHandler, null);
	}
	
	/**
	 * 执行同步请求
	 * 带有连接池管理的HttpClient
	 * @author mdc
	 * @date 2017年6月11日
	 * @param responseHandler 使用HttpResponse自定义返回值
	 * @param context httpContext可以包含任意类型的对象，因此如果在多线程中共享上下文会不安全。推荐每个线程都只包含自己的http上下文
	 * @return 返回由ResponseHandler处理后的结果
	 */
	public <T> T execute(final ResponseHandler<T> responseHandler, HttpContext context){
		try {
			HttpClientContext clientContext = settings(context);
			return httpClient.execute(request, responseHandler, clientContext);
		} 
		catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 多线程异步请求,获取结果时会阻塞
	 * 这里会使用默认上下文
	 * @author mdc
	 * @date 2017年6月11日
	 * @param executorService java ExecutorService
	 * @param handler 响应处理器
	 * @return 返回一个异步且带有跟踪指标的对象
	 */
	public <T> RequestFuture<T> executeCallback(final ExecutorService executorService, final ResponseHandler<T> handler){
		return executeCallback(executorService, settings(null), handler);
	}
	
	/**
	 * 多线程带有回调函数的请求,获取结果时会阻塞
	 * @author mdc
	 * @date 2017年6月11日
	 * @param executorService java ExecutorService
	 * @param context 请求上下文
	 * @param handler 响应处理器
	 * @return 返回一个异步且带有跟踪指标的对象
	 */
	public <T> RequestFuture<T> executeCallback(
			final ExecutorService executorService, 
			final HttpContext context, 
			final ResponseHandler<T> handler){
		
		return executeCallback(executorService, context, handler, null);
	}
	

	/**
	 * 多线程带有回调函数的请求,获取结果时会阻塞
	 * @author mdc
	 * @date 2017年6月11日
	 * @param executorService java ExecutorService
	 * @param handler 响应处理器
	 * @param callback 回调函数
	 * @return 返回一个异步且带有跟踪指标的对象
	 */
	public <T> RequestFuture<T> executeCallback(
			final ExecutorService executorService, 
			final ResponseHandler<T> handler, 
			final FutureCallback<T> callback){
		
		return executeCallback(executorService, null, handler, callback);
	}
	
	/**
	 * 多线程带有回调函数的请求,获取结果时会阻塞
	 * @author mdc
	 * @date 2017年6月11日
	 * @param executorService java ExecutorService
	 * @param context 请求上下文,如果自定义了HttpContext,那么之前有关HttpContext的设置将被忽略
	 * @param handler 响应处理器
	 * @param callback 回调函数
	 * @return 返回一个异步且带有跟踪指标的对象
	 */
	public <T> RequestFuture<T> executeCallback(
			final ExecutorService executorService, 
			final HttpContext context,
			final ResponseHandler<T> handler, 
			final FutureCallback<T> callback){
		
		//不能直接把settings(context)写到execute里,否则空指针
		HttpClientContext clientContext = settings(context);
		FutureRequestExecutionService service = new FutureRequestExecutionService(httpClient, executorService);
		HttpRequestFutureTask<T> futureTask = service.execute(request, clientContext, handler, callback);
		
		return new RequestFuture<T>(futureTask, service);
	}
}