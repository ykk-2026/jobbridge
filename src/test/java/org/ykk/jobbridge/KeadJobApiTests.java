package org.ykk.jobbridge;

import org.junit.jupiter.api.Test;
import org.ykk.jobbridge.dto.KeadJobDTO;
import org.ykk.jobbridge.external.kead.KeadJobXmlParser;
import org.ykk.jobbridge.util.SalaryTypeCodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeadJobApiTests {

    @Test
    void parsesKeadJobXml() throws Exception {
        String xml = """
                <response>
                    <body>
                        <items>
                            <item>
                                <busplaName>한국전력공사 서울본부</busplaName>
                                <cntctNo>1588-1519</cntctNo>
                                <compAddr>서울특별시 중구 남대문로 92</compAddr>
                                <empType>계약직</empType>
                                <enterType>무관</enterType>
                                <jobNm>사무 보조원(공공기관)</jobNm>
                                <offerregDt>20260915</offerregDt>
                                <regDt>20260915</regDt>
                                <regagnName>한국장애인고용공단 서울지역본부</regagnName>
                                <reqCareer>0년</reqCareer>
                                <reqEduc>무관</reqEduc>
                                <salary>82,560</salary>
                                <salaryType>일급</salaryType>
                                <termDate>2026-09-15~2026-09-29</termDate>
                            </item>
                        </items>
                        <totalCount>1</totalCount>
                    </body>
                </response>
                """;

        KeadJobXmlParser.Page page = KeadJobXmlParser.parse(xml);
        KeadJobDTO job = page.jobs().get(0);

        assertEquals(1, page.totalCount());
        assertEquals("한국전력공사 서울본부", job.getBusinessPlaceName());
        assertEquals("일급", job.getSalaryType());
        assertEquals("2026-09-15~2026-09-29", job.getTermDate());
    }

    @Test
    void convertsSalaryToAnnualTenThousandWon() {
        assertEquals(2155, SalaryTypeCodes.toAnnualTenThousandWon(82_560L, "일급"));
        assertEquals(3000, SalaryTypeCodes.toAnnualTenThousandWon(2_500_000L, "월급"));
        assertEquals(3600, SalaryTypeCodes.toAnnualTenThousandWon(36_000_000L, "연봉"));
        assertEquals(2508, SalaryTypeCodes.toAnnualTenThousandWon(10_000L, "시급"));
    }
}
