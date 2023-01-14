package ru.practicum.shareit.booking;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, Object> {

    private String start;
    private String end;

    public void initialize(StartBeforeEnd constraintAnnotation) {

        this.start = constraintAnnotation.start();
        this.end = constraintAnnotation.end();
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {

       String startValue = new BeanWrapperImpl(value)
                .getPropertyValue(start).toString();
       String endValue = new BeanWrapperImpl(value)
                .getPropertyValue(end).toString();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime s = LocalDateTime.parse(startValue, formatter);
        LocalDateTime e =LocalDateTime.parse(endValue, formatter);

        if (s != null && e != null) {
            return s.isBefore(e);
        } else {
            return false;
        }
    }
}
