package com.myg.yxy.utils.http.request;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.*;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * 使用Form方式提交multipart/form-data表单,模拟浏览器方式提交参数
 * @author mdc
 * @date 2017年6月11日
 */
public class FormPart {
	
	/**
	 * 用于上传文件的请求参数构造
	 */
	private MultipartEntityBuilder partBuilder;
	
	/**
	 * 通过AbstractRequest构造FormRequest
	 */
	private FormPart() {
		partBuilder = MultipartEntityBuilder.create();
		//使用浏览器方式
		partBuilder.setLaxMode();
	}
	
	/**
	 * 通过AbstractRequest构造FormRequest
	 */
	private FormPart(String name, String text) {
		this();
		addParameter(name, text);
	}
	
	/**
	 * 创建FormPart
	 * @author mdc
	 * @date 2017年6月11日
	 * @return
	 */
	public static FormPart create(){
		return new FormPart();
	}
	
	/**
	 * 创建FormPart
	 * @author mdc
	 * @date 2017年6月11日
	 * @return
	 */
	public static FormPart create(String name, String text){
		return new FormPart(name, text);
	}
	
	public FormPart setMode(final HttpMultipartMode mode) {
        partBuilder.setMode(mode);
        return this;
    }

    public FormPart setLaxMode() {
    	partBuilder.setLaxMode();
        return this;
    }

    public FormPart setStrictMode() {
    	partBuilder.setStrictMode();
        return this;
    }

    public FormPart setBoundary(final String boundary) {
    	partBuilder.setBoundary(boundary);
        return this;
    }
    
    public FormPart setCharset(final Charset charset) {
        partBuilder.setCharset(charset);
        return this;
    }

    public FormPart addParameter(final FormBodyPart bodyPart) {
    	partBuilder.addPart(bodyPart.getName(), bodyPart.getBody());
        return this;
    }

    public FormPart addParameter(final String name, final ContentBody contentBody) {
    	partBuilder.addPart(name, contentBody);
       return this;
    }

    public FormPart addParameter(final String name, final String text, final ContentType contentType) {
        return addParameter(name, new StringBody(text, contentType));
    }
    
    /**
     * 添加请求参数,默认ContentType设置为text/plain,编码为UTF-8
     * @author mdc
     * @date 2017年6月11日
     * @param name 参数名称
     * @param text 参数值
     * @return
     */
    public FormPart addParameter(final String name, final Serializable text) {
        return addParameter(name, text.toString(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
    }

    /**
     * 添加请求参数,默认ContentType设置为text/plain,编码为UTF-8
     * @author mdc
     * @date 2017年6月11日
     * @param name 参数名称
     * @param text 参数值
     * @return
     */
    public FormPart addParameter(final String name, final String text) {
        return addParameter(name, text, ContentType.TEXT_PLAIN.withCharset("UTF-8"));
    }

    public FormPart addParameter(final String name, final byte[] bytes, final ContentType contentType, final String filename) {
        return addParameter(name, new ByteArrayBody(bytes, contentType, filename));
    }

    public FormPart addParameter(final String name, final byte[] bytes) {
        return addParameter(name, bytes, ContentType.DEFAULT_BINARY, (String)null);
    }

    public FormPart addParameter(final String name, final File file, final ContentType contentType, final String filename) {
        return addParameter(name, new FileBody(file, contentType, filename));
    }

    public FormPart addParameter(final String name, final File file) {
        return addParameter(name, file, ContentType.DEFAULT_BINARY, file != null ? file.getName() : null);
    }

    public FormPart addParameter(final String name, final InputStream stream, final ContentType contentType, final String filename) {
        return addParameter(name, new InputStreamBody(stream, contentType, filename));
    }

    public FormPart addParameter(final String name, final InputStream stream) {
        return addParameter(name, stream, ContentType.DEFAULT_BINARY, (String)null);
    }
    
    /**
	 * 添加参数到参数列表
	 * @author mdc
	 * @date 2015年11月6日
	 * @param parameters 参数列表
	 * @return
	 */
	public FormPart addParameter(final NameValuePair ...parameters) {
		if (parameters != null && parameters.length > 0) {
			for (int i = 0, len = parameters.length; i < len; i++) {
				addParameter(parameters[i].getName(), parameters[i].getValue());
			}
	    }
		return this;
	}
	
	/**
	 * 获取HttpEntity
	 * @author mdc
	 * @date 2017年6月11日
	 * @return
	 */
	public HttpEntity getHttpEntity(){
		return partBuilder.build();
	}
}
