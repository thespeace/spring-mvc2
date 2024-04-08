package thespeace.springmvc2.upload.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import thespeace.springmvc2.upload.domain.UploadFile;
import thespeace.springmvc2.upload.domain.UploadItem;
import thespeace.springmvc2.upload.domain.UploadItemRepository;
import thespeace.springmvc2.upload.file.UploadFileStore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/upload")
public class UploadItemController {

    private final UploadItemRepository itemRepository;
    private final UploadFileStore fileStore;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute UploadItemForm form) {
        return "upload/item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute UploadItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        //데이터베이스에 저장
        UploadItem item = new UploadItem();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", item.getId());

        return "redirect:/upload/items/{itemId}";
    }

    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model) {
        UploadItem item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "upload/item-view";
    }

    /**
     * <h2>이미지 조회</h2>
     * UrlResource 로 이미지 파일을 읽어서 @ResponseBody 로 이미지 바이너리를 반환한다.
     */
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    /**
     * <h2>파일 다운로드</h2>
     * 파일 다운로드 시 권한 체크같은 복잡한 상황까지 가정한다 생각하고 이미지 `id`를 요청하도록 했다.
     * 파일 다운로드시에는 고객이 업로드한 파일 이름으로 다운로드 하는게 좋다. 이때는 Content-Disposition 해더에
     * attachment; filename="업로드 파일명" 값을 주면 된다.
     */
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        UploadItem item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);

        String encodeUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodeUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

}
