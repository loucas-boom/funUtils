package com.loucas.funnyUtils.service.impl;

import com.loucas.funnyUtils.service.BBCService;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BBCServiceImpl implements BBCService {

    // BBC 官方 RSS 地址
    private static final String RSS_URL = "https://feeds.bbci.co.uk/learningenglish/english/features/6-minute-english/rss";

    @Override
    public void download() throws IOException {
        String baseDir = new File(".").getCanonicalPath() + File.separator + "podcasts";
        File podcastsDir = new File(baseDir);
        if (!podcastsDir.exists()) {
            podcastsDir.mkdirs();
        }

        try {
            System.setProperty("http.proxyHost", "http://127.0.0.1");
            System.setProperty("http.proxyPort", "7890");
            System.setProperty("https.proxyHost", "http://127.0.0.1");
            System.setProperty("https.proxyPort", "7890");

            // 使用 Jsoup 获取 RSS 数据
            Document feed = Jsoup.connect(RSS_URL)
                    .timeout(60 * 1000) // 设置 10 秒超时
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                    .header("Host", "feeds.bbci.co.uk")
                    .header("Referer", "https://www.google.com/")
                    .get();

            Elements items = feed.select("item");

            for (Element item : items) {
                Element titleEl = item.selectFirst("title");
                Element linkEl = item.selectFirst("link");
                Element pubDateEl = item.selectFirst("pubDate");
                Element enclosureEl = item.selectFirst("enclosure");

                if (titleEl == null || linkEl == null || pubDateEl == null || enclosureEl == null) continue;

                String title = titleEl.text();
                String detailUrl = linkEl.text();
                String pubDateStr = pubDateEl.text();

                // 格式化日期
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date pubDate = inputFormat.parse(pubDateStr);
                String formattedDate = outputFormat.format(pubDate);

                // 构建保存路径
                String podPath = baseDir + File.separator + formattedDate + " " + title;
                String mp3FileName = title + ".mp3";
                String mp3Path = podPath + File.separator + mp3FileName;

                File podDir = new File(podPath);
                File mp3File = new File(mp3Path);

                // 如果文件已存在则跳过
                if (mp3File.exists()) {
                    System.out.println("Skip..... " + title);
                    continue;
                }

                // 创建目录
                if (!podDir.exists()) {
                    podDir.mkdirs();
                }

                // 下载音频文件
                String audioUrl = enclosureEl.attr("url");
                FileUtils.copyURLToFile(new URL(audioUrl), mp3File);

                System.out.println("Done ..... " + title);
            }

            System.out.println("Done .... Downloaded");

        } catch (IOException | ParseException e) {
            throw new IOException("Failed to fetch or parse BBC RSS feed: " + e.getMessage(), e);
        }
    }
}
