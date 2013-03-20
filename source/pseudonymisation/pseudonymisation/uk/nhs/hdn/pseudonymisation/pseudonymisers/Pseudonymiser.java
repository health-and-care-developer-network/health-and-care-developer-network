package uk.nhs.hdn.pseudonymisation.pseudonymisers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.pseudonymisation.IndexTable;

public interface Pseudonymiser<N extends Normalisable>
{
	int size();

	void pseudonymise(@Nullable final N valueToPsuedonymise, @NotNull final IndexTable<N> indexTable);

	boolean hasSalt();
}
