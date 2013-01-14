/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

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
