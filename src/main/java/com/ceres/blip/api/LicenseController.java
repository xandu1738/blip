package com.ceres.blip.api;

import com.ceres.blip.dtos.*;
import com.ceres.blip.models.database.LicenseKeyModel;
import com.ceres.blip.repositories.LicenseKeyRepository;
import com.ceres.blip.services.LicenseGenerationService;
import com.ceres.blip.services.LicenseValidationService;
import com.ceres.blip.utils.LicenseActivationResult;
import com.ceres.blip.utils.LicenseValidationResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/license")
@RequiredArgsConstructor
@Tag(name = "License", description = "License Management API")
public class LicenseController {
    private final LicenseKeyRepository licenseKeyRepository;

    private final LicenseGenerationService keyGeneratorService;

    private final LicenseValidationService validationService;

    /**
     * Generate new license keys
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerateLicenseResponse> generateLicenses(@RequestBody @Valid GenerateLicenseRequest request) {
        List<String> generatedKeys = keyGeneratorService.generateKeys(
                request.count(),
                request.productId()
        );

        List<LicenseKeyModel> savedLicenses = new ArrayList<>();
        for (String key : generatedKeys) {
            LicenseKeyModel license = new LicenseKeyModel();
            license.setLicenseKey(key);
            license.setProductId(request.productId());
            license.setPartnerCode(request.partnerCode());
            license.setExpiryDate(request.expiryDate());
            license.setMaxActivations(request.maxActivations());

            savedLicenses.add(licenseKeyRepository.save(license));
        }

        List<@Size(max = 100) @NotNull String> licenseKeys = savedLicenses.stream()
                .map(LicenseKeyModel::getLicenseKey)
                .toList();

        return ResponseEntity.ok(new GenerateLicenseResponse(savedLicenses.size(), licenseKeys));
    }

    /**
     * Validate license key
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidateLicenseResponse> validateLicense(@RequestBody @Valid ValidateLicenseRequest request) {
        LicenseValidationResult result = validationService.validateLicense(
                request.licenseKey(),
                new LicenseValidationRequest(request.hardwareId(), request.ipAddress()));

        ValidateLicenseResponse licenseResponse = new ValidateLicenseResponse(
                result.isValid(),
                result.getMessage(),
                result.isValid() ? result.getLicense().getProductId() : null,
                result.isValid() ? result.getLicense().getExpiryDate() : null);
        return ResponseEntity.ok(
                licenseResponse
        );
    }

    /**
     * Activate license
     */
    @PostMapping("/activate")
    public ResponseEntity<ActivateLicenseResponse> activateLicense(@RequestBody @Valid ActivateLicenseRequest request) {
        LicenseActivationResult result = validationService.activateLicense(
                request.licenseKey(),
                new LicenseActivationRequest(
                        request.hardwareId(),
                        request.ipAddress(),
                        request.userAgent()
                )
        );

        return ResponseEntity.ok(
                new ActivateLicenseResponse(
                        result.isSuccess(),
                        result.getMessage(),
                        result.isSuccess() ? result.getActivation().getId() : null)
        );
    }

    /**
     * Deactivate license
     */
    @PostMapping("/deactivate")
    public ResponseEntity<BaseResponse> deactivateLicense(@RequestBody @Valid DeactivateLicenseRequest request) {
        boolean success = validationService.deactivateLicense(
                request.licenseKey(),
                request.hardwareId()
        );

        return ResponseEntity.ok(new BaseResponse(success, success ? "License deactivated" : "Deactivation failed"));
    }

    /**
     * Get license details
     */
    @GetMapping("/{licenseKey}")
    public ResponseEntity<LicenseKeyModel> getLicenseDetails(@PathVariable String licenseKey) {
        Optional<LicenseKeyModel> optionalLicense = licenseKeyRepository.findByLicenseKey(licenseKey);
        if (optionalLicense.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LicenseKeyModel license = optionalLicense.get();
        return ResponseEntity.ok(license);
    }
}
