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

package uk.nhs.hdn.pseudonymisation.client;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.MessageDigestHelper;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.pseudonymisation.pseudonymisers.*;

public enum PseudonymisationAction
{
	Signed32BitSequence
	{
		@NotNull
		@Override
		public <N extends Normalisable> Pseudonymiser<N> pseudonymiser()
		{
			return new Signed32BitSequencePseudonymiser<>();
		}
	},
	Signed64BitSequence
	{
		@NotNull
		@Override
		public <N extends Normalisable> Pseudonymiser<N> pseudonymiser()
		{
			return new Signed64BitSequencePseudonymiser<>();
		}
	},
	UUID
	{
		@NotNull
		@Override
		public <N extends Normalisable> Pseudonymiser<N> pseudonymiser()
		{
			return new UuidPseudonymiser<>();
		}
	},
	Random8Bytes
	{
		@NotNull
		@Override
		public <N extends Normalisable> Pseudonymiser<N> pseudonymiser()
		{
			return new JavaNoQuiteSoSecureRandomNumberGeneratorPseudonymiser<>(8);
		}
	},
	SHA512
	{
		@NotNull
		@Override
		public <N extends Normalisable> Pseudonymiser<N> pseudonymiser()
		{
			return new HashPseudonymiser<>(MessageDigestHelper.SHA512);
		}
	},
	SHA512First4Bytes
	{
		@NotNull
		@Override
		public <N extends Normalisable> Pseudonymiser<N> pseudonymiser()
		{
			return new HashPseudonymiser<>(4, MessageDigestHelper.SHA512);
		}
	},
	SHA512First5Bytes
	{
		@NotNull
		@Override
		public <N extends Normalisable> Pseudonymiser<N> pseudonymiser()
		{
			return new HashPseudonymiser<>(5, MessageDigestHelper.SHA512);
		}
	},
	SHA512First6Bytes
	{
		@NotNull
		@Override
		public <N extends Normalisable> Pseudonymiser<N> pseudonymiser()
		{
			return new HashPseudonymiser<>(6, MessageDigestHelper.SHA512);
		}
	},
	SHA512First8Bytes
	{
		@NotNull
		@Override
		public <N extends Normalisable> Pseudonymiser<N> pseudonymiser()
		{
			return new HashPseudonymiser<>(7, MessageDigestHelper.SHA512);
		}
	},
	;

	@NotNull
	public abstract <N extends Normalisable> Pseudonymiser<N> pseudonymiser();
}
