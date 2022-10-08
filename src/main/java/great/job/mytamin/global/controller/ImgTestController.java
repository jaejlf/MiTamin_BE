package great.job.mytamin.global.controller;

import great.job.mytamin.topic.user.entity.User;
import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.global.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/img")
public class ImgTestController {

    private final AwsS3Service awsS3Service;

    @PostMapping("/one")
    public ResponseEntity<Object> uploadImgOne(@AuthenticationPrincipal User user, @RequestPart("file") MultipartFile file) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("단일 이미지 업로드", awsS3Service.uploadImg(file, user.getNickname())));
    }

    @PostMapping("/list")
    public ResponseEntity<Object> uploadImgList(@AuthenticationPrincipal User user, @RequestPart("file") List<MultipartFile> file) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이미지 리스트 업로드", awsS3Service.uploadImageList(file, user.getNickname())));
    }

}
