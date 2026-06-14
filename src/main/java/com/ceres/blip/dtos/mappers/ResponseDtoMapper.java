package com.ceres.blip.dtos.mappers;

import com.ceres.blip.dtos.ListResponseDto;
import com.ceres.blip.dtos.OperationReturnObject;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Function;
/**
 * * This class is responsible for mapping a Page of data to an OperationReturnObject containing a ListResponseDto.
 * * It implements the Function interface, allowing it to be used as a lambda or method reference where needed.
 **/
@Component
public class ResponseDtoMapper implements Function<Page<?>, OperationReturnObject> {
    @Override
    public OperationReturnObject apply(Page<?> page) {
        ListResponseDto listResponseDto = new ListResponseDto(page.getTotalElements(), page.getContent());
        return new OperationReturnObject(200, "Data fetched successfully", listResponseDto);
    }
}
