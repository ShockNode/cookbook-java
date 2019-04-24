package com.shocknode.cookbook.pdf;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;

public class PDFBoldTextStripper extends PDFTextStripper {
    /**
     * Instantiate a new PDFTextStripper object.
     *
     * @throws IOException If there is an error loading the properties.
     */
    public PDFBoldTextStripper() throws IOException {
        super();
    }

    
    /*
    *
    * Stack overflow: https://stackoverflow.com/a/19777953/9571317
    * 
    * (Optional; PDFs 1.5; should be used for Type 3 fonts in Tagged PDFs documents)
    * The weight (thickness) component of the fully-qualified font name or font specifier.
    * The possible values shall be 100, 200, 300, 400, 500, 600, 700, 800, or 900,
    * where each number indicates a weight that is at least as dark as its predecessor.
    * A value of 400 shall indicate a normal weight; 700 shall indicate bold.
    * 
    */
    @Override
    protected void processTextPosition(TextPosition text){
        if(text.getFont().getFontDescriptor().getFontWeight() >= 700){
            super.processTextPosition(text);
        }
    }
    
}
