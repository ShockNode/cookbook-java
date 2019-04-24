package com.shocknode.cookbook.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PDFs {


    public static List<String> boldText(String pathToPDF) throws IOException {

        File file = new File(pathToPDF);
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFBoldTextStripper();

        pdfStripper.setStartPage(1);
        pdfStripper.setEndPage(document.getNumberOfPages());

        //Load all lines into a string
        String pages = pdfStripper.getText(document)
                .replaceAll("\\r\\n|\\r|\\n", " ")
                .replaceAll("\\s+", " ");

        //Split by detecting newline
        return new ArrayList<>(Arrays.asList(pages.split(" ")));

    }


    public static List<String> lines(String pathToPDF) throws IOException {

        File file = new File(pathToPDF);
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();

        pdfStripper.setStartPage(1);
        pdfStripper.setEndPage(document.getNumberOfPages());

        //Load all lines into a string
        String pages = pdfStripper.getText(document);

        //Split by detecting newline
        return new ArrayList<>(Arrays.asList(pages.split("\r\n|\r|\n")));

    }

    public static void forEachLine(String pathToPDF, Consumer<String> consumer) throws IOException {
        lines(pathToPDF).forEach(consumer);
    }

    public static void forEachLineIndexed(String pathToPDF, BiConsumer<Integer, String> consumer) throws IOException {

        AtomicInteger counter = new AtomicInteger(0);

        lines(pathToPDF).forEach(line ->{

            consumer.accept(counter.get(), line);
            counter.incrementAndGet();

        });
    }

    public static boolean compare(boolean regex, boolean printToStdOut, String pathToTruthSource, String pathToPDF) throws IOException {
        
        List<String> pdfLines = lines(pathToPDF);
        List<String> truthSourceLines = Files.readAllLines(Paths.get(pathToTruthSource));
        
        if(pdfLines.size() != truthSourceLines.size()){

            if(printToStdOut) {
                System.out.println(String.format("Line count (expected): %d", truthSourceLines.size()));
                System.out.println(String.format("Line count (actual): %d", pdfLines.size()));
                System.out.println();
            }
            
            return false;
            
        }
        
        for(int i =0; i < pdfLines.size(); i++){
            
                if((regex && !pdfLines.get(i).matches(truthSourceLines.get(i)))||(!regex && !pdfLines.get(i).equals(truthSourceLines.get(i)))) {
                    
                    if(printToStdOut) {
                        System.out.println(String.format("Mismatch: %d", i));
                        System.out.println(String.format("Line (expected):\t %s", truthSourceLines.get(i)));
                        System.out.println(String.format("Line (actual):\t\t %s", pdfLines.get(i)));
                        System.out.println(String.format("Line count (expected): %d", truthSourceLines.get(i).length()));
                        System.out.println(String.format("Line count (actual): %d", pdfLines.get(i).length()));
                        System.out.println();
                    }
                    
                    return false;

                }

        }
        
        return true;

    }

    public static void compareAndConsumeMismatches(boolean regex, String pathToTruthSource, String pathToPDF, BiConsumer<String, String> consumer) throws IOException {

        List<String> pdfLines = lines(pathToPDF);
        List<String> truthSourceLines = Files.readAllLines(Paths.get(pathToTruthSource));
        
        for(int i =0; i < pdfLines.size(); i++){

            if(i < truthSourceLines.size()){
                
                if((regex && !pdfLines.get(i).matches(truthSourceLines.get(i)))||(!regex && !pdfLines.get(i).equals(truthSourceLines.get(i)))) {
                    consumer.accept(truthSourceLines.get(i), pdfLines.get(i));
                }

            }else { consumer.accept(null, pdfLines.get(i)); }

        }

    }

    public static void consumeForComparison(String pathToTruthSource, String pathToPDF, BiConsumer<String, String> consumer) throws IOException {

        List<String> pdfLines = lines(pathToPDF);
        List<String> truthSourceLines = Files.readAllLines(Paths.get(pathToTruthSource));

        for(int i =0; i < pdfLines.size(); i++){

            if(i < truthSourceLines.size()){ consumer.accept(truthSourceLines.get(i), pdfLines.get(i)); }
            else { consumer.accept(null, pdfLines.get(i)); }

        }

    }

    public static void compareAndConsumeBoldTextMismatches(String pathToPDF1, String pathToPDF2, BiConsumer<String, String> consumer) throws IOException {

        List<String> pdf1Lines = boldText(pathToPDF1);
        List<String> pdf2Lines = boldText(pathToPDF2);

        for(int i =0; i < pdf2Lines.size(); i++){

            if(i < pdf1Lines.size()){

                if(!pdf2Lines.get(i).equals(pdf1Lines.get(i))) {
                    consumer.accept(pdf1Lines.get(i), pdf2Lines.get(i));
                }

            }else { consumer.accept(null, pdf2Lines.get(i)); }

        }

    }
    
    public static void consumeForBoldTextComparison(String pathToPDF1, String pathToPDF2, BiConsumer<String, String> consumer) throws IOException {

        List<String> pdf1Lines = boldText(pathToPDF1);
        List<String> pdf2Lines = boldText(pathToPDF2);

        for(int i =0; i < pdf2Lines.size(); i++){

            if(i < pdf1Lines.size()){ consumer.accept(pdf1Lines.get(i), pdf2Lines.get(i)); }
            else { consumer.accept(null, pdf2Lines.get(i)); }

        }

    }
    
    public static boolean compareBoldText(boolean printToStdOut, String pathToPDF1, String pathToPDF2) throws IOException {

        List<String> pdf1Lines = boldText(pathToPDF1);
        List<String> pdf2Lines = boldText(pathToPDF2);
        if(pdf1Lines.size() != pdf2Lines.size()){

            if(printToStdOut) {
                System.out.println(String.format("Line count (PDFs 1): %d", pdf1Lines.size()));
                System.out.println(String.format("Line count (PDFs 2): %d", pdf2Lines.size()));
                System.out.println();
            }
            
            return false;

        }

        for(int i =0; i < pdf2Lines.size(); i++){
            
                if(!pdf2Lines.get(i).equals(pdf1Lines.get(i))) {

                    if(printToStdOut) {
                        System.out.println(String.format("Mismatch: %d", i));
                        System.out.println(String.format("Line (PDFs 1):\t %s", pdf1Lines.get(i)));
                        System.out.println(String.format("Line (PDFs 2):\t\t %s", pdf2Lines.get(i)));
                        System.out.println(String.format("Line count (PDFs 1): %d", pdf1Lines.get(i).length()));
                        System.out.println(String.format("Line count (PDFs 2): %d", pdf2Lines.get(i).length()));
                        System.out.println();
                    }
                    
                    return false;

                }

        }

        return true;

    }

    public static boolean compareTables(boolean printToStdOut, String pathToPDF1, String pathToPDF2) throws IOException {

        List<Table> pdf1Tables = new PDFTableExtractor(PDFTableExtractor.ExtractionMethod.DECIDE, true).extractTables(pathToPDF1);
        List<Table> pdf2Tables = new PDFTableExtractor(PDFTableExtractor.ExtractionMethod.DECIDE, true).extractTables(pathToPDF2);

        if(pdf1Tables.size() != pdf2Tables.size()){

            if(printToStdOut) {
                System.out.println(String.format("Table count (PDFs 1): %d", pdf1Tables.size()));
                System.out.println(String.format("Table count (PDFs 2): %d", pdf2Tables.size()));
                System.out.println();
            }

            return false;
            
        }

        for(int i =0; i < pdf2Tables.size(); i++){

            if(pdf1Tables.size() != pdf2Tables.size()){
                
                if(printToStdOut) {
                    System.out.println(String.format("Column count (PDFs 1): %d", pdf1Tables.get(i).getColCount()));
                    System.out.println(String.format("Column count (PDFs 2): %d", pdf2Tables.get(i).getColCount()));
                    System.out.println(String.format("Row count (PDFs 1): %d", pdf1Tables.get(i).getRowCount()));
                    System.out.println(String.format("Row count (PDFs 2): %d", pdf2Tables.get(i).getRowCount()));
                    System.out.println();
                }

                return false;

            }

            List<List<RectangularTextContainer>> pdf1Rows = pdf1Tables.get(i).getRows();
            List<List<RectangularTextContainer>> pdf2Rows = pdf1Tables.get(i).getRows();
            
            for(int j =0; j < pdf2Rows.size(); j++){
                
                List<RectangularTextContainer> pdf1Columns = pdf1Rows.get(j);
                List<RectangularTextContainer> pdf2Columns = pdf2Rows.get(j);

                for(int k =0; k < pdf2Columns.size(); k++){
                    
                    RectangularTextContainer pdf1Text = pdf1Columns.get(k);
                    RectangularTextContainer pdf2Text = pdf2Columns.get(k);
                    if(!pdf1Text.getText().equals(pdf2Text.getText())) {
                        if (printToStdOut) {
                            System.out.println(String.format("Text (PDFs 1): %s", pdf1Text.getText()));
                            System.out.println(String.format("Text (PDFs 2): %s", pdf2Text.getText()));
                            System.out.println();
                        }
                        return false;
                    }
                }
                
                
            }

        }

        return true;

    }

    
    public static void extractAllTables(String pathToPDF, Consumer<List<Table>> consumer) throws IOException {
        consumer.accept(new PDFTableExtractor(PDFTableExtractor.ExtractionMethod.DECIDE, false).extractTables(pathToPDF));
    }
    
    public static void extractAllGuessableTables(String pathToPDF, Consumer<List<Table>> consumer) throws IOException {
        consumer.accept(new PDFTableExtractor(PDFTableExtractor.ExtractionMethod.DECIDE, true).extractTables(pathToPDF));
    }
    
}
