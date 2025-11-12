package com.authcodelab.smartmoneymanageapp.service;

import com.authcodelab.smartmoneymanageapp.dto.IncomeEmailDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendIncomeReport(String to, List<IncomeEmailDTO> incomes) throws Exception {
        // Build Excel file
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Income Report");

            // Create header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Date");
            header.createCell(1).setCellValue("Source");
            header.createCell(2).setCellValue("Amount");
            header.createCell(3).setCellValue("Description");

            // Add data rows
            int rowNum = 1;
            for (IncomeEmailDTO dto : incomes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dto.getDate() != null ? dto.getDate().toString() : "");
                row.createCell(1).setCellValue(dto.getSource() != null ? dto.getSource() : "");
                row.createCell(2).setCellValue(dto.getAmount() != null ? dto.getAmount().doubleValue() : 0.0);
                row.createCell(3).setCellValue(dto.getDescription() != null ? dto.getDescription() : "");
            }

            // Auto-size columns
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(baos);
        }

        byte[] excelFile = baos.toByteArray();

        // Calculate total
        BigDecimal total = incomes.stream()
                .map(i -> i.getAmount() != null ? i.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Send email with attachment
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Income Report");

        String htmlBody = "<html><body>" +
                "<h2>Income Report</h2>" +
                "<p>Please find attached the complete income report.</p>" +
                "<p><strong>Total Income: </strong>" + total + "</p>" +
                "<p><strong>Total Records: </strong>" + incomes.size() + "</p>" +
                "</body></html>";

        helper.setText(htmlBody, true);
        helper.addAttachment("income-report.xlsx", new ByteArrayResource(excelFile));

        mailSender.send(message);
    }
}
