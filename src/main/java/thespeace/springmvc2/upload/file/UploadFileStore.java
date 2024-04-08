package thespeace.springmvc2.upload.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import thespeace.springmvc2.upload.domain.UploadFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UploadFileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName);
    }

    /**
     * <h2>서버에 저장할 파일명 생성</h2>
     * 서버 내부에서 관리하는 파일명은 유일한 이름을 생성하는 UUID 를 사용해서 충돌하지 않도록 한다.
     */
    private String createStoreFileName(String originalFilename) {
        //UUID
        String uuid = UUID.randomUUID().toString();

        //확장자
        String ext = extractExt(originalFilename);

        //저장할 파일명(UUID.확장자)
        return uuid + "." + ext;
    }

    /**
     * <h2>확장자를 별도로 추출</h2>
     */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
