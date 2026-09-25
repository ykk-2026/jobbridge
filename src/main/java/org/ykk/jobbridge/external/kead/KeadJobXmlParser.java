package org.ykk.jobbridge.external.kead;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.ykk.jobbridge.dto.KeadJobDTO;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class KeadJobXmlParser {

    private KeadJobXmlParser() {
    }

    public static Page parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        Document document = factory.newDocumentBuilder().parse(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        document.getDocumentElement().normalize();

        validateResult(document);

        List<KeadJobDTO> jobs = new ArrayList<>();
        NodeList itemNodes = document.getElementsByTagName("item");
        for (int i = 0; i < itemNodes.getLength(); i++) {
            Node node = itemNodes.item(i);
            if (node instanceof Element item) jobs.add(toJob(item));
        }

        return new Page(jobs, parseInt(text(document.getDocumentElement(), "totalCount")));
    }

    private static KeadJobDTO toJob(Element item) {
        KeadJobDTO job = new KeadJobDTO();
        job.setBusinessPlaceName(text(item, "busplaName"));
        job.setContactNumber(text(item, "cntctNo"));
        job.setCompanyAddress(text(item, "compAddr"));
        job.setEmploymentType(text(item, "empType"));
        job.setEntryType(text(item, "enterType"));
        job.setJobName(text(item, "jobNm"));
        job.setOfferRegisteredDate(text(item, "offerregDt"));
        job.setRegisteredDate(text(item, "regDt"));
        job.setManagingAgency(text(item, "regagnName"));
        job.setRequiredCareer(text(item, "reqCareer"));
        job.setRequiredEducation(text(item, "reqEduc"));
        job.setSalary(text(item, "salary"));
        job.setSalaryType(text(item, "salaryType"));
        job.setTermDate(text(item, "termDate"));
        return job;
    }

    private static void validateResult(Document document) {
        Element root = document.getDocumentElement();
        String code = text(root, "resultCode");
        if (code.isEmpty() || code.equals("00") || code.equals("0000")) return;

        String message = text(root, "resultMsg");
        if (message.isEmpty()) message = text(root, "resultMessage");
        throw new IllegalStateException("KEAD API 오류 " + code + ": " + message);
    }

    private static String text(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0 || nodes.item(0).getTextContent() == null) return "";
        return nodes.item(0).getTextContent().trim();
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public record Page(List<KeadJobDTO> jobs, int totalCount) {
    }
}
