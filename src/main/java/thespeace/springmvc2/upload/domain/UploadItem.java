package thespeace.springmvc2.upload.domain;

import lombok.Data;

import java.util.List;

@Data
public class UploadItem {

    private Long id;
    private String itemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles;
}
