package com.shocknode.cookbook.pdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.*;
import technology.tabula.detectors.DetectionAlgorithm;
import technology.tabula.detectors.NurminenDetectionAlgorithm;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class PDFTableExtractor {
    
    private TableExtractor tableExtractor;

    public PDFTableExtractor(ExtractionMethod method, boolean guess) {
        this.tableExtractor = createExtractor(method, guess);
    }
    
    
    private List<Table> extract(PDDocument document) throws IOException {

        PageIterator pageIterator = getPageIterator(document);
        List<Table> tables = new ArrayList<>();
        
        
        
        while (pageIterator.hasNext()) {
            Page page = pageIterator.next();
            tables.addAll(tableExtractor.extractTables(page));
            
        }
        
        document.close();
        
        return tables;
        
    }

    public List<Table> extractTables(File file, String password) throws IOException {
        return extract(PDDocument.load(file, password));
    }

    public List<Table> extractTables(File file) throws IOException {
        return extract(PDDocument.load(file));
    }

    public List<Table> extractTables(Path path) throws IOException {
        return extractTables(path.toFile());
    }

    public List<Table> extractTables(Path path, String password) throws IOException {
        return extractTables(path.toFile(), password);
    }

    public List<Table> extractTables(String path) throws IOException {
        return extractTables(new File(path));
    }

    public List<Table> extractTables(String path, String password) throws IOException {
        return extractTables(new File(path), password);
    }
    
    private PageIterator getPageIterator(PDDocument pdfDocument) {
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
        return extractor.extract();
    }
    
    private TableExtractor createExtractor(ExtractionMethod method, boolean guess) {
        TableExtractor extractor = new TableExtractor();
        extractor.setGuess(guess);
        extractor.setMethod(method);
        return extractor;
    }
    
    private static class TableExtractor {
        private boolean guess = false;
        private BasicExtractionAlgorithm basicExtractor = new BasicExtractionAlgorithm();
        private SpreadsheetExtractionAlgorithm spreadsheetExtractor = new SpreadsheetExtractionAlgorithm();
        private ExtractionMethod method = ExtractionMethod.BASIC;

        public TableExtractor() {
        }

        public void setGuess(boolean guess) {
            this.guess = guess;
        }

        public void setMethod(ExtractionMethod method) {
            this.method = method;
        }

        public List<Table> extractTables(Page page) {
            ExtractionMethod effectiveMethod = this.method;
            if (effectiveMethod == ExtractionMethod.DECIDE) {
                effectiveMethod = spreadsheetExtractor.isTabular(page) ?
                        ExtractionMethod.SPREADSHEET :
                        ExtractionMethod.BASIC;
            }
            switch (effectiveMethod) {
                case BASIC:
                    return extractTablesBasic(page);
                case SPREADSHEET:
                    return extractTablesSpreadsheet(page);
                default:
                    return new ArrayList<>();
            }
        }

        public List<Table> extractTablesBasic(Page page) {
            if (guess) {
                // guess the page areas to extract using a detection algorithm
                // currently we only have a detector that uses spreadsheets to find table areas
                DetectionAlgorithm detector = new NurminenDetectionAlgorithm();
                List<Rectangle> guesses = detector.detect(page);
                List<Table> tables = new ArrayList<>();

                for (Rectangle guessRect : guesses) {
                    Page guess = page.getArea(guessRect);
                    tables.addAll(basicExtractor.extract(guess));
                }
                return tables;
            }

            return basicExtractor.extract(page);
            
        }

        public List<Table> extractTablesSpreadsheet(Page page) {
            return spreadsheetExtractor.extract(page);
        }
    }

    public enum ExtractionMethod {
        BASIC,
        SPREADSHEET,
        DECIDE
    }
    
}
