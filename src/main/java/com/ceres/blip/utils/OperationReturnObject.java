package com.ceres.blip.utils;

public record OperationReturnObject(int returnCode, String returnMessage, Object returnObject) {
}
