package com.saranaresturantsystem.reports.repository;

import com.saranaresturantsystem.reports.entities.ViewStockMovementReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ViewStockMovementReportRepository extends JpaRepository<ViewStockMovementReport, Long>, JpaSpecificationExecutor<ViewStockMovementReport> {
}
