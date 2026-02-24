package com.ceres.blip.services;

import com.ceres.blip.models.database.ScheduleModel;
import com.ceres.blip.repositories.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleModel> getAllSchedulesByPartner(String partnerCode) {
        return scheduleRepository.findAllByPartnerCode(partnerCode);
    }

    public ScheduleModel saveSchedule(ScheduleModel schedule) {
        return scheduleRepository.save(schedule);
    }

    public Optional<ScheduleModel> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
