package com.shocknode.cookbook;

import com.shocknode.cookbook.pdf.PDF;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static com.shocknode.utilities.Lambdas.consumeWithException;

public class PDFs {
    
    public static final String FILE_TRUTH_1 = "src\\test\\resources\\sample1.txt";
    public static final String FILE_TRUTH_1b = "src\\test\\resources\\sample2.txt";
    public static final String FILE_PDF_1 = "src\\test\\resources\\sample1.pdf";
    public static final String FILE_PDF_2 = "src\\test\\resources\\sample2.pdf";
    public static final String FILE_PDF_TABLES = "src\\test\\resources\\datatables.pdf";
    
    @Test
    public void lines() throws Exception {
        
        Arrays.asList(FILE_PDF_1, FILE_PDF_2).forEach(consumeWithException(path ->{

            System.out.println("------NEW PDF-------");
            PDF.lines(path).forEach(System.out::println);
            System.out.println();
            
        }));

    }
    @Test
    public void compareTruthToPDF() throws Exception {
        
        Assert.assertTrue("Expected match!", PDF.compare(true, true, FILE_TRUTH_1, FILE_PDF_1));
        Assert.assertTrue("Expected match!", PDF.compare(true, true, FILE_TRUTH_1b, FILE_PDF_1));
        Assert.assertTrue("Expected match!", PDF.compare(false, true, FILE_TRUTH_1b, FILE_PDF_1));
        Assert.assertFalse("Expected mismatch!", PDF.compare(false, false, FILE_TRUTH_1b, FILE_PDF_2));
        
    }

    @Test
    public void compareTruthToPDF2() throws Exception {

        PDF.compareAndConsumeMismatches(true, FILE_TRUTH_1, FILE_PDF_1, (expected, actual)->{
            Assert.fail("Expected match!");
        });

        PDF.compareAndConsumeMismatches(true, FILE_TRUTH_1b, FILE_PDF_1, (expected, actual)->{
            Assert.fail("Expected match!");
        });

        PDF.compareAndConsumeMismatches(false, FILE_TRUTH_1b, FILE_PDF_1, (expected, actual)->{
            Assert.fail("Expected match!");
        });

    }

    @Test
    public void extractTables() throws Exception {
        
        PDF.extractAllTables(FILE_PDF_TABLES, tables -> {
            System.out.println(tables.size());
        });

        PDF.extractAllGuessableTables(FILE_PDF_TABLES, tables -> {
            System.out.println(tables.size());
        });
        
    }


}
