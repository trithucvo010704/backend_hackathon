package vn.ezisolutions.cloud.hackathon.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FileUtils {

    /**
     * Extracts text content from a MultipartFile.
     * Supports .txt, .md, and .docx formats.
     *
     * @param file the file to extract content from.
     * @return the extracted text content.
     */
    public static String extractContent(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) return "";

        try {
            if (fileName.endsWith(".txt") || fileName.endsWith(".md")) {
                return new String(file.getBytes(), StandardCharsets.UTF_8);
            } else if (fileName.endsWith(".docx")) {
                try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
                    XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                    return extractor.getText();
                }
            } else {
                return "Unsupported file format for content extraction: " + fileName;
            }
        } catch (IOException e) {
            log.error("Error extracting content from file: {}", fileName, e);
            return "Error extracting content: " + e.getMessage();
        }
    }
}
