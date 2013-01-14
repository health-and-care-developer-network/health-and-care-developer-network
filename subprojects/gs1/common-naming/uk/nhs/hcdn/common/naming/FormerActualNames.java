/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.naming;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface FormerActualNames
{
	@NotNull
	Set<String> formerActualNames();
}
