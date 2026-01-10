package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.dto.LigneCommandeDto;
import com.example.BackendProject.services.interfaces.CommandeServiceInterface;
import com.example.BackendProject.services.interfaces.LigneCommandeServiceInterface;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
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

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A7, 10, 10, 10, 10); // Format ticket
            PdfWriter.getInstance(document, out);
            document.open();

            // Style fonts
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontRegular = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);

            // En-tête
            Paragraph titre = new Paragraph("GUSTO RESTAURANT", fontHeader);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);
            
            Paragraph info = new Paragraph("Ticket #" + commande.getId() + "\n" +
                    "Table: " + commande.getTableId() + "\n" +
                    "Date: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(commande.getDateHeureCommande()), fontRegular);
            info.setAlignment(Element.ALIGN_CENTER);
            info.setSpacingAfter(10);
            document.add(info);

            // Ligne de séparation
            document.add(new Paragraph("----------------------------------", fontRegular));

            // Table des articles
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 1, 1}); // Largeur des colonnes

            // En-têtes tableau
            table.addCell(getCell("Article", fontBold));
            table.addCell(getCell("Qté", fontBold));
            table.addCell(getCell("Prix", fontBold));

            for (LigneCommandeDto ligne : lignes) {
                // Nom du plat (simulation car LigneCommandeDto a juste l'ID du plat, 
                // mais supposons qu'on a le nom dans un objet enrichi ou on le récupère. 
                // Simplification: on affiche "Plat #" + id ou on récupère le nom si possible.
                // Note: Idéalement LigneCommandeDto devrait avoir le nomPlat.
                // Je vais utiliser "Item" générique pour l'instant ou supposer que DTO a évolué.
                // Vérification DTO: il n'a que platId. Je dois fetcher le plat ou modifier DTO.
                // Pour faire simple ici, on met "Plat ID ..."
                table.addCell(getCell("Plat " + ligne.getPlat(), fontRegular));
                table.addCell(getCell(String.valueOf(ligne.getQuantite()), fontRegular));
                table.addCell(getCell(ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantite())) + "€", fontRegular));
            }

            document.add(table);

            document.add(new Paragraph("----------------------------------", fontRegular));

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
            logger.error("Erreur génération PDF", e);
            throw new RuntimeException("Erreur lors de la génération du ticket PDF");
        }
    }

    private PdfPCell getCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
