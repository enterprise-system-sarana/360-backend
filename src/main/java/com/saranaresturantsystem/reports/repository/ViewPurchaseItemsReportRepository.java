package com.saranaresturantsystem.reports.repository;

import com.saranaresturantsystem.reports.entities.ViewPurchaseItemsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ViewPurchaseItemsReportRepository extends JpaRepository<ViewPurchaseItemsReport, Long>, JpaSpecificationExecutor<ViewPurchaseItemsReport> {
}
