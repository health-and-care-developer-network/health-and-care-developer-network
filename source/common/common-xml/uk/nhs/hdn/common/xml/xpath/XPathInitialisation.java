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

package uk.nhs.hdn.common.xml.xpath;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.System.setProperty;

public final class XPathInitialisation
{
	@SuppressWarnings("DuplicateStringLiteralInspection")
	@NonNls
	@NotNull
	private static final String JavaXmlXPathFactoryProperty = "javax.xml.xpath.XPathFactory";

	@SuppressWarnings("DuplicateStringLiteralInspection")
	@NonNls
	@NotNull
	private static final String SaxonXPathClassName = "net.sf.saxon.xpath.XPathFactoryImpl";

	public static void useSaxonForXPath()
	{
		configureXPathFactory(SaxonXPathClassName);
	}

	public static void configureXPathFactory(@NotNull final String fullClassNameWhichMustBeOnRuntimeClassPath)
	{
		setProperty(JavaXmlXPathFactoryProperty, fullClassNameWhichMustBeOnRuntimeClassPath);
	}

	private XPathInitialisation()
	{
	}
}
