package com.laboratory.airlinebackend.controller.service;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.itextpdf.text.*;
import com.laboratory.airlinebackend.controller.DTO.ConsultCheckInDTO;
import org.springframework.stereotype.Service;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGenerationService {

    public byte[] generatePdf(ConsultCheckInDTO checkInDTO) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add content to the PDF
            addContentToPdf(document, checkInDTO);

            // Add QR code to the PDF
            String content = checkInDTO.toString();
            byte[] qrCodeImage = generateQrCode(content);
            Image image = Image.getInstance(qrCodeImage);
            //image.scaleToFit(100, 100);
            document.add(image);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    private byte[] generateQrCode(String message) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(message, com.google.zxing.BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    private void addContentToPdf(Document document, ConsultCheckInDTO checkInDTO) throws DocumentException {
        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Boarding Pass", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Add information
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        document.add(new Paragraph("Flight ID: " + checkInDTO.getFlightId(), infoFont));
        document.add(new Paragraph("Passenger ID: " + checkInDTO.getPassengerId(), infoFont));
        document.add(new Paragraph("Email: " + checkInDTO.getEmail(), infoFont));
        document.add(new Paragraph("Name: " + checkInDTO.getFirstName() + " " + checkInDTO.getLastName(), infoFont));
        document.add(new Paragraph("DNI: " + checkInDTO.getDNI(), infoFont));
        document.add(new Paragraph("Check-in Status: " + (checkInDTO.isDidCheckIn() ? "Checked In" : "Not Checked In"), infoFont));
        document.add(new Paragraph("Origin: " + checkInDTO.getOrigin(), infoFont));
        document.add(new Paragraph("Destination: " + checkInDTO.getDestination(), infoFont));
        document.add(new Paragraph("Flight Date: " + checkInDTO.getFlightDate(), infoFont));
        document.add(new Paragraph("State: " + checkInDTO.getState(), infoFont));
        document.add(new Paragraph("Seat ID: " + checkInDTO.getSeatId(), infoFont));
        document.add(new Paragraph("Seat Number: " + checkInDTO.getSeatNumber() + " " + checkInDTO.getSeatLetter(), infoFont));
    }
}
