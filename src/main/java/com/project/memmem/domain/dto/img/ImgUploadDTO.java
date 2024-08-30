package com.project.memmem.domain.dto.img;

import com.project.memmem.domain.entity.ImageEntity.ImageType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImgUploadDTO {

    private List<String> uploadUrls;
    private List<String> uploadKeys;
    private ImageType imageType;
}
