package com.adms.batch.enums;

public enum EFileFormat {
	TSR_TRACKING("tsrTracking", "config/fileformat/tsrTrackingFormat.xml"),
	SALES_BY_RECORD("salesByRecord", "config/fileformat/salesByRecFormat.xml"),
	QC_RECONFIRM("qcReconfirm", "config/fileformat/qcReconfirmFormat.xml"),
	DAILY_PERFORMANCE("dailyPerformanceTracking", "config/fileformat/dailyPerformanceFormat.xml"),
	TSR_IMPORT("tsrImport", "config/fileformat/tsrImportFormat.xml");
	
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
