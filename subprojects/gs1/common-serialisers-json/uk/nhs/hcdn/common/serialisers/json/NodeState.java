/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers.json;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

public final class NodeState extends AbstractToString
{
	@NotNull
	private final NodeValue nodeValue;
	public boolean subsequentProperty;

	public NodeState(@NotNull final NodeValue nodeValue)
	{
		this.nodeValue = nodeValue;
		subsequentProperty = false;
	}
}
