package com.ceres.blip.services;

import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.models.database.ModuleModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.repositories.ModuleRepository;
import com.ceres.blip.repositories.SubscriptionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
class ModulesServiceTest {

//    @Mock
//    private ModuleRepository moduleRepository;
//
//    @Mock
//    private SubscriptionRepository subscriptionRepository;
//
//    @InjectMocks
//    private ModulesService modulesService;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    void testAddModule_Success() {
//        // Setup mock for authenticated user in LocalUtilsService might be tricky if it's not easily mockable
//        // However, I'll focus on the core logic.
//        // Note: ModulesService extends LocalUtilsService which has authenticatedUser()
//        // For unit testing private/protected methods of parent class, I might need to spy or mock the parent behavior if possible.
//        // Assuming the base methods can be handled or mocked.
//    }
//
//    @Test
//    void testModulesList() {
//        Page<ModuleModel> page = new PageImpl<>(Collections.emptyList());
//        when(moduleRepository.findByArchivedFalse(any(PageRequest.class))).thenReturn(page);
//
//        OperationReturnObject result = modulesService.modulesList(0, 10);
//
//        assertEquals(200, result.getReturnCode());
//        verify(moduleRepository).findByArchivedFalse(any(PageRequest.class));
//    }
//
//    @Test
//    void testRemoveModule_Archives() {
//        Long id = 1L;
//        ModuleModel module = new ModuleModel();
//        module.setId(id);
//        module.setArchived(false);
//
//        when(moduleRepository.findById(id)).thenReturn(Optional.of(module));
//        when(moduleRepository.save(any(ModuleModel.class))).thenReturn(module);
//
//        OperationReturnObject result = modulesService.removeModule(id);
//
//        assertEquals(200, result.getReturnCode());
//        assertTrue(module.getArchived());
//        verify(moduleRepository).save(module);
//    }
}
