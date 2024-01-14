package year.exp.lore.services;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import year.exp.lore.dto.Initiative;
import year.exp.lore.dto.Member;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DocumentService {

    public String export(Initiative dto) throws IOException {
        String filePath = "src/main/resources/founders-agreement-template.docx";
        FileInputStream fis = new FileInputStream(filePath);
        XWPFDocument document = new XWPFDocument(fis);
        textReplace(document, dto.getName());
        addFounders(document, dto.getMembers());
        addShares(document, dto.getMembers());
        String path = "src/main/resources/tmp/"+dto.getId()+".docx";
        try (FileOutputStream out = new FileOutputStream(path)) {
            document.write(out);
        }
        document.close();
        return path;
    }

    private void textReplace(XWPFDocument document, String name) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs != null) {
                for (XWPFRun run : runs) {
                    String text = run.getText(0);
                    if (text != null && text.contains("_")) {
                        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        text = text.replace("_", date);
                        run.setText(text, 0);
                    }
                    if (text != null && text.contains("=")) {
                        text = text.replace("=", name);
                        run.setText(text, 0);
                    }
                }
            }
        }
    }

    private void addFounders(XWPFDocument document, List<Member> members) {
        if (!document.getTables().isEmpty()) {
            XWPFTable table = document.getTables().get(0);
            for (int i = 1; i <= members.size(); i++) {
                XWPFTableRow newRow = table.createRow();
                Member current = members.get(i-1);
                newRow.getCell(0).setText(current.toString());
            }
        }
    }


    private void addShares(XWPFDocument document, List<Member> members) {
        float shares = (float) 100/members.size();

        if (!document.getTables().isEmpty()) {
            XWPFTable table = document.getTables().get(1);
            for (int i = 1; i <= members.size(); i++) {
                XWPFTableRow newRow = table.createRow();
                Member current = members.get(i-1);
                newRow.getCell(0).setText(current.toString());
                newRow.getCell(1).setText(shares+"%");
            }
        }

    }

    private static void setTextToParagraph(XWPFParagraph paragraph, String text) {
        paragraph.removeRun(0);
        paragraph.createRun().setText(text, 0);
    }


}
