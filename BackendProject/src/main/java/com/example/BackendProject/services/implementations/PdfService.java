package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.dto.LigneCommandeDto;
import com.example.BackendProject.services.interfaces.CommandeServiceInterface;
import com.example.BackendProject.services.interfaces.LigneCommandeServiceInterface;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);
    private final CommandeServiceInterface commandeService;
    private final LigneCommandeServiceInterface ligneCommandeService;

    public PdfService(CommandeServiceInterface commandeService, LigneCommandeServiceInterface ligneCommandeService) {
        this.commandeService = commandeService;
        this.ligneCommandeService = ligneCommandeService;
    }

    public byte[] generateTicket(Long commandeId) {
        CommandeDto commande = commandeService.getById(commandeId);
        List<LigneCommandeDto> lignes = ligneCommandeService.findByCommandeId(commandeId);

        // Définir une taille de ticket de caisse (80mm de large environ)
        Rectangle pageSize = new Rectangle(226, 800); // 226 points = ~80mm
        Document document = new Document(pageSize, 10, 10, 10, 10);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();

            // Polices
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font fontRegular = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);

            // En-tête
            Paragraph titre = new Paragraph("GUSTO RESTAURANT", fontHeader);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);

            String dateStr = (commande.getDateHeureCommande() != null)
                    ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(commande.getDateHeureCommande())
                    : "N/A";

            Paragraph info = new Paragraph("Ticket #" + commande.getId() + "\n" +
                    "Table: " + commande.getTableId() + "\n" +
                    "Date: " + dateStr, fontRegular);
            info.setAlignment(Element.ALIGN_CENTER);
            info.setSpacingAfter(5);
            document.add(info);

            document.add(new Paragraph("--------------------------------------------------", fontRegular));

            // Table des articles
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 1, 1.5f});

            table.addCell(getCell("Article", fontBold, Element.ALIGN_LEFT));
            table.addCell(getCell("Qté", fontBold, Element.ALIGN_CENTER));
            table.addCell(getCell("Total", fontBold, Element.ALIGN_RIGHT));

            for (LigneCommandeDto ligne : lignes) {
                // Note: Ici vous devriez idéalement avoir le nom du plat dans le DTO
                table.addCell(getCell("Plat " + ligne.getPlat(), fontRegular, Element.ALIGN_LEFT));
                table.addCell(getCell(String.valueOf(ligne.getQuantite()), fontRegular, Element.ALIGN_CENTER));

                BigDecimal ligneTotal = ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantite()));
                table.addCell(getCell(ligneTotal + "€", fontRegular, Element.ALIGN_RIGHT));
            }

            document.add(table);
            document.add(new Paragraph("--------------------------------------------------", fontRegular));

            // Totaux
            Paragraph total = new Paragraph("TOTAL: " + commande.getTotalTtc() + " €", fontHeader);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            Paragraph merci = new Paragraph("\nMerci de votre visite !", fontRegular);
            merci.setAlignment(Element.ALIGN_CENTER);
            document.add(merci);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Erreur génération PDF pour commande {}", commandeId, e);
            throw new RuntimeException("Erreur lors de la génération du ticket PDF");
        }
    }

    private PdfPCell getCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }
}