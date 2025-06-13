package com.loucas.funnyUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ProxyTest {
    public static void main(String[] args) {
        // 设置代理（与你在服务中设置的一致）
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7890");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7890");

        try {
            // 测试访问 Google，确认是否能通过代理访问外网
            URL url = new URL("http://www.google.com");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
            System.out.println("✅ 代理测试成功！");
        } catch (Exception e) {
            System.err.println("❌ 代理测试失败！");
            e.printStackTrace();
        }
    }
}
