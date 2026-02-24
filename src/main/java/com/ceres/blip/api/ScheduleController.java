package com.ceres.blip.api;

import com.ceres.blip.models.database.ScheduleModel;
import com.ceres.blip.services.ScheduleService;
import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.utils.LocalUtilsService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedules", description = "Schedules API")
public class ScheduleController extends LocalUtilsService {
    private final ScheduleService scheduleService;

    @PostMapping("/add")
    public ResponseEntity<OperationReturnObject> addSchedule(@RequestBody JsonNode object) {
        JsonNode data = getRequestData(object);
        requires(data, "route_id", "days_of_week", "set_off_time", "expected_arrival_time");

        ScheduleModel schedule = new ScheduleModel();
        schedule.setRouteId(data.get("route_id").asLong());
        JsonNode daysOfWeek = data.get("days_of_week");

        if (daysOfWeek != null && daysOfWeek.isTextual()){
            String value = daysOfWeek.asText(); // "MON,TUE,WED,THU,FRI"
            String[] daysArray = value.split("\\s*,\\s*");
            schedule.setDaysOfWeek(daysArray);
        }

        schedule.setSetOffTime(LocalTime.parse(data.get("set_off_time").asText()));
        schedule.setExpectedArrivalTime(LocalTime.parse(data.get("expected_arrival_time").asText()));
        schedule.setPartnerCode(authenticatedUser().getPartnerCode());
        schedule.setStatus("ACTIVE");
        schedule.setCreatedAt(getCurrentTimestamp());
        schedule.setCreatedBy(authenticatedUser().getId());

        return ResponseEntity.ok(
                new OperationReturnObject(200, "Schedule added successfully", scheduleService.saveSchedule(schedule)));
    }

    @GetMapping("/list")
    public ResponseEntity<OperationReturnObject> listSchedules() {
        List<ScheduleModel> schedules = scheduleService.getAllSchedulesByPartner(authenticatedUser().getPartnerCode());
        return ResponseEntity.ok(new OperationReturnObject(200, "Schedules fetched successfully", schedules));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<OperationReturnObject> removeSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(new OperationReturnObject(200, "Schedule removed successfully", null));
    }
}
