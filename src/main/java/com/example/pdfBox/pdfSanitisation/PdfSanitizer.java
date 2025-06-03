package com.example.pdfBox.pdfSanitisation;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import java.io.File;
import java.util.List;

public class PdfSanitizer {
    public static void main(String[] args) {
        try {
            File file = new File("devon-logo-blue.pdf");
            PDDocument pdf = Loader.loadPDF(file);

            PDDocumentInformation info = pdf.getDocumentInformation();
            //Set Metadata to Null
            info.setAuthor(null);
            info.setTitle(null);
            info.setSubject(null);
            info.setKeywords(null);
            info.setCreator(null);
            info.setProducer(null);
            info.setCreationDate(null);
            info.setModificationDate(null);

            //Remove custom properties
            COSDictionary dict_1 = info.getCOSObject();
            for(COSName key : dict_1.keySet()) {
                String keyName = key.getName();
                if(!isStandardKey(keyName)) {
                    dict_1.removeItem(key);
                }
            }

            //Remove all annotations
//            pdf.getPages().forEach(page -> {
//                try {
//                    List<PDAnnotation> annotations = page.getAnnotations();
//                    annotations.clear();
//                }
//                catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//            });

            //Remove all authors while retaining comments!
            for(PDPage page : pdf.getPages()) {
                List<PDAnnotation> annotations = page.getAnnotations();
                for(PDAnnotation annotation : annotations) {
                    COSDictionary dict = annotation.getCOSObject();
                    dict.setString(COSName.T, null);
                    dict.removeItem(COSName.MOD_DATE);
                }
            }

            pdf.save("sanitized.pdf");
            pdf.close();
            System.out.println("PDF sanitization successful");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean isStandardKey(String key) {
        return key.equals("Title") ||
                key.equals("Author") ||
                key.equals("Subject") ||
                key.equals("Keywords") ||
                key.equals("Creator") ||
                key.equals("Producer") ||
                key.equals("CreationDate") ||
                key.equals("ModDate") ||
                key.equals("Trapped");
    }
}
