package com.BE.mapper;

import com.BE.model.entity.Config;
import com.BE.model.request.ConfigRequest;
import com.BE.model.response.ConfigResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Duration;

@Mapper(componentModel = "spring")
public interface ConfigMapper {
    Config toConfig(ConfigRequest configRequest);

    void toUpdateConfig(@MappingTarget Config config, ConfigRequest configRequest);

    @Mapping(target = "minTimeSlotDuration", source = "minTimeSlotDuration", qualifiedByName = "durationToString")
    ConfigResponse toConfigResponse(Config config);

    @Named("durationToString")
    default String durationToString(Duration duration) {
        if (duration == null) {
            return null;
        }
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
}
