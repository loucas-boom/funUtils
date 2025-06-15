package com.loucas.funnyUtils.service.impl;

import com.loucas.funnyUtils.common.Utils.ProxyHttpClient;
import com.loucas.funnyUtils.service.BBCService;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class BBCServiceImpl implements BBCService {

    private static final Logger logger = LoggerFactory.getLogger(BBCServiceImpl.class);

    // BBC 主页地址
    private static final String BASE_URL = "http://www.bbc.co.uk";
    private static final String RSS_PAGE_URL = BASE_URL + "/learningenglish/english/features/6-minute-english";
    private static final String GOOGLE_URL = "http://www.google.com";

    // 代理配置（根据实际情况修改）
    private static final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.31.151", 7890));

    // 超时时间
    private static final int TIMEOUT = 30 * 1000; // 30秒超时


    @Override
    public void download() throws IOException {
        try {
            // 初始化目录
            String baseDir = new File(".").getCanonicalPath() + File.separator + "podcasts";
            File podcastsDir = new File(baseDir);
            if (!podcastsDir.exists()) {
                podcastsDir.mkdirs();
            }

            // 创建 HTTP 客户端
            ProxyHttpClient client = new ProxyHttpClient(proxy);
            client.setTimeout(TIMEOUT);

            // 获取主页面内容
            logger.info("正在访问 BBC 页面: {}", RSS_PAGE_URL);
            Document doc = client.getAsDocument(RSS_PAGE_URL);

            // 查找播客单元
            Elements podcastLinks = doc.select("#bbcle-content .widget-container-full .text");

            for (Element p : podcastLinks) {
                Element titleEl = p.selectFirst("h2 a");
                Element dateEl = p.selectFirst(".details h3");

                if (titleEl == null || dateEl == null) {
                    continue;
                }

                String title = titleEl.text();
                String rawDate = dateEl.text();

                // 提取日期并格式化
                String dateStr = extractDate(rawDate);
                if (dateStr == null) {
                    logger.warn("无法提取日期信息: {}", rawDate);
                    continue;
                }

                SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date pubDate = inputFormat.parse(dateStr);
                String formattedDate = outputFormat.format(pubDate);

                // 构建路径
                String podPath = baseDir + File.separator + formattedDate + " " + title;
                String pdfFileName = title + ".pdf";
                String mp3FileName = title + ".mp3";
                File podDir = new File(podPath);
                File pdfFile = new File(podPath + File.separator + pdfFileName);
                File mp3File = new File(podPath + File.separator + mp3FileName);

                // 如果文件已存在则跳过
                if (pdfFile.exists() && mp3File.exists()) {
                    logger.info("Skip..... {}", title);
                    continue;
                }

                // 创建目录
                if (!podDir.exists()) {
                    podDir.mkdirs();
                }

                // 请求详情页
                String detailUrl = BASE_URL + titleEl.attr("href");
                logger.info("访问详情页: {}", detailUrl);

                // 添加随机延迟，防止高频访问
                randomDelay();

                Document detailDoc = client.getAsDocument(detailUrl);

                // 查找下载区域
                Element downloadWidget = detailDoc.selectFirst("#bbcle-content .widget-container-right .widget-pagelink-download");
                if (downloadWidget == null) {
                    logger.warn("未找到下载区域: {}", detailUrl);
                    continue;
                }

                // 提取链接
                Elements links = downloadWidget.select("a");
                if (links.size() < 2) {
                    logger.warn("下载链接不足: {}", detailUrl);
                    continue;
                }

                String pdfUrl = links.get(0).attr("abs:href");
                String mp3Url = links.get(1).attr("abs:href");

                // 下载 PDF
                if (!pdfFile.exists()) {
                    logger.info("开始下载 PDF: {}", pdfUrl);
                    FileUtils.copyURLToFile(new URL(pdfUrl), pdfFile);
                }

                // 下载 MP3
                if (!mp3File.exists()) {
                    logger.info("开始下载 MP3: {}", mp3Url);
                    FileUtils.copyURLToFile(new URL(mp3Url), mp3File);
                }

                logger.info("Done ..... {}", title);
            }

            logger.info("Done .... Downloaded");

        } catch (IOException | ParseException e) {
            logger.error("解析或下载失败: {}", e.getMessage(), e);
            throw new IOException("解析或下载失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用正则表达式从文本中提取日期
     */
    private String extractDate(String text) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{2}\\s\\w{3}\\s\\d{4})");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 添加随机延迟，防止被识别为爬虫
     */
    private void randomDelay() {
        try {
            int delay = new Random().nextInt(3000) + 2000; // 2~5 秒随机延迟
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("线程中断: {}", e.getMessage());
        }
    }
}
