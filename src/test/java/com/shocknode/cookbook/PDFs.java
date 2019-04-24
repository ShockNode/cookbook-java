package com.shocknode.cookbook;

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

            System.out.println("------NEW PDFs-------");
            com.shocknode.cookbook.pdf.PDFs.lines(path).forEach(System.out::println);
            System.out.println();
            
        }));

    }
    @Test
    public void compareTruthToPDF() throws Exception {
        
        Assert.assertTrue("Expected match!", com.shocknode.cookbook.pdf.PDFs.compare(true, true, FILE_TRUTH_1, FILE_PDF_1));
        Assert.assertTrue("Expected match!", com.shocknode.cookbook.pdf.PDFs.compare(true, true, FILE_TRUTH_1b, FILE_PDF_1));
        Assert.assertTrue("Expected match!", com.shocknode.cookbook.pdf.PDFs.compare(false, true, FILE_TRUTH_1b, FILE_PDF_1));
        Assert.assertFalse("Expected mismatch!", com.shocknode.cookbook.pdf.PDFs.compare(false, false, FILE_TRUTH_1b, FILE_PDF_2));
        
    }

    @Test
    public void compareTruthToPDF2() throws Exception {

        com.shocknode.cookbook.pdf.PDFs.compareAndConsumeMismatches(true, FILE_TRUTH_1, FILE_PDF_1, (expected, actual)->{
            Assert.fail("Expected match!");
        });

        com.shocknode.cookbook.pdf.PDFs.compareAndConsumeMismatches(true, FILE_TRUTH_1b, FILE_PDF_1, (expected, actual)->{
            Assert.fail("Expected match!");
        });

        com.shocknode.cookbook.pdf.PDFs.compareAndConsumeMismatches(false, FILE_TRUTH_1b, FILE_PDF_1, (expected, actual)->{
            Assert.fail("Expected match!");
        });

    }

    @Test
    public void extractTables() throws Exception {
        
        com.shocknode.cookbook.pdf.PDFs.extractAllTables(FILE_PDF_TABLES, tables -> {
            System.out.println(tables.size());
        });

        com.shocknode.cookbook.pdf.PDFs.extractAllGuessableTables(FILE_PDF_TABLES, tables -> {
            System.out.println(tables.size());
        });
        
    }


}
