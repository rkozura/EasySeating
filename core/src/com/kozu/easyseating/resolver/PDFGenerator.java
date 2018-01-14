package com.kozu.easyseating.resolver;

import com.kozu.easyseating.object.Conference;

/**
 * Created by Rob on 1/13/2018.
 */

public interface PDFGenerator {
    void generatePDF(Conference conference);
}
