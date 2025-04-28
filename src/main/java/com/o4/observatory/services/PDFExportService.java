package com.o4.observatory.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.o4.observatory.models.Collection;
import com.o4.observatory.models.Observation;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PDFExportService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font CONTENT_TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static final Font CONTENT_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font META_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.DARK_GRAY);
    
    /**
     * Generate PDF document for a collection
     */
    public ByteArrayInputStream generateCollectionPDF(Collection collection) throws DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add collection metadata
            addCollectionMetadata(document, collection);
            
            // Add title and description
            addCollectionHeader(document, collection);

            // Add list of observations
            if (collection.getObservations() != null && !collection.getObservations().isEmpty()) {
                addObservationsTable(document, collection);
            } else {
                document.add(new Paragraph("This collection does not contain any observations yet.", CONTENT_FONT));
            }

            document.close();
        } catch (Exception e) {
            document.close();
            throw new DocumentException("Error generating PDF: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addCollectionMetadata(Document document, Collection collection) {
        document.addTitle("Collection: " + collection.getName());
        document.addCreator("Astronomy Observations Application");
        document.addSubject("Collection of Astronomical Observations");
        
        document.addCreationDate();
        document.addKeywords("astronomy, observations, collection, " + collection.getName());
    }

    private void addCollectionHeader(Document document, Collection collection) throws DocumentException {
        // Add collection title
        Paragraph title = new Paragraph(collection.getName(), TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        // Add owner information
        Paragraph owner = new Paragraph("Curated by: " + collection.getUser().getNickname(), META_FONT);
        owner.setAlignment(Element.ALIGN_CENTER);
        owner.setSpacingAfter(20);
        document.add(owner);

        // Add description
        if (collection.getDescription() != null && !collection.getDescription().isEmpty()) {
            Paragraph description = new Paragraph(collection.getDescription(), CONTENT_FONT);
            description.setAlignment(Element.ALIGN_JUSTIFIED);
            description.setSpacingAfter(20);
            document.add(description);
        }
        
        // Add observation count
        int obsCount = collection.getObservations() != null ? collection.getObservations().size() : 0;
        Paragraph count = new Paragraph("Number of observations: " + obsCount, CONTENT_FONT);
        count.setSpacingAfter(20);
        document.add(count);
        
        // Add divider
        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);
    }

    private void addObservationsTable(Document document, Collection collection) throws DocumentException {
        // Add section title for observations
        Paragraph obsTitle = new Paragraph("Observations", SUBTITLE_FONT);
        obsTitle.setSpacingAfter(10);
        document.add(obsTitle);
        
        // Details for each observation
        for (Observation observation : collection.getObservations()) {
            // Observation name as subheading
            Paragraph obsName = new Paragraph(observation.getName(), CONTENT_TITLE_FONT);
            obsName.setSpacingBefore(15);
            obsName.setSpacingAfter(5);
            document.add(obsName);
            
            // Observer info
            document.add(new Paragraph("Observer: " + observation.getUser().getNickname(), META_FONT));
            
            // Coordinates
            document.add(new Paragraph("Coordinates: RA " + 
                formatDecimal(observation.getRightAscension()) + "°, Dec " + 
                formatDecimal(observation.getDeclination()) + "°", CONTENT_FONT));
            
            // Observatory info
            if (observation.getObservatory() != null) {
                document.add(new Paragraph("Observatory: " + observation.getObservatory().getName() + 
                    " (Lat: " + formatDecimal(observation.getObservatory().getLatitude()) + 
                    "°, Long: " + formatDecimal(observation.getObservatory().getLongitude()) + "°)", CONTENT_FONT));
            }
            
            // View count
            document.add(new Paragraph("Views: " + observation.getViewCount(), META_FONT));
            
            // Description
            PdfPTable descTable = new PdfPTable(1);
            descTable.setWidthPercentage(95);
            descTable.setSpacingBefore(5);
            descTable.setSpacingAfter(10);
            
            PdfPCell descCell = new PdfPCell(new Phrase(observation.getDescription(), CONTENT_FONT));
            descCell.setPadding(5);
            descCell.setBorderColor(BaseColor.LIGHT_GRAY);
            descTable.addCell(descCell);
            
            document.add(descTable);
            
            // Divider between observations
            document.add(new LineSeparator(0.5f, 80, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -5));
        }
    }
    
    private String formatDecimal(double value) {
        return String.format("%.4f", value);
    }
}