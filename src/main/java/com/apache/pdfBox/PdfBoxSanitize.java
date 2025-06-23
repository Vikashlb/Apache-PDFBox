package com.apache.pdfBox;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import java.io.File;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PdfBoxSanitize {
    public static void main(String[] args) {
        try {
            File file = new File("Lorem Ipsum.pdf");
            PDDocument pdf = Loader.loadPDF(file);

            PDDocumentInformation info = pdf.getDocumentInformation();
            //Set Metadata to Null
            info.setAuthor("");
            info.setTitle("");
            info.setSubject("");
            info.setKeywords("");
            info.setCreator("");
            info.setProducer("");
            info.setCreationDate(Calendar.getInstance());
            info.setModificationDate(Calendar.getInstance());

            //Remove custom properties
            Set<COSName> customProps = new HashSet<>();
            COSDictionary dict_1 = info.getCOSObject();
            for(COSName key : dict_1.keySet()) {
                String keyName = key.getName();
                if(!isStandardKey(keyName)) {
                    customProps.add(key);
                }
            }

            for(COSName key : customProps) {
                info.getCOSObject().removeItem(key);
            }

            //Remove all authors while retaining comments!
            for(PDPage page : pdf.getPages()) {
                List<PDAnnotation> annotations = page.getAnnotations();
                for(PDAnnotation annotation : annotations) {
                    COSDictionary dict = annotation.getCOSObject();
                    dict.setString(COSName.T, "Author");
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
