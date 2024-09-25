package com.BE.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumUtils {
    public static <E extends Enum<E>> String getValidEnumValues(Class<E> enumClass) {
        return Stream.of(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
