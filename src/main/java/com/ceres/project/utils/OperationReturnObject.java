package com.ceres.project.utils;

public record OperationReturnObject(int returnCode, String returnMessage, Object returnObject) {
}
