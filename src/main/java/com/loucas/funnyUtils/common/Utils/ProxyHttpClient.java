package com.loucas.funnyUtils.common.Utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * 支持通过代理发起 HTTP 请求的工具类。
 */
public class ProxyHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(ProxyHttpClient.class);

    private Proxy proxy;
    private int timeout = 60 * 1000; // 默认超时时间：60秒
    private String  userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36";

    public ProxyHttpClient() {}

    public ProxyHttpClient(Proxy proxy) {
        this.proxy = proxy;
    }

    public ProxyHttpClient(Proxy proxy, int timeout) {
        this.proxy = proxy;
        this.timeout = timeout;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 发起 GET 请求并获取响应内容（字符串）
     *
     * @param url 请求地址
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public String getAsString(String url) throws IOException {
        return executeRequest(url, "GET", null);
    }

    /**
     * 发起 GET 请求并解析为 Jsoup Document
     *
     * @param url 请求地址
     * @return Jsoup Document
     * @throws IOException 网络异常
     */
    public Document getAsDocument(String url) throws IOException {
        String html = executeRequest(url, "GET", null);
        return Jsoup.parse(html);
    }

    /**
     * 发起 GET 请求并解析为 Jsoup Document
     *
     * @param url 请求地址
     * @param headers 请求头信息
     * @return Jsoup Document
     * @throws IOException 网络异常
     */
    public Document getAsDocument(String url, Map<String, String> headers) throws IOException {
        logger.info("发起 GET 请求: {}", url);

        Connection connection = Jsoup.connect(url)
                .timeout(timeout)
                .userAgent(userAgent)
                .method(Connection.Method.GET);

        if (proxy != null) {
            InetSocketAddress address = (InetSocketAddress) proxy.address();
            connection.proxy(address.getHostName(), address.getPort());
        }

        // 设置自定义请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.header(entry.getKey(), entry.getValue());
            }
        }

        Connection.Response response = connection.execute();

        if (response.statusCode() != 200) {
            logger.warn("HTTP 请求失败，状态码: {}", response.statusCode());
            throw new IOException("HTTP status code: " + response.statusCode());
        }

        return response.parse();
    }

    /**
     * 发起 POST 请求并获取响应内容（字符串）
     *
     * @param url 请求地址
     * @param data 请求参数（Map 形式）
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public String postAsString(String url, Map<String, String> data) throws IOException {
        return executeRequest(url, "POST", data);
    }

    /**
     * 执行实际的 HTTP 请求
     *
     * @param url 请求地址
     * @param method 请求方法（GET/POST）
     * @param data 请求数据（仅用于 POST）
     * @return 响应内容
     * @throws IOException 网络异常
     */
    private String executeRequest(String url, String method, Map<String, String> data) throws IOException {
        logger.info("发起 {} 请求: {}", method, url);

        Connection connection = Jsoup.connect(url)
                .timeout(timeout)
                .userAgent(userAgent)
                .method(Connection.Method.valueOf(method))
//                .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Cache-Control", "max-age=0")
                .header("Priority", "u=0, i")
                .header("Sec-Ch-Ua", "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"")
                .header("Sec-Ch-Ua-Mobile", "?0")
                .header("Sec-Ch-Ua-Platform", "\"Windows\"")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-User", "?1")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Host", "www.bbc.co.uk")
                .header("Referer", "http://www.bbc.co.uk/learningenglish/english/features/6-minute-english");

        if (proxy != null) {
            InetSocketAddress address = (InetSocketAddress) proxy.address();
            connection.proxy(address.getHostName(), address.getPort());
        }

        if ("POST".equalsIgnoreCase(method) && data != null) {
            connection.data(data);
        }

        Connection.Response response = connection.execute();

        if (response.statusCode() != 200) {
            logger.warn("HTTP 请求失败，状态码: {}", response.statusCode());
            throw new IOException("HTTP status code: " + response.statusCode());
        }

        // 自动识别响应编码并返回字符串
        return Objects.requireNonNull(response.charset()).contains("UTF")
                ? new String(response.bodyAsBytes(), StandardCharsets.UTF_8)
                : new String(response.bodyAsBytes(), "GBK");
    }

    /**
     * 直接获取输入流
     *
     * @param url 请求地址
     * @return 输入流
     * @throws IOException 网络异常
     */
    public InputStream getInputStream(String url) throws IOException {
        URL targetUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection(proxy != null ? proxy : Proxy.NO_PROXY);
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        conn.setRequestProperty("User-Agent", userAgent);

        conn.connect();

        if (conn.getResponseCode() != 200) {
            throw new IOException("Failed to fetch content, HTTP status code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }
}
