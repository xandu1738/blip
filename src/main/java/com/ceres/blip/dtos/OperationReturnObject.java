package com.ceres.blip.dtos;

public record OperationReturnObject(int returnCode, String returnMessage, Object returnObject) {
}
