package com.SpringBoot.Controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.SpringBoot.repository.UserRepository;
import com.SpringBoot.entity.User;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<byte[]> generateUserReport() throws Exception {
        List<User> users = userRepository.findAll();

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);

        InputStream reportStream = new ClassPathResource("reports/user_report.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        Map<String, Object> params = new HashMap<>();
        params.put("createdBy", "GrabFood Admin");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        byte[] pdfData = JasperExportManager.exportReportToPdf(jasperPrint);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
        
       // http://localhost:8081/api/reports/users
    }
}
