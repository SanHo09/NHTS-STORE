package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.viewmodel.product.ProductInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service xử lý tìm kiếm thông tin sản phẩm dựa theo mã barcode.
 */
@Service
public class FindProductInformationService {

    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";

    /**
     * Lấy thông tin sản phẩm từ Google theo mã barcode.
     * @param barcode mã barcode cần tìm
     * @return ProductInfo chứa tiêu đề, mô tả và URL hoặc Base64 của ảnh
     * @throws IOException khi không kết nối được đến Google
     */
    public ProductInfo getProductInfo(String barcode) throws IOException {
        // Ghép URL tìm kiếm
        String url = GOOGLE_SEARCH_URL + barcode;
        // Kết nối và tải về trang kết quả
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36")
                .timeout(5000)

                .get();
        System.out.println("is connect: " + doc.title());
        ProductInfo info = new ProductInfo();

        // Lấy tiêu đề từ thẻ có class mới LC20lb MBeuO DKV0Md
        Element titleEl = doc.selectXpath("//h3[@class='LC20lb MBeuO DKV0Md']").first();
        if (titleEl != null) {
            info.setTitle(titleEl.text());
        }

        // Lấy mô tả (snippet)
        Element descEl = doc.selectXpath("//div[@class='BNeawe s3v9rd AP7Wnd']").first();
        if (descEl != null) {
            info.setDescription(descEl.text());
        }

        // Lấy ảnh: tìm trong div có class gdOPf uhHOwf ez24Df rồi lấy thẻ <img>
        Element imgEl = doc.selectXpath("//div[@class='gdOPf uhHOwf ez24Df']//img").first();
        if (imgEl != null) {
            String src = imgEl.attr("src");
            // Nếu Google trả về base64 data URI
            if (src.startsWith("data:image")) {
                info.setImageBase64(src);
            } else {
                info.setImageUrl(imgEl.absUrl("src"));
            }
        }

        return info;
    }
}
