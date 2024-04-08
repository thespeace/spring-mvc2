package thespeace.springmvc2.upload.controller;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadItemForm {

    private Long itemId;
    private String itemName;
    private MultipartFile attachFile;
    private List<MultipartFile> imageFiles; //이미지를 다중 업로드 하기 위해 MultipartFile 를 사용
}
