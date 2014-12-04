package com.adms.batch.enums;

public enum EFileFormat {
	TSR_TRACKING("tsrTracking", "config/fileformat/tsrTrackingFormat.xml"),
	SALES_BY_RECORD("salesByRecord", "config/fileformat/salesByRecFormat.xml"),
	QC_RECONFIRM("qcReconfirm", "config/fileformat/qcReconfirmFormat.xml"),
	DAILY_PERFORMANCE("dailyPerformanceTracking", "config/fileformat/dailyPerformanceFormat.xml"),
	
	TSR_IMPORT("tsrImport", "config/fileformat/tsrImportFormat.xml"),
	CAMPAIGN_KEY_CODE("campaignKeyCode", "config/fileformat/campaignKeyCodeFormat.xml"),
	POSITION_IMPORT("positionImportFormat", "config/fileformat/positionImportFormat.xml"),
	KPI_SETUP_IMPORT("kpiSetupImport", "config/fileformat/kpiTargetSetupFormat.xml"),
	KPI_EOC_IMPORT("kpiEocImport", "config/fileformat/eocFormat.xml"),
	
	DAILY_PERFORMANCE_OTO("dailyPerformanceOTO", "config/fileformat/dailyPerformance121Format.xml"),
	SALES_BY_RECORD_OTO("salesByRecordOTO", "config/fileformat/salesByRec121Format.xml"),
	TSR_TRACKING_OTO("tsrTrackingOTO", "config/fileformat/tsrTracking121Format.xml"),
	
	SALES_BY_RECORD_MSIG("salesByRecordMSIG", "config/fileformat/salesByRecMSIGFormat.xml"),
	
	QC_RECONFIRM_NEW("qcReconfirmNew", "config/fileformat/qcReconfirmFormat_new.xml");
	
	private String code;
	private String value;
	
	private EFileFormat(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}
	
	public String getValue() {
		return value;
	}
}
