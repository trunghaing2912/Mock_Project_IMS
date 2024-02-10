package fa.training.fjb04.ims.util.imports;

import fa.training.fjb04.ims.util.dto.jobs.JobImportDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImportFile {

    public static List<JobImportDTO> processExelFile(MultipartFile file) throws IOException {
        List<JobImportDTO> jobImportDTOList = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();  // Skip header row
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                JobImportDTO jobImportDTO = new JobImportDTO();
                jobImportDTO.setTitle(row.getCell(0).getStringCellValue());
                jobImportDTO.setStartDate(LocalDate.from(row.getCell(1).getLocalDateTimeCellValue()));
                jobImportDTO.setEndDate(LocalDate.from(row.getCell(2).getLocalDateTimeCellValue()));
                jobImportDTO.setMinSalary(BigDecimal.valueOf(row.getCell(3).getNumericCellValue()));
                jobImportDTO.setMaxSalary(BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
                jobImportDTO.setWorkingAddress(row.getCell(5).getStringCellValue());
                jobImportDTO.setDescription(row.getCell(6).getStringCellValue());
                jobImportDTO.setSkillsSet(row.getCell(7).getStringCellValue());
                jobImportDTO.setBenefitSet(row.getCell(8).getStringCellValue());
                jobImportDTO.setLevelsSet(row.getCell(9).getStringCellValue());


                // ... map other fields
                jobImportDTOList.add(jobImportDTO);
            }
        }
        return jobImportDTOList;
    }
}
