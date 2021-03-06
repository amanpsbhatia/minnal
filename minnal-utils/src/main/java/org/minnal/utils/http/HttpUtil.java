/**
 * 
 */
package org.minnal.utils.http;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;

/**
 * @author ganeshs
 *
 */
public class HttpUtil {
	
	public static final String SEPARATOR = "/";

	public static Map<String, String> getQueryParameters(URI uri) {
		String query = uri.getQuery();
		Map<String, String> params = new HashMap<String, String>();
		if (! Strings.isNullOrEmpty(query)) {
			Iterator<String> iterator = Splitter.on("&").split(decode(query)).iterator();
			while(iterator.hasNext()) {
				String[] keyValue =  iterator.next().split("=");
				params.put(keyValue[0], keyValue[1]);
			}
		}
		return params;
	}
	
	public static String decode(String value) {
		try {
			return URLDecoder.decode(value, Charsets.UTF_8.name());
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed while decoding the value - " + value, e);
		}
	}
	
	public static String encode(String value) {
		try {
			return URLEncoder.encode(value, Charsets.UTF_8.name());
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed while encoding the value - " + value, e);
		}
	}
	
	public static URI createURI(String scheme, String host, String path) {
		String uri = scheme + "://" + host + path;
		return createURI(uri);
	}
	
	public static URI createURI(String uri) {
		if (uri.endsWith("/") && uri.length() > 1) {
			uri = uri.substring(0, uri.length() - 1);
		}
		try {
			return new URI(uri);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid uri - " + uri);
		}
	}

	public static Map<String, String> getCookies(String cookieHeader) {
		Map<String, String> cookies = new HashMap<String, String>();
		if(Strings.isNullOrEmpty(cookieHeader)) {
			return cookies;
		}
		
		StringTokenizer tokenizer = new StringTokenizer(cookieHeader, ";");
		String header = null;
		String value = null;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			int index = token.indexOf("=");
			if (index > 0) {
				header = token.substring(0, index).trim();
				value = token.substring(index + 1).trim();
				if (! cookies.containsKey(header)) {
					cookies.put(header, value);
				}
			}
		}
		return cookies;
	}
	
	public static String createCookie(Map<String, String> cookies) {
		StringBuffer buffer = new StringBuffer();
		for(Entry<String, String> entry : cookies.entrySet()) {
			buffer.append(entry.getKey() + "=" + entry.getValue());
//			buffer.append(";");
		}
		return buffer.toString();
	}
	
	/**
	 * Structures the url to look like a valid path.
	 * 
	 * @param url
	 * @return
	 */
	public static String structureUrl(String url) {
		if (url == null || url.equals("")) {
			return SEPARATOR;
		}
		if (! url.startsWith(SEPARATOR)) {
			url = SEPARATOR + url;
		}
		if (url.endsWith(SEPARATOR)) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}
	
	/**
	 * Returns the first segment from the path
	 * 
	 * @param path
	 * @return
	 */
	public static String getRootSegment(String path) {
		String[] segments = Iterators.toArray(Splitter.on('/').omitEmptyStrings().split(path).iterator(), String.class);
		return structureUrl(segments.length > 0 ? segments[0] : "");
	}
	
	/**
	 * Concats the given paths 
	 * 
	 * @param paths
	 * @return
	 */
	public static String concatPaths(String... paths) {
		if (paths == null || paths.length == 0) {
			return SEPARATOR;
		}
		StringBuilder path = new StringBuilder();
		for (int i = 0; i < paths.length; i++) {
			if (! Strings.isNullOrEmpty(paths[i])) {
				path.append(structureUrl(paths[i]));
			}
		}
		return structureUrl(path.toString());
	}
	
	/**
	 * Returns the relative path given the base path and absolute path
	 * 
	 * @param basePath
	 * @param absolutePath
	 * @return
	 */
	public static String deriveRelativePath(String basePath, String absolutePath) {
		absolutePath = structureUrl(absolutePath);
		basePath = structureUrl(basePath);
		String relativePath = absolutePath;
		if (absolutePath.startsWith(basePath)) {
			relativePath = absolutePath.substring(basePath.length());
		}
//		return relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
		return relativePath;
	}
	
	public static void main(String[] args) {
		System.out.println(concatPaths("", "", ""));
	}
}
