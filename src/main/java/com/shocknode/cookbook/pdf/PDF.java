package com.shocknode.cookbook.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import technology.tabula.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PDF {

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

        Counter counter = new Counter();

        lines(pathToPDF).forEach(line ->{

            consumer.accept(counter.getCount(), line);
            counter.increment();

        });
    }

    public static boolean compare(boolean regex, boolean printToStdOut, String pathToTruthSource, String pathToPDF) throws IOException {
        
        Bool match = new Bool(true);
        List<String> pdfLines = lines(pathToPDF);
        List<String> truthSourceLines = Files.readAllLines(Paths.get(pathToTruthSource));
        
        if(printToStdOut && pdfLines.size() != truthSourceLines.size()){

            System.out.println(String.format("Line count (expected): %d", truthSourceLines.size()));
            System.out.println(String.format("Line count (actual): %d", pdfLines.size()));
            System.out.println();
            
        }
        
        for(int i =0; i < pdfLines.size(); i++){

            if(i < truthSourceLines.size()){

                if((regex && !pdfLines.get(i).matches(truthSourceLines.get(i)))||(!regex && !pdfLines.get(i).equals(truthSourceLines.get(i)))) {
                    match.setFalse();
                    
                    if(printToStdOut) {
                        System.out.println(String.format("Mismatch: %d", i));
                        System.out.println(String.format("Line (expected):\t %s", truthSourceLines.get(i)));
                        System.out.println(String.format("Line (actual):\t\t %s", pdfLines.get(i)));
                        System.out.println(String.format("Line count (expected): %d", truthSourceLines.get(i).length()));
                        System.out.println(String.format("Line count (actual): %d", pdfLines.get(i).length()));
                        System.out.println();
                    }

                }

            }else {
                if(printToStdOut) {
                    System.out.println(String.format("No truth source at line %d!", i));
                    System.out.println();
                }
            }

        }
        
        return match.get();

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

    public static void extractAllTables(String pathToPDF, Consumer<List<Table>> consumer) throws IOException {
        consumer.accept(new PDFTableExtractor(PDFTableExtractor.ExtractionMethod.DECIDE, false).extractTables(pathToPDF));
    }
    
    public static void extractAllGuessableTables(String pathToPDF, Consumer<List<Table>> consumer) throws IOException {
        consumer.accept(new PDFTableExtractor(PDFTableExtractor.ExtractionMethod.DECIDE, true).extractTables(pathToPDF));
    }


    public static class Bool {
    
        private boolean bool;
    
        public Bool(){
            this(false);
        }
    
        public Bool(boolean bool){
            this.bool = bool;
        }
    
        public synchronized void setFalse() {
            bool = false;
        }
    
        public synchronized void setTrue() {
            bool = true;
        }
    
        public boolean get() {
            return bool;
        }
    }

    public static class Counter {
    
        private int count;
    
        public Counter(){
            this(0);
        }
    
        public Counter(int count){
            this.count = count;
        }
    
        public synchronized void increment() {
            count++;
        }
    
        public synchronized void decrement() {
            count--;
        }
    
        public int getCount() {
            return count;
        }
    }
    
}
