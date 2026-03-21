package com.ceres.blip.annotations.handler;

import com.ceres.blip.annotations.base.Requires;
import com.fasterxml.jackson.databind.JsonNode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequiresHandler {
    @Before("@annotation(requires)")
    public void requires(JoinPoint jointPoint, Requires requires) {
        // 1. Get the fields defined in the annotation
        String[] fields = requires.fields();

        // 2. Get the method args and get the first arg which must be the request object
        Object[] args = jointPoint.getArgs();

        // 3. Check if the first arg is a JsonNode
        if (args.length == 0){
            throw new IllegalArgumentException("At least one JsonNode argument must be provided!");
        }

        Object arg = args[0];
        if (!(arg instanceof JsonNode request)) {
            throw new IllegalArgumentException("First argument must be a JsonNode!");
        }

        for (String field : fields) {
            if (!request.has(field) || request.get(field) == null) {
                throw new IllegalArgumentException(field.replace("_", " ") + " must be provided!");
            }
        }
    }
}
