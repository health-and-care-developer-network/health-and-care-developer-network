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

package uk.nhs.hdn.crds.registry.server.eventObservers;

import org.jetbrains.annotations.NotNull;

public final class DoNothingEventObserver<K> extends AbstractEventObserver<K>
{
	@NotNull private static final EventObserver<?> DoNothing = new DoNothingEventObserver();

	@SuppressWarnings({"unchecked", "MethodNamesDifferingOnlyByCase"})
	@NotNull
	public static <K> EventObserver<K> doNothingRepositoryEventObserver()
	{
		return (EventObserver<K>) DoNothing;
	}

	private DoNothingEventObserver()
	{
	}

	@Override
	public void storeChanged(@NotNull final K key)
	{
	}
}
