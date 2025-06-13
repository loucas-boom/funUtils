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
    @Override
    public void download() throws IOException {
        String baseDir = new File(".").getCanonicalPath() + File.separator + "podcasts";
        File podcastsDir = new File(baseDir);
        if (!podcastsDir.exists()) {
            podcastsDir.mkdirs();
        }

        String url = "http://www.bbc.co.uk/learningenglish/english/features/6-minute-english";
        Document doc = Jsoup.connect(url)
                .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3")
                .header("Content-Type", "text/plain;charset=UTF-8")
                .header("Host", "www.bbc.co.uk")
                .header("Referer", url)
                .get();

        Element content = doc.getElementById("bbcle-content");
        if (content == null) {
            System.out.println("Content not found!");
            return;
        }
        Element widget = content.selectFirst("div.widget-container.widget-container-full");
        Elements podcastLinks = widget.select("div.text");

        for (Element p : podcastLinks) {
            Element a = p.selectFirst("h2 a");
            String title = a.text();
            String dateStr = p.selectFirst("div.details h3").text();

            // Extract date
            String datePattern = "(\\d{2} \\w{3} \\d{4})";
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(datePattern).matcher(dateStr);
            if (!matcher.find()) continue;
            String dateRaw = matcher.group(1);
            String date;
            try {
                Date d = new SimpleDateFormat("dd MMM yyyy").parse(dateRaw);
                date = new SimpleDateFormat("yyyy-MM-dd").format(d);
            } catch (ParseException e) {
                continue;
            }

            String podPath = baseDir + File.separator + date + " " + title;
            String pdfPath = podPath + File.separator + title + ".pdf";
            String mp3Path = podPath + File.separator + title + ".mp3";

            File podDir = new File(podPath);
            File pdfFile = new File(pdfPath);
            File mp3File = new File(mp3Path);

            if (!podDir.exists() || !pdfFile.exists() || !mp3File.exists()) {
                if (!podDir.exists()) podDir.mkdirs();

                String detailUrl = "http://www.bbc.co.uk" + a.attr("href");
                Document detailDoc = Jsoup.connect(detailUrl)
                        .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3")
                        .header("Content-Type", "text/plain;charset=UTF-8")
                        .header("Host", "www.bbc.co.uk")
                        .header("Referer", detailUrl)
                        .get();

                Element rightContent = detailDoc.getElementById("bbcle-content")
                        .selectFirst("div.widget-container.widget-container-right");
                Element downloadWidget = rightContent.selectFirst("div.widget.widget-pagelink.widget-pagelink-download ");
                Elements links = downloadWidget.select("a");

                String pdfUrl = links.get(0).attr("href");
                String mp3Url = links.get(1).attr("href");

                if (!pdfFile.exists()) {
                    FileUtils.copyURLToFile(new URL(pdfUrl), pdfFile);
                }
                if (!mp3File.exists()) {
                    FileUtils.copyURLToFile(new URL(mp3Url), mp3File);
                }

                System.out.println("Done ..... " + title);
            } else {
                System.out.println("Skip..... " + title);
            }
        }
        System.out.println("Done .... Downloaded");
    }
}
