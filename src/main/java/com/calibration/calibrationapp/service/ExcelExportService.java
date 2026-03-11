package com.calibration.calibrationapp.service;

import com.calibration.calibrationapp.entity.Appareil;
import com.calibration.calibrationapp.repository.AppareilRepository;
import com.calibration.calibrationapp.repository.ClientRepository;
import com.calibration.calibrationapp.entity.Client;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private final AppareilRepository appareilRepository;
    private final ClientRepository clientRepository;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ExcelExportService(AppareilRepository appareilRepository, ClientRepository clientRepository) {
        this.appareilRepository = appareilRepository;
        this.clientRepository = clientRepository;
    }

    public byte[] exportClientAppareils(Long clientId) throws Exception {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        List<Appareil> appareils = appareilRepository.findAll().stream()
                .filter(a -> a.getClient() != null && a.getClient().getId().equals(clientId))
                .toList();

        try (XSSFWorkbook wb = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet ws = wb.createSheet("Appareils");

            // ── Styles ──
            XSSFCellStyle titleStyle = createStyle(wb, "0A3D7A", "FFFFFF", 18, true, HorizontalAlignment.CENTER);
            XSSFCellStyle subClientStyle = createStyle(wb, "EBF3FF", "1E6BC4", 12, true, HorizontalAlignment.LEFT);
            XSSFCellStyle subDateStyle = createStyle(wb, "EBF3FF", "7A93B0", 10, false, HorizontalAlignment.RIGHT);
            XSSFCellStyle headerStyle = createStyle(wb, "1E6BC4", "FFFFFF", 10, true, HorizontalAlignment.CENTER);
            XSSFCellStyle dataStyleLight = createStyle(wb, "F0F6FF", "0A2540", 10, false, HorizontalAlignment.CENTER);
            XSSFCellStyle dataStyleWhite = createStyle(wb, "FFFFFF", "0A2540", 10, false, HorizontalAlignment.CENTER);
            XSSFCellStyle dataStyleLeftLight = createStyle(wb, "F0F6FF", "0A2540", 10, false, HorizontalAlignment.LEFT);
            XSSFCellStyle dataStyleLeftWhite = createStyle(wb, "FFFFFF", "0A2540", 10, false, HorizontalAlignment.LEFT);
            XSSFCellStyle totalStyle = createStyle(wb, "0A3D7A", "FFFFFF", 10, true, HorizontalAlignment.RIGHT);
            XSSFCellStyle validStyle = createStyle(wb, "F0FFF7", "27AE60", 10, true, HorizontalAlignment.CENTER);
            XSSFCellStyle soonStyle = createStyle(wb, "FFF8F0", "E67E22", 10, true, HorizontalAlignment.CENTER);
            XSSFCellStyle expiredStyle = createStyle(wb, "FEF2F2", "E74C3C", 10, true, HorizontalAlignment.CENTER);
            XSSFCellStyle noSessionStyle = createStyle(wb, "F7FAFF", "A0B4C8", 10, false, HorizontalAlignment.CENTER);

            // ── Largeurs colonnes ──
            int[] widths = { 7000, 4000, 4500, 4500, 5000, 6000, 5000, 5000, 4500 };
            for (int i = 0; i < widths.length; i++)
                ws.setColumnWidth(i, widths[i]);

            // ── LIGNE 1 : Titre ──
            Row r1 = ws.createRow(0);
            r1.setHeightInPoints(46);
            Cell c1 = r1.createCell(0);
            c1.setCellValue("A2m  —  Rapport d'Étalonnage");
            c1.setCellStyle(titleStyle);
            ws.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            // ── LIGNE 2 : Client + date ──
            Row r2 = ws.createRow(1);
            r2.setHeightInPoints(26);
            Cell c2 = r2.createCell(0);
            c2.setCellValue("Client : " + client.getName());
            c2.setCellStyle(subClientStyle);
            ws.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
            Cell c2d = r2.createCell(6);
            c2d.setCellValue("Généré le " + LocalDate.now().format(FMT));
            c2d.setCellStyle(subDateStyle);
            ws.addMergedRegion(new CellRangeAddress(1, 1, 6, 8));

            // ── LIGNE 3 : vide ──
            ws.createRow(2).setHeightInPoints(6);

            // ── LIGNE 4 : En-têtes ──
            String[] headers = { "Désignation", "Code", "Marque", "Type", "N° Série", "N° Certificat",
                    "Date Étalonnage", "Prochaine Date", "Statut" };
            Row r4 = ws.createRow(3);
            r4.setHeightInPoints(30);
            for (int i = 0; i < headers.length; i++) {
                Cell c = r4.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // ── LIGNES DONNÉES ──
            int rowIdx = 4;
            for (Appareil a : appareils) {
                Row row = ws.createRow(rowIdx);
                row.setHeightInPoints(22);
                boolean even = rowIdx % 2 == 0;

                XSSFCellStyle left = even ? dataStyleLeftLight : dataStyleLeftWhite;
                XSSFCellStyle center = even ? dataStyleLight : dataStyleWhite;

                String[] values = {
                        nvl(a.getDesignation()), nvl(a.getCode()), nvl(a.getMarque()), nvl(a.getType()),
                        nvl(a.getNumeroSerie()), nvl(a.getNumeroCertificat()),
                        fmtDate(a.getDateEtalonnage()), fmtDate(a.getProchaineDate()),
                        getStatut(a.getProchaineDate())
                };

                for (int c = 0; c < values.length; c++) {
                    Cell cell = row.createCell(c);
                    cell.setCellValue(values[c]);
                    if (c == 0)
                        cell.setCellStyle(left);
                    else if (c == 8) {
                        // Statut coloré
                        String statut = values[8];
                        if (statut.equals("Valide"))
                            cell.setCellStyle(validStyle);
                        else if (statut.equals("Expire bientôt"))
                            cell.setCellStyle(soonStyle);
                        else if (statut.equals("Expiré"))
                            cell.setCellStyle(expiredStyle);
                        else
                            cell.setCellStyle(noSessionStyle);
                    } else
                        cell.setCellStyle(center);
                }
                rowIdx++;
            }

            // ── LIGNE TOTAL ──
            Row rtotal = ws.createRow(rowIdx);
            rtotal.setHeightInPoints(24);
            Cell ctotal = rtotal.createCell(0);
            ctotal.setCellValue("Total : " + appareils.size() + " appareil(s) enregistré(s)");
            ctotal.setCellStyle(totalStyle);
            ws.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 8));

            // ── Auto-filter + freeze ──
            ws.setAutoFilter(new CellRangeAddress(3, rowIdx - 1, 0, 8));
            ws.createFreezePane(0, 4);

            wb.write(out);
            return out.toByteArray();
        }
    }

    // ── Helpers ──
    private XSSFCellStyle createStyle(XSSFWorkbook wb, String bg, String fg, int fontSize, boolean bold,
            HorizontalAlignment align) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(hexToXSSF(wb, bg));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(align);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBottomBorderColor(hexToXSSF(wb, "D6E3F0"));
        style.setTopBorderColor(hexToXSSF(wb, "D6E3F0"));
        style.setLeftBorderColor(hexToXSSF(wb, "D6E3F0"));
        style.setRightBorderColor(hexToXSSF(wb, "D6E3F0"));
        XSSFFont font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(bold);
        font.setColor(hexToXSSF(wb, fg));
        style.setFont(font);
        return style;
    }

    private XSSFColor hexToXSSF(XSSFWorkbook wb, String hex) {
        byte[] rgb = new byte[] {
                (byte) Integer.parseInt(hex.substring(0, 2), 16),
                (byte) Integer.parseInt(hex.substring(2, 4), 16),
                (byte) Integer.parseInt(hex.substring(4, 6), 16)
        };
        return new XSSFColor(rgb, wb.getStylesSource().getIndexedColors());
    }

    private String nvl(String s) {
        return s != null ? s : "—";
    }

    private String fmtDate(LocalDate d) {
        return d != null ? d.format(FMT) : "—";
    }

    private String getStatut(LocalDate prochaine) {
        if (prochaine == null)
            return "Pas de session";
        long diff = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), prochaine);
        if (diff < 0)
            return "Expiré";
        if (diff <= 30)
            return "Expire bientôt";
        return "Valide";
    }
}