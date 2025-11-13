package com.swp391.evdealersystem.service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.swp391.evdealersystem.dto.response.DeliverySlipDTO;
import com.swp391.evdealersystem.dto.response.DepositContractDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

    // Lấy thông tin công ty từ properties
    @Value("${evdealer.company.name}")      private String companyName;
    @Value("${evdealer.company.tax-code}")  private String companyTaxCode;
    @Value("${evdealer.company.address}")   private String companyAddress;
    @Value("${evdealer.company.phone}")     private String companyPhone;
    @Value("${evdealer.company.rep-name}")  private String legalRepName;
    @Value("${evdealer.company.rep-title}") private String legalRepTitle;

    // Các hằng số định dạng
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final NumberFormat CURRENCY_FORMAT = new DecimalFormat("#,##0 VND");

    /**
     * HÀM IN HỢP ĐỒNG (Đã sửa lỗi NullPointerException)
     */
    @Override
    public byte[] generateDepositContractPdf(DepositContractDTO dto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont font = PdfFontFactory.createFont("Helvetica");
        PdfFont fontBold = PdfFontFactory.createFont("Helvetica-Bold");

        // === FIX: BIẾN AN TOÀN (NULL-SAFE) ===
        // Lấy các giá trị BigDecimal, nếu null thì coi là 0
        BigDecimal totalPrice = (dto.getVehicleTotalPrice() != null) ? dto.getVehicleTotalPrice() : BigDecimal.ZERO;
        BigDecimal plannedDeposit = (dto.getPlannedDepositAmount() != null) ? dto.getPlannedDepositAmount() : BigDecimal.ZERO;
        // Tính toán an toàn
        BigDecimal remainingBalance = totalPrice.subtract(plannedDeposit);
        // ======================================

        // === 2. Tiêu đề ===
        document.add(new Paragraph("VEHICLE DEPOSIT AGREEMENT")
                .setFont(fontBold).setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        // --- (Code phần tiêu đề, Bên A, Bên B... giữ nguyên) ---
        document.add(new Paragraph("Agreement #" + dto.getContractNumber())
                .setFont(font).setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Date: " + dto.getContractDate().format(DATE_FORMATTER))
                .setFont(font).setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        float[] columnWidths = {1, 1};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        table.setBorder(null);

        Cell cellSeller = new Cell().setBorder(null).setPaddingRight(10);
        cellSeller.add(new Paragraph("PARTY B (SELLER)").setFont(fontBold).setFontSize(14).setUnderline());
        cellSeller.add(new Paragraph(this.companyName).setFont(font)); // <-- Sửa: Dùng this.companyName
        cellSeller.add(new Paragraph("Tax Code: " + this.companyTaxCode).setFont(font)); // <-- Sửa
        cellSeller.add(new Paragraph("Address: " + this.companyAddress).setFont(font)); // <-- Sửa
        cellSeller.add(new Paragraph("Phone: " + this.companyPhone).setFont(font)); // <-- Sửa
        cellSeller.add(new Paragraph("Represented by: " + this.legalRepName + " (" + this.legalRepTitle + ")").setFont(font)); // <-- Sửa
        table.addCell(cellSeller);

        Cell cellBuyer = new Cell().setBorder(null).setPaddingLeft(10);
        cellBuyer.add(new Paragraph("PARTY A (BUYER)").setFont(fontBold).setFontSize(14).setUnderline());
        cellBuyer.add(new Paragraph(dto.getCustomerName()).setFont(font));
        cellBuyer.add(new Paragraph("Address: " + dto.getCustomerAddress()).setFont(font));
        cellBuyer.add(new Paragraph("Phone: " + dto.getCustomerPhone()).setFont(font));
        cellBuyer.add(new Paragraph("Citizen ID: " + dto.getCustomerCitizenId()).setFont(font));
        cellBuyer.add(new Paragraph("Email: " + dto.getCustomerEmail()).setFont(font));
        table.addCell(cellBuyer);

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
        document.add(new Paragraph("VEHICLE DETAILS").setFont(fontBold).setFontSize(14).setUnderline().setMarginBottom(5));
        document.add(new Paragraph("Brand: " + dto.getVehicleBrand()).setFont(font));
        document.add(new Paragraph("Model: " + dto.getVehicleModelCode()).setFont(font));
        document.add(new Paragraph("Year: " + dto.getVehicleProductionYear()).setFont(font));
        document.add(new Paragraph("Color: " + dto.getVehicleColor()).setFont(font));
        document.add(new Paragraph("VIN (Vehicle Identification Number): " + dto.getVehicleVin()).setFont(fontBold));

        // === SỬA LỖI 1 ===
        // Dùng biến 'totalPrice' (đã null-safe) thay vì 'dto.getVehicleTotalPrice()'
        document.add(new Paragraph("Total Price: " + CURRENCY_FORMAT.format(totalPrice)).setFont(fontBold));
        document.add(new Paragraph().setMarginBottom(15));

        document.add(new Paragraph("TERMS AND CONDITIONS").setFont(fontBold).setFontSize(14).setUnderline().setMarginBottom(10));

        // === SỬA LỖI 2 ===
        // Dùng biến 'plannedDeposit' (đã null-safe)
        document.add(new Paragraph("1. Deposit Agreement").setFont(fontBold));
        document.add(new Paragraph("Party A agrees to pay a non-refundable deposit in the amount of "
                + CURRENCY_FORMAT.format(plannedDeposit) + " " // <-- Đã sửa
                + "to Party B to reserve the vehicle specified above. This deposit secures the vehicle for Party A.")
                .setFont(font).setMarginBottom(10));

        // === SỬA LỖI 3 ===
        // Dùng biến 'remainingBalance' (đã null-safe)
        document.add(new Paragraph("2. Payment Terms").setFont(fontBold));
        document.add(new Paragraph("The remaining balance ("
                + CURRENCY_FORMAT.format(remainingBalance) // <-- Đã sửa
                + ") must be paid in full on or before the agreed delivery date. Failure to complete payment may result in forfeiture of the deposit.")
                .setFont(font).setMarginBottom(10));

        // === SỬA LỖI 4 ===
        // Dùng biến 'plannedDeposit' (đã null-safe)
        document.add(new Paragraph("3. Cancellation Policy").setFont(fontBold));
        document.add(new Paragraph("If Party A fails to complete the purchase, the deposit amount of "
                + CURRENCY_FORMAT.format(plannedDeposit) // <-- Đã sửa
                + " shall be forfeited to Party B as liquidated damages.")
                .setFont(font).setMarginBottom(10));

        document.add(new Paragraph("4. Governing Law").setFont(fontBold));
        document.add(new Paragraph("This Agreement shall be governed by and construed in accordance with the laws of Vietnam.")
                .setFont(font).setMarginBottom(30));

        // (Phần chữ ký giữ nguyên)
        Table signatureTable = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        signatureTable.setBorder(null);

        signatureTable.addCell(new Cell().setBorder(null).add(new Paragraph("PARTY A (Buyer Signature)").setFont(fontBold).setTextAlignment(TextAlignment.CENTER)));
        signatureTable.addCell(new Cell().setBorder(null).add(new Paragraph("PARTY B (Seller Signature)").setFont(fontBold).setTextAlignment(TextAlignment.CENTER)));

        signatureTable.addCell(new Cell().setBorder(null).setHeight(80));
        signatureTable.addCell(new Cell().setBorder(null).setHeight(80));

        signatureTable.addCell(new Cell().setBorder(null).add(new Paragraph("(Sign and print full name)").setFont(font).setTextAlignment(TextAlignment.CENTER)));
        signatureTable.addCell(new Cell().setBorder(null).add(new Paragraph("(Sign, print full name, and stamp)").setFont(font).setTextAlignment(TextAlignment.CENTER)));

        document.add(signatureTable);
        // --- KẾT THÚC PHẦN CODE HỢP ĐỒNG ---

        document.close();
        return baos.toByteArray();
    }

    /**
     * HÀM IN PHIẾU GIAO XE (Đã sửa lỗi NullPointerException)
     */
    @Override
    public byte[] generateDeliverySlipPdf(DeliverySlipDTO dto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont font = PdfFontFactory.createFont("Helvetica");
        PdfFont fontBold = PdfFontFactory.createFont("Helvetica-Bold");

        // === FIX: BIẾN AN TOÀN (NULL-SAFE) ===
        // Dùng 'this' để lấy thông tin công ty từ @Value
        String companyName = (this.companyName != null) ? this.companyName : "EV Dealer";
        String companyAddress = (this.companyAddress != null) ? this.companyAddress : "N/A";
        String companyPhone = (this.companyPhone != null) ? this.companyPhone : "N/A";

        // Dùng dto để lấy thông tin (check null)
        String salesName = (dto.getSalespersonName() != null) ? dto.getSalespersonName() : "N/A";
        String customerName = (dto.getCustomerName() != null) ? dto.getCustomerName() : "N/A";
        String customerAddress = (dto.getCustomerAddress() != null) ? dto.getCustomerAddress() : "N/A";
        String customerPhone = (dto.getCustomerPhone() != null) ? dto.getCustomerPhone() : "N/A";
        String vehicleBrand = (dto.getVehicleBrand() != null) ? dto.getVehicleBrand() : "N/A";
        String vehicleModel = (dto.getVehicleModelCode() != null) ? dto.getVehicleModelCode() : "N/A";
        String vehicleColor = (dto.getVehicleColor() != null) ? dto.getVehicleColor() : "N/A";
        String vehicleVin = (dto.getVehicleVin() != null) ? dto.getVehicleVin() : "N/A";
        // ======================================


        // === 1. Tiêu đề ===
        document.add(new Paragraph("VEHICLE DELIVERY SLIP")
                .setFont(fontBold).setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        document.add(new Paragraph("Order #" + dto.getOrderId())
                .setFont(font).setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Delivery Date: " + dto.getDeliveryDate().format(DATE_FORMATTER))
                .setFont(fontBold).setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(25));

        // === 2. Thông tin Showroom (Bên Giao) ===
        document.add(new Paragraph("FROM (SELLER):").setFont(fontBold).setFontSize(14));
        document.add(new Paragraph(companyName).setFont(font));
        document.add(new Paragraph("Address: " + companyAddress).setFont(font));
        document.add(new Paragraph("Phone: " + companyPhone).setFont(font));
        document.add(new Paragraph("Sales Representative: " + salesName)
                .setFont(font).setMarginBottom(15));

        // === 3. Thông tin Khách hàng (Bên Nhận) ===
        document.add(new Paragraph("TO (CUSTOMER):").setFont(fontBold).setFontSize(14));
        document.add(new Paragraph("Customer Name: " + customerName).setFont(font));
        document.add(new Paragraph("Address: " + customerAddress).setFont(font));
        document.add(new Paragraph("Phone: " + customerPhone).setFont(font).setMarginBottom(15)); // <-- Đã sửa

        // === 4. Thông tin Xe (Tài sản) ===
        document.add(new Paragraph("DELIVERED VEHICLE DETAILS:").setFont(fontBold).setFontSize(14));
        document.add(new Paragraph("Brand: " + vehicleBrand).setFont(font)); // <-- Đã sửa
        document.add(new Paragraph("Model: " + vehicleModel).setFont(font)); // <-- Đã sửa
        document.add(new Paragraph("Color: " + vehicleColor).setFont(font)); // <-- Đã sửa
        document.add(new Paragraph("VIN (Vehicle Identification Number): " + vehicleVin) // <-- Đã sửa
                .setFont(fontBold).setMarginBottom(30));

        // === 5. Xác nhận ===
        document.add(new Paragraph("I, " + customerName + ", hereby confirm that I have received the vehicle " // <-- Đã sửa
                + "listed above in good condition and with all required documentation.")
                .setFont(font).setMarginBottom(50));

        // === 6. Chữ ký ===
        float[] columnWidths = {1, 1};
        Table signatureTable = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        signatureTable.setBorder(null);

        signatureTable.addCell(new Cell().setBorder(null).add(new Paragraph("Customer Signature").setFont(fontBold).setTextAlignment(TextAlignment.CENTER)));
        signatureTable.addCell(new Cell().setBorder(null).add(new Paragraph("Seller Signature").setFont(fontBold).setTextAlignment(TextAlignment.CENTER)));

        signatureTable.addCell(new Cell().setBorder(null).setHeight(80)); // Khoảng trống ký
        signatureTable.addCell(new Cell().setBorder(null).setHeight(80)); // Khoảng trống ký

        document.add(signatureTable);

        document.close();
        return baos.toByteArray();
    }
}