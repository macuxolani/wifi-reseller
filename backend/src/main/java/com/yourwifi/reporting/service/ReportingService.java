package com.yourwifi.reporting.service;

import com.yourwifi.reporting.dto.ReportSummaryDto;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {

    public ReportSummaryDto getExecutiveSummary() {
        return new ReportSummaryDto(
            "Today",
            0,
            0,
            0,
            0,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
    }
}
