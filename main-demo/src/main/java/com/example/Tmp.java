package com.example;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import jodd.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 临时测试类：随时可能删除
 */
public class Tmp {
    public static void main(String[] args) throws InterruptedException, IOException {
//        validateSAT();
//        validateGRE();
//        validateTOEFL();
//        count();
//        count2();
//        validateMisc();
//        extract15000();
//        extractWords();
//        extractGre(17);
//        merge();

        diff();
    }

    static void diff() throws IOException {
//        List<String> sat = FileUtils.readLines(new File("/home/justin/ws2/Downloads/sat/result/sat.txt"), StandardCharsets.UTF_8);
//        List<String> toefl = FileUtils.readLines(new File("/home/justin/ws2/Downloads/toefl/toefl.txt"), StandardCharsets.UTF_8);
//        List<String> gre = FileUtils.readLines(new File("/home/justin/ws2/Downloads/gre/gre.txt"), StandardCharsets.UTF_8);
        List<String> middle = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/middle.txt"), StandardCharsets.UTF_8);
//        List<String> high = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/high.txt"), StandardCharsets.UTF_8);
//        List<String> mylist = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/mylist.txt"), StandardCharsets.UTF_8);
//        List<String> pet = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/pet.txt"), StandardCharsets.UTF_8);
        List<String> ket = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/ket.txt"), StandardCharsets.UTF_8);
//        List<String> cet4 = FileUtils.readLines(new File("/home/justin/ws2/Downloads/cet4/result/cet.txt"), StandardCharsets.UTF_8);
//        List<String> cet6 = FileUtils.readLines(new File("/home/justin/ws2/Downloads/cet6/result/cet.txt"), StandardCharsets.UTF_8);

        List<String> list = CollectionUtil.subtractToList(middle, ket);
        System.out.println(list);
    }

    static void extract15000() throws IOException, InterruptedException {
        List<String> words = new ArrayList<>();
        for (int i = 1; i <= 42 ; i++) {
            HttpRequest request = HttpUtil.createGet(String.format("https://www.koolearn.com/dict/tag_2673_%s.html", String.valueOf(i)));
            request.header("User-Agent", randomUserAgent());
            String html = request.execute().body();
            Document doc = Jsoup.parse(html);
            Elements wordsEles = doc.select("div.word-wrap>div.word-box>a.word");
            if (wordsEles.size() > 0) {
                System.out.println("processing: " + i);
            }
            for (Element ele : wordsEles) {
                words.add(StringUtils.trim(ele.text()));
            }
            Thread.sleep(1000);
        }
        FileUtils.writeLines(new File("/home/justin/ws2/Downloads/misc/nbc15000.txt"), words);
    }
    /**
     * 常用 user agent 列表
     */
    static List<String> USER_AGENT = new ArrayList<String>(10) {
        {
            add("Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");
            add("Mozilla/5.0 (Linux; U; Android 4.0.4; en-gb; GT-I9300 Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
            add("Mozilla/5.0 (Linux; U; Android 2.2; en-gb; GT-P1000 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
            add("Mozilla/5.0 (Android; Mobile; rv:14.0) Gecko/14.0 Firefox/14.0");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
            add("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
            add("Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
            add("Mozilla/5.0 (iPod; U; CPU like Mac OS X; en) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/3A101a Safari/419.3");
        }
    };

    /**
     * 随机获取 user agent
     *
     * @return
     */
    static String randomUserAgent() {
        Random random = new Random();
        int num = random.nextInt(USER_AGENT.size());
        return USER_AGENT.get(num);
    }

    static void count2() throws IOException {
        List<String> sat = FileUtils.readLines(new File("/home/justin/ws2/Downloads/sat/result/sat.txt"), StandardCharsets.UTF_8);
        List<String> toefl = FileUtils.readLines(new File("/home/justin/ws2/Downloads/toefl/toefl.txt"), StandardCharsets.UTF_8);
        List<String> gre = FileUtils.readLines(new File("/home/justin/ws2/Downloads/gre/gre.txt"), StandardCharsets.UTF_8);
        List<String> middle = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/middle.txt"), StandardCharsets.UTF_8);
        List<String> high = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/high.txt"), StandardCharsets.UTF_8);


        Set<String> allWords = new HashSet<>();
        allWords.addAll(sat);
        allWords.addAll(toefl);
        allWords.addAll(middle);
        allWords.addAll(high);

        List<String> diffWords = new ArrayList<>();
        for (String word : gre) {
            if (!allWords.contains(word)) {
                diffWords.add(word);
            }
        }

        FileUtils.writeLines(new File("/home/justin/ws2/Downloads/misc/diff-gre.txt"), diffWords);
    }

    static void count() throws IOException {
        List<String> sat = FileUtils.readLines(new File("/home/justin/ws2/Downloads/sat/result/sat.txt"), StandardCharsets.UTF_8);
        List<String> toefl = FileUtils.readLines(new File("/home/justin/ws2/Downloads/toefl/toefl.txt"), StandardCharsets.UTF_8);
        List<String> gre = FileUtils.readLines(new File("/home/justin/ws2/Downloads/gre/gre.txt"), StandardCharsets.UTF_8);
        List<String> middle = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/middle.txt"), StandardCharsets.UTF_8);
        List<String> high = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/high.txt"), StandardCharsets.UTF_8);
        List<String> mylist = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/mylist.txt"), StandardCharsets.UTF_8);
        List<String> pet = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/pet.txt"), StandardCharsets.UTF_8);
        List<String> ket = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/ket.txt"), StandardCharsets.UTF_8);
        List<String> cet4 = FileUtils.readLines(new File("/home/justin/ws2/Downloads/cet4/result/cet.txt"), StandardCharsets.UTF_8);
        List<String> cet6 = FileUtils.readLines(new File("/home/justin/ws2/Downloads/cet6/result/cet.txt"), StandardCharsets.UTF_8);

        List<String> nbc = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/nbc15000-diff.txt"), StandardCharsets.UTF_8);
        Set<String> nbcs = new HashSet<>();
        nbcs.addAll(nbc);
        System.out.println(nbcs.size());

        Set<String> allWords = new HashSet<>();
        allWords.addAll(sat);
        allWords.addAll(toefl);
        allWords.addAll(gre);
        allWords.addAll(middle);
        allWords.addAll(high);
        allWords.addAll(mylist);
        allWords.addAll(pet);
        allWords.addAll(ket);
        allWords.addAll(cet4);
        allWords.addAll(cet6);
        allWords.addAll(nbc);

//        List<String> list = CollectionUtil.subtractToList(nbc, allWords);

        System.out.println(allWords.size());
        //FileUtils.writeLines(new File("/home/justin/ws2/Downloads/misc/nbc15000-diff.txt"), list);
    }

    static void validateMisc() throws IOException {
        // validate GRE/SAT
        List<String> words = FileUtils.readLines(new File("/home/justin/ws2/Downloads/cet4/result/cet.txt"), StandardCharsets.UTF_8);
        int i = 0;
        for (String word : words) {
            if ((++i) % 200 == 0) {
                System.out.println("progress: " + i + "/" + words.size());
            }
            if (!validateWord(word)) {
                System.out.println("<<< " + word + " >>>");
            }
        }
    }


    static void validateSAT() throws IOException {
        // validate GRE/SAT
        List<String> words = FileUtils.readLines(new File("/home/justin/ws2/Downloads/sat/result/sat.txt"), StandardCharsets.UTF_8);
        int i = 0;
        for (String word : words) {
            if ((++i) % 200 == 0) {
                System.out.println("progress: " + i + "/" + words.size());
            }
            if (!validateWord(word, "SAT")) {
                System.out.println("<<< " + word + " >>>");
            }
        }
    }

    static void validateTOEFL() throws IOException {
        // validate GRE/SAT
        List<String> words = FileUtils.readLines(new File("/home/justin/ws2/Downloads/toefl/toefl.txt"), StandardCharsets.UTF_8);
        int i = 0;
        for (String word : words) {
            if ((++i) % 200 == 0) {
                System.out.println("progress: " + i + "/" + words.size());
            }
            if (!validateWord(word, "TOEFL")) {
                System.out.println("<<< " + word + " >>>");
            }
        }
    }

    static void validateGRE() throws IOException {
        // validate GRE/SAT
        List<String> words = FileUtils.readLines(new File("/home/justin/ws2/Downloads/gre/gre.txt"), StandardCharsets.UTF_8);
        int i = 0;
        for (String word : words) {
            if ((++i) % 300 == 0) {
                System.out.println("progress: " + i + "/" + words.size());
            }
            if (!validateWord(word, "GRE")) {
                System.out.println("<<< " + word + " >>>");
            }
        }
    }

    static boolean validateWord(String word) {
        String html = HttpUtil.get(String.format("https://dict.youdao.com/result?word=%s&lang=en", word));
        Document doc = Jsoup.parse(html);
        Element typeEle = doc.selectFirst("div.catalogue_author");
        return typeEle != null;
    }

    static boolean validateWord(String word, String examType) {
        String html = HttpUtil.get(String.format("https://dict.youdao.com/result?word=%s&lang=en", word));
        Document doc = Jsoup.parse(html);
        Element typeEle = doc.selectFirst("div.exam_type");
        return typeEle != null && typeEle.text().contains(examType);
    }

    static void merge() throws IOException {
        File[] files = new File("/home/justin/ws2/Downloads/cet6/result").listFiles();
        Arrays.sort(files, (f1, f2) -> {
            int f1No = Integer.valueOf(f1.getName().substring(3, f1.getName().indexOf('.')));
            int f2No = Integer.valueOf(f2.getName().substring(3, f2.getName().indexOf('.')));
            return f1No - f2No;
        });

        List<String> words = new ArrayList<>();
        for (File file : files) {
            words.addAll(FileUtils.readLines(file, StandardCharsets.UTF_8));
        }
        FileUtils.writeLines(new File("/home/justin/ws2/Downloads/cet6/result/cet.txt"), words);
    }

    static void extractGre(int fileNo) throws IOException {
        List<String> list = FileUtils.readLines(new File("/home/justin/ws2/Downloads/cet6/cet" + fileNo + ".txt"), StandardCharsets.UTF_8);
        List<String> words = new LinkedList<>();
        for (String line : list) {
            if (StringUtil.isBlank(line)) {
                continue;
            }
            line = StringUtils.strip(line);
            line = StringUtils.stripAccents(line);
            boolean isWord = true;
            for (char c : line.toCharArray()) {
                if (!CharUtil.isLetter(c) && !CharUtil.isBlankChar(c) && c != '-') {
                    isWord = false;
                    break;
                }
            }
            if (isWord) {
                words.add(line);
            }
        }

        FileUtils.writeLines(new File("/home/justin/ws2/Downloads/cet6/result/cet" + fileNo + ".txt"), words);
    }

    static void compareWords() throws IOException {
        List<String> list1 = FileUtils.readLines(new File("/home/justin/ws2/Downloads/wxyz.txt.rrr"), StandardCharsets.UTF_8);
        List<String> list2 = FileUtils.readLines(new File("/home/justin/ws2/Downloads/wxyz.txt.rr"), StandardCharsets.UTF_8);

        List<String> result = CollectionUtil.subtractToList(list1, list2);
        System.out.println(result);
    }

    static void extractWords() throws IOException {
        String fileName = "ket-stuvwxyz.txt";
        List<String> lines = FileUtils.readLines(new File("/home/justin/ws2/Downloads/misc/" + fileName), StandardCharsets.UTF_8);
        Pattern p = Pattern.compile("^[a-zA-Z].+\\s?\\(");
        Set<String> words = new HashSet<>();
        for (String line : lines) {
            Matcher matcher = p.matcher(line);

            if (matcher.find()) {
                words.add(line.substring(0, line.indexOf('(')).trim());
            }
        }
        List<String> sorted = new ArrayList<>(words);
        Collections.sort(sorted, (a, b) -> a.compareToIgnoreCase(b));
        FileUtils.writeLines(new File("/home/justin/ws2/Downloads/misc/" + fileName + ".rr"), sorted);
    }
}

