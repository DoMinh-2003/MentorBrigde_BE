package com.BE.mapper;

import com.BE.model.entity.File;
import com.BE.model.response.FileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    FileResponse toFileResponse(File file);

}
