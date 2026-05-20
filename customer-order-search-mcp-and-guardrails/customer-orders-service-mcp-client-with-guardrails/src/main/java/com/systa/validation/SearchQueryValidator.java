package com.systa.validation;

import com.systa.exception.GuardrailViolationException;
import com.systa.service.QueryValidationService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class SearchQueryValidator implements ConstraintValidator<ValidSearchQuery, String> {

    private final QueryValidationService queryValidationService;

    public SearchQueryValidator(QueryValidationService queryValidationService) {
        this.queryValidationService = queryValidationService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            queryValidationService.validate(value);
            return true;
        } catch (GuardrailViolationException ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ex.getMessage()).addConstraintViolation();
            return false;
        }
    }
}
