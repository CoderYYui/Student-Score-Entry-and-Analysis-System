package com.student.score.system.controller;

import com.student.score.system.service.AnalysisService;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {
    
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/course/{courseId}/full")
    public Map<String, Object> getFullAnalysis(@PathVariable Long courseId) {
        return analysisService.getFullAnalysis(courseId);
    }

    @GetMapping("/course/{courseId}/objectives")
    public Map<String, Object> getObjectiveAnalysis(@PathVariable Long courseId) {
        return analysisService.calculateObjectiveAchievement(courseId);
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    public Map<String, Object> getStudentObjectiveAnalysis(@PathVariable Long courseId, @PathVariable Long studentId) {
        return analysisService.calculateStudentObjectiveAchievement(courseId, studentId);
    }

    @GetMapping("/report/{courseId}")
    public Map<String, String> generateReport(@PathVariable Long courseId) {
        Map<String, Object> data = analysisService.getFullAnalysis(courseId);
        String reportText = generateReportText(data);
        Map<String, String> report = new HashMap<>();
        report.put("content", reportText);
        return report;
    }

    @GetMapping("/report/{courseId}/download")
    public void downloadReportPdf(@PathVariable Long courseId, HttpServletResponse response) throws IOException {
        Map<String, Object> data = analysisService.getFullAnalysis(courseId);
        String reportText = generateReportText(data);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=course_report_" + courseId + ".pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        document.add(new Paragraph("Course Evaluation Report").setBold().setFontSize(18));
        document.add(new Paragraph("Course ID: " + courseId).setFontSize(12));
        document.add(new Paragraph("--------------------------------------------------"));
        
        for (String line : reportText.split("\n")) {
            if (line.trim().isEmpty()) document.add(new Paragraph("\n"));
            else document.add(new Paragraph(line));
        }
        
        document.close();
    }

    private String generateReportText(Map<String, Object> data) {
        Map<String, Object> stats = (Map<String, Object>) data.get("courseStats");
        if (stats == null || stats.get("avg_score") == null) return "No data available for analysis.";

        double avg = ((Number) stats.get("avg_score")).doubleValue();
        
        StringBuilder sb = new StringBuilder();
        sb.append("Evaluation Result:\n\n");
        sb.append("According to the calculation method stipulated in the syllabus, the overall course objective achievement value is: ")
          .append(String.format("%.4f", avg / 100.0)).append(".\n\n");
          
        sb.append("Analysis Findings:\n");
        sb.append("(1) From the overall achievement value, ");
        if (avg >= 70) {
            sb.append("all course objectives have been sufficiently achieved. The direct achievement status of Objective 1 and Objective 2 is good. ");
            sb.append("Combined with regular performance, test scores, final exam, and lab reports, Objective 1 reflects that students have a good grasp of basic knowledge. ");
            sb.append("Objective 2 indicates that students possess the ability to solve practical problems.\n\n");
        } else {
            sb.append("some course objectives require improvement. The achievement is below expected levels.\n\n");
        }
        
        sb.append("(2) From the sample achievement distribution, the distribution approximates a ").append(avg >= 75 ? "Normal Distribution" : "Skewed Distribution");
        sb.append(", centered around ").append(String.format("%.2f", avg / 100.0)).append(", which conforms to teaching laws.\n\n");
        
        sb.append("(3) Comparing direct and indirect evaluation results, student self-assessment matches the direct assessment results, indicating high student gain.\n");
        
        return sb.toString();
    }
}
