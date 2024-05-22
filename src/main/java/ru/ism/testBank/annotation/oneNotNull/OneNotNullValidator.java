package ru.ism.testBank.annotation.oneNotNull;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OneNotNullValidator implements ConstraintValidator<OneNotNull, Object> {
    private String[] fields;

    @Override
    public void initialize(final OneNotNull combinedNotNull) {
        fields = combinedNotNull.fields();
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(obj);

        for (final String f : fields) {
            final Object fieldValue = beanWrapper.getPropertyValue(f);


            if (fieldValue != null) {
                return true;
            }
        }

        return false;

    }
}