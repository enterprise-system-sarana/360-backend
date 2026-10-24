package com.saranaresturantsystem.reports.repository;

import com.saranaresturantsystem.reports.entities.ViewSalesItemsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ViewSalesItemsReportRepository extends JpaRepository<ViewSalesItemsReport, Long>, JpaSpecificationExecutor<ViewSalesItemsReport> {
}
