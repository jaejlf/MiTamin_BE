package great.job.mytamin.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static great.job.mytamin.global.exception.ErrorMap.FILE_EXTENTION_ERROR;
import static great.job.mytamin.global.exception.ErrorMap.FILE_UPLOAD_ERROR;

@RequiredArgsConstructor
@Service
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.url}")
    private String defaultUrl;

    private final AmazonS3 amazonS3;

    /*
    이미지 리스트 업로드
    */
    public List<String> uploadImageList(List<MultipartFile> uploadFiles, String uniq) {
        if(uploadFiles.isEmpty()) return null;
        List<String> uploadUrl = new ArrayList<>();
        for (MultipartFile uploadFile : uploadFiles) {
            uploadUrl.add(uploadImg(uploadFile, uniq));
        }
        return uploadUrl;
    }

    /*
    단일 이미지 업로드
    */
    public String uploadImg(MultipartFile file, String uniq) {
        if (file.isEmpty()) return null;

        // 확장자 체크
        String originFileName = file.getOriginalFilename();
        String ext = originFileName.substring(originFileName.lastIndexOf('.'));
        if (!ext.equals(".jpg") && !ext.equals(".png") && !ext.equals(".jpeg")) {
            throw new MytaminException(FILE_EXTENTION_ERROR);
        }

        // 업로드
        String fileName = uniq + "-" + UUID.randomUUID();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new MytaminException(FILE_UPLOAD_ERROR);
        }
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

}
