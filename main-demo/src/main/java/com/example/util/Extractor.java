package com.example.util;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Extractor {
    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 2) {
            System.out.println("arguments length is not 2");
            return;
        }

        String src = args[0];
        String dest = args[1];

        Document root = Jsoup.parse(new File(src), "UTF-8");
        Element storeList = root.getElementById("store-list");

        Elements trElements = storeList.getElementsByAttribute("data-id");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        int i = 0;
        for (Element tr : trElements) {
            Element titleEle = tr.selectFirst("td>h4");
            Element addrEle = tr.selectFirst("td>div>p");
            Element contactEle = tr.selectFirst("td>div>p>a");

            String title = titleEle.text();
            String addr = addrEle == null ? "" : addrEle.text();
            String contact = contactEle == null ? "" : contactEle.text();

            XSSFRow row = sheet.createRow(i++);
            row.createCell(0).setCellValue(title);
            row.createCell(1).setCellValue(addr);
            row.createCell(2).setCellValue(contact);
        }

        try (OutputStream outputStream = new FileOutputStream(dest)){
            workbook.write(outputStream);
            outputStream.flush();
        }

    }
}
