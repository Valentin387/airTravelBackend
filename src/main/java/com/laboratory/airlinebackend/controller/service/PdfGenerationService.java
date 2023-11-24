package com.laboratory.airlinebackend.controller.service;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.itextpdf.text.Image;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGenerationService {

    public byte[] generatePdf(String content) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add content to the PDF
            //document.add(new Paragraph(content));

            // Add QR code to the PDF
            byte[] qrCodeImage = generateQrCode(content);
            Image image = Image.getInstance(qrCodeImage);
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
}