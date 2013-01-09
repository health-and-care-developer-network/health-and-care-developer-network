package org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class LockToStringGenerator extends AbstractToStringGenerator<Lock>
{
	@NotNull
	public static final ToStringGenerator<?> LockToStringGeneratorInstance = new LockToStringGenerator();

	private LockToStringGenerator()
	{
		super(ReentrantReadWriteLock.ReadLock.class, ReentrantLock.class, ReentrantReadWriteLock.WriteLock.class);
	}

	@NotNull
	@Override
	public String toString(@NotNull final Lock value)
	{
		return value.getClass().getSimpleName();
	}
}
