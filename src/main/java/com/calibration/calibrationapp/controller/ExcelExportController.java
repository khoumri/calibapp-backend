package com.calibration.calibrationapp.controller;

import com.calibration.calibrationapp.service.ExcelExportService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/admin")
public class ExcelExportController {

    private final ExcelExportService excelExportService;

    public ExcelExportController(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }

    /**
     * GET /admin/clients/{id}/export-excel
     * Télécharge un fichier .xlsx avec tous les appareils du client
     */
    @GetMapping("/clients/{id}/export-excel")
    public ResponseEntity<byte[]> exportClientExcel(@PathVariable Long id,
            @RequestParam(defaultValue = "Client") String clientName) {
        try {
            byte[] data = excelExportService.exportClientAppareils(id);

            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String safeName = clientName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            String filename = "A2m" + safeName + "_" + today + ".xlsx";
            String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + encoded)
                    .header(HttpHeaders.CONTENT_TYPE,
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(data);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}