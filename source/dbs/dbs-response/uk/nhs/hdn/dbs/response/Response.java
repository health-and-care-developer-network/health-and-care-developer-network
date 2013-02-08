/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.dbs.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.util.Arrays;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;

public final class Response extends AbstractToString
{
	@NotNull
	public final ResponseHeader responseHeader;
	@NotNull
	public final ResponseBody[] responseBodies;

	public Response(@NotNull final ResponseHeader responseHeader, @NotNull final ResponseBody... responseBodies)
	{
		this.responseHeader = responseHeader;
		this.responseBodies = copyOf(responseBodies);
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final Response response = (Response) obj;

		if (!Arrays.equals(responseBodies, response.responseBodies))
		{
			return false;
		}
		if (!responseHeader.equals(response.responseHeader))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = responseHeader.hashCode();
		result = 31 * result + Arrays.hashCode(responseBodies);
		return result;
	}
}
