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

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MapSerialiser
{
	void writeProperty(@NonNls @NotNull String name, @NotNull String value) throws CouldNotWritePropertyException;

	void writeProperty(@NonNls @NotNull String name, @NotNull MapSerialisable value) throws CouldNotWritePropertyException;

	void writeProperty(@NonNls @NotNull String name, @NotNull ValueSerialisable value) throws CouldNotWritePropertyException;

	void writeProperty(@NonNls @NotNull String name, @Nullable Object value) throws CouldNotWritePropertyException;

	void writePropertyNull(@NonNls @NotNull String name) throws CouldNotWritePropertyException;
}
