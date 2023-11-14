package com.example.util;

import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 解决国内访问Github慢的问题
 * <br><br>
 * 获取github相关的ip地址信息<pre>
 * https://github.com/
 * https://assets-cdn.github.com/
 * http://global.ssl.fastly.net/
 * codeload.github.com</pre>
 * <pre>生成的host文件替换系统的hosts。</pre>
 * Mac OS X终端输入 ：<pre>
 * sudo killall -HUP mDNSResponder
 * sudo rcnscd restart</pre>
 * Windows： 开始 -> 运行 -> 输入cmd -> 在CMD窗口输入
 * <pre>ipconfig /flushdns</pre>
 */
public class GithubIpUtil {
    // 下载与chrome浏览器版本匹配的 chromedriver
    private static final String osDriverPath = "C:/Users/Justin/Downloads/chromedriver-win64/chromedriver.exe";
    private static final String githubComUrl = "https://site.ip138.com/github.com/";
    private static final String fastlyNetUrl = "https://site.ip138.com/global.ssl.fastly.net/";
    private static final String codeLoadGithubUrl = "https://site.ip138.com/codeload.github.com/";
    private static ChromeDriver driver;
    private static  ChromeOptions options = new ChromeOptions();
    private static void setConfig(){
        System.setProperty("webdriver.chrome.driver",osDriverPath);
        // options.addArguments("--headless"); //无浏览器模式
        options.addArguments("--disable-gpu"); // 谷歌文档提到需要加上这个属性来规避bug
        options.addArguments("--disable-software-rasterizer"); //禁用3D软件光栅化器
        options.addArguments("--no-sandbox");// 为了让linux root用户也能执行
        options.addArguments("--disable-dev-shm-usage"); //解决在某些VM环境中，/dev/shm分区太小，导致Chrome失败或崩溃
        // user-agent="MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"
        options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36" );
        options.addArguments("--whitelisted-ips=\"\"");
        options.addArguments("--remote-allow-origins=*");
        //options.addArguments("--Cookie=s39b0d82c=76h7trvtti6v8rch0av9tfhnlh; Hm_lvt_1927ff7ba46f74209851f2813e32099d=1687443002; Hm_lvt_aa2f9869e9b578122e4692de2bd9f80f=1687443003; __session:0.15131880659974928:=http:; Hm_lpvt_1927ff7ba46f74209851f2813e32099d=1687443064; Hm_lpvt_aa2f9869e9b578122e4692de2bd9f80f=1687443064; rows=20");
        driver = new ChromeDriver(options);//实例化
    }

    public static Map<String,String> getGithubComIps() throws InterruptedException {
        setConfig();
        driver.get(githubComUrl);
        Thread.sleep(2000);
        Map<String,String> result = new HashMap<>();
        Document document = Jsoup.parse(driver.getPageSource());
        Element element = document.getElementById("curadress");
        Elements elements = element.getElementsByTag("p");
        for(Element els :elements){
            String ip =  els.getElementsByTag("a").text();
            result.put(ip,"github.com");
        }
        driver.close();
        return result;
    }


    public static  Map<String,String> getFasterlnetIps() throws InterruptedException {
        setConfig();
        driver.get(fastlyNetUrl);
        Thread.sleep(2000);
        Map<String,String> result = new HashMap<>();
        Document document = Jsoup.parse(driver.getPageSource());
        Element element = document.getElementById("curadress");
        Elements elements = element.getElementsByTag("p");
        for(Element els :elements){
            String ip =  els.getElementsByTag("a").text();
            result.put(ip,"global.ssl.fastly.net");
        }
        driver.close();
        return result;
    }

    public static  Map<String,String> getCodeloadGithubIps() throws InterruptedException {
        setConfig();
        driver.get(codeLoadGithubUrl);
        Thread.sleep(2000);
        Map<String,String> result = new HashMap<>();
        Document document = Jsoup.parse(driver.getPageSource());
        Element element = document.getElementById("curadress");
        Elements elements = element.getElementsByTag("p");
        for(Element els :elements){
            String ip =  els.getElementsByTag("a").text();
            result.put(ip,"codeload.github.com");
        }
        driver.close();
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        Map<String,String> m1 =  GithubIpUtil.getGithubComIps();

        Map<String,String> m2 = GithubIpUtil.getFasterlnetIps();

        Map<String,String> m3 =  GithubIpUtil.getCodeloadGithubIps();

        System.out.println(m1);
        System.out.println(m2);
        System.out.println(m3);
        //写文件
        m1.putAll(m2);
        m1.putAll(m3);
        File file = new File(GithubIpUtil.class.getResource("/").getPath()+"host.txt");
        if(!file.exists()){
            FileUtil.touch(file);
        }
        StringBuffer sb = new StringBuffer();
        for(String key :m1.keySet()){
            sb.append(key).append(" ").append(m1.get(key)).append("\n");
        }
        FileUtil.writeString(sb.toString(),file,"UTF-8");
    }
}