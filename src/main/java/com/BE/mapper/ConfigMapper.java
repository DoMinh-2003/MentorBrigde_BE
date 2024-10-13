package com.BE.mapper;

import com.BE.model.entity.Config;
import com.BE.model.request.ConfigRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigMapper {
    Config toConfig(ConfigRequest configRequest);
}
