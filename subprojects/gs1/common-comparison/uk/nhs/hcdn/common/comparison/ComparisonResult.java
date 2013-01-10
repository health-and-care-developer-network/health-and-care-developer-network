package uk.nhs.hcdn.common.comparison;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({PARAMETER, FIELD, METHOD, LOCAL_VARIABLE})
public @interface ComparisonResult
{
	int EqualTo = 0;
	int LessThan = -1;
	int GreaterThan = 1;
}
