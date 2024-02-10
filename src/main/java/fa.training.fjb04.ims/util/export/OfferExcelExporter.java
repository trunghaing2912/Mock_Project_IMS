package fa.training.fjb04.ims.util.export;

import fa.training.fjb04.ims.entity.offer.Offer;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class OfferExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Offer> offerList;

    public OfferExcelExporter(List<Offer> offerList) {
        this.offerList = offerList;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Offer");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Candidate Name", style);
        createCell(row, 1, "Email", style);
        createCell(row, 2, "Approver", style);
        createCell(row, 3, "Department", style);
        createCell(row, 4, "Notes", style);
        createCell(row, 5, "Status", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);

        if(value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for(Offer o: offerList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, o.getCandidate().getFullName(),style);
            createCell(row, columnCount++, o.getCandidate().getEmail(),style);
            createCell(row, columnCount++, o.getApprovedBy().getFullName(),style);
            createCell(row, columnCount++, o.getCandidate().getPosition().getPositionName(),style);
            createCell(row, columnCount++, o.getInterviewNotes(),style);
            createCell(row, columnCount++, o.getOfferStatus().toString(),style);
        }

    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
