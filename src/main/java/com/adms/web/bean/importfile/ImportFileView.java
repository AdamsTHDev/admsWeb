package com.adms.web.bean.importfile;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.PageRequest;

import com.adms.bo.filebase.FileBaseBo;
import com.adms.bo.importfile.ImportFileBo;
import com.adms.domain.entities.FileBase;
import com.adms.domain.entities.ImportFile;
import com.adms.web.bean.base.AbstractSearchBean;
import com.adms.web.model.importfile.ImportFileLazyModel;
import com.adms.web.utils.CryptUtil;
import com.adms.web.utils.FileUtils;

@ManagedBean
@ViewScoped
public class ImportFileView extends AbstractSearchBean<ImportFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6038082994061062532L;

	@ManagedProperty(value = "#{importFileBo}")
	private ImportFileBo importFileBo;
	
	@ManagedProperty(value = "#{fileBaseBo}")
	private FileBaseBo fileBaseBo;

	private String fileUploadName;
	private UploadedFile uploadedFile;
	private ImportFileLazyModel importFileModel;
	
	@PostConstruct
	public void init() {
		getAllImportFile();
		fileUploadName = null;
	}
	
	public String nav() {
		return "importFile?faces-redirect=true";
	}
	
	public void handleFileUpload(FileUploadEvent event) {
		if (event.getFile() != null) {
			fileUploadName = event.getFile().getFileName();
			uploadedFile = event.getFile();
		}
	}
	
	public void submitFileUpload() {

		try {

			if(uploadedFile == null) {
				// do something
			} else {
				
				String base64 = CryptUtil.getInstance().base64Encode(uploadedFile.getContents());
				
				ImportFile importFile = new ImportFile();
				importFile.setFileName(uploadedFile.getFileName());
				importFile.setContentType(uploadedFile.getContentType());
				importFile.setFileSize(uploadedFile.getSize());
				
				// insert to importFile
				ImportFile file = importFileBo.addImportFile(importFile, super.getLoginBean().getUsername());

				FileBase fileBase = new FileBase();
				fileBase.setId(file.getId());
				fileBase.setBase64(base64);
				
				// insert to fileBase
				fileBaseBo.addFileBase(fileBase);
			}
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadFile(String id) {
		try {
			Long idVal = Long.parseLong(id);
			
			FileBase file = fileBaseBo.findById(idVal);
			ImportFile importFile = importFileBo.findById(idVal);
			
			FileUtils.getInstance().downloadFile(importFile.getFileName()
					, importFile.getContentType()
					, CryptUtil.getInstance().base64Decode(file.getBase64()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getAllImportFile() {
		try {
			importFileModel = new ImportFileLazyModel(importFileBo.findImportFileAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setImportFileBo(ImportFileBo importFileBo) {
		this.importFileBo = importFileBo;
	}
	
	public void setFileBaseBo(FileBaseBo fileBaseBo) {
		this.fileBaseBo = fileBaseBo;
	}
	
	public String getFileUploadName() {
		return fileUploadName;
	}

	public void setFileUploadName(String fileUploadName) {
		this.fileUploadName = fileUploadName;
	}

	public ImportFileLazyModel getImportFileModel() {
		return importFileModel;
	}

	public void setImportFileModel(ImportFileLazyModel importFileModel) {
		this.importFileModel = importFileModel;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	@Override
	public List<ImportFile> search(ImportFile object, PageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getTotalCount(ImportFile object) {
		// TODO Auto-generated method stub
		return null;
	}

}
