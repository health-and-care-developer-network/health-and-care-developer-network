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

import com.softwarecraftsmen.orogeny.AbstractIntelliJConvenientBuildScript;
import com.softwarecraftsmen.orogeny.BaseDirectory;
import com.softwarecraftsmen.orogeny.TasksExecuteInParallel;
import com.softwarecraftsmen.orogeny.UpperCaseEnvironmentVariableOnWindows;
import com.softwarecraftsmen.orogeny.filing.AbsoluteDirectory;
import com.softwarecraftsmen.orogeny.filing.findFileFilters.FindFilesFilter;

import static com.softwarecraftsmen.orogeny.actions.CopyFilesAction.flatHardLinkFiles;
import static com.softwarecraftsmen.orogeny.actions.DeleteDirectoryAction.deleteDirectory;
import static com.softwarecraftsmen.orogeny.actions.JarTogetherAction.jarTogether;
import static com.softwarecraftsmen.orogeny.actions.MakeDirectoryAction.makeDirectory;
import static com.softwarecraftsmen.orogeny.actions.ZipTogetherAction.zipTogether;
import static com.softwarecraftsmen.orogeny.filing.FilesFilteredAbsolutePaths.filesFilteredAbsolutePaths;
import static com.softwarecraftsmen.orogeny.filing.findFileFilters.AbstractFindFilesFilter.*;
import static com.softwarecraftsmen.orogeny.filing.findFileFilters.FileHasExtensionFindFilesFilter.fileHasExtension;

@SuppressWarnings({"ClassWithoutPackageStatement", "HardCodedStringLiteral", "ClassNameSameAsAncestorName", "DuplicateStringLiteralInspection", "ZeroLengthArrayAllocation", "StringConcatenation"})
@BaseDirectory("../../health-and-care-developer-network")
@UpperCaseEnvironmentVariableOnWindows("PATH")
@TasksExecuteInParallel(true)
public final class BuildScript extends AbstractIntelliJConvenientBuildScript
{
	private String[] debianPackagesPackageTasks = {};

	private String[] debianNonRepositoryPackageTasks = {};

	{
		librarySubfolders("library");

		sourceSubfolders("source");

		outputSubfolders("release");

		packageTemplateSubFolders("source", "package-templates");

		intellijLibrary("IntelliJ Annotations", library("intellij-annotations", "unversioned").file("annotations.jar"));

		intellijLibrary("JOpt Simple", library("jopt-simple", "4.4").file("jopt-simple-4.4.jar"));

		intellijLibrary("Orogeny", library("orogeny").file("jna.jar").and(library("orogeny").file("orogeny-without-dependencies.jar")));

		intellijLibrary("JUnit 4", library("junit", "4.11").file("junit-4.11.jar"));




		intellijModuleHasMainClass("barcodes-gs1-client-application", "uk.nhs.hdn.barcodes.gs1.client.application" + "Gs1BarcodesClientConsoleEntryPoint");

		intellijModuleHasMainClass("barcodes-gs1-server-application", "uk.nhs.hdn.barcodes.gs1.server.application" + "Gs1BarcodesServerConsoleEntryPoint");




		task("clean").does
		(
			deleteDirectory(output())
		);

		task("make output").dependsOn("clean").does
		(
			makeDirectory(output())
		);

		intellijProject("subprojects", ".");

		compile();

		final FindFilesFilter isInLibrary = isInRoot(library());
		final FindFilesFilter dependentJarFilesExcludingLibraries = isInLibrary.not().and(Jar);

		final AbsoluteDirectory barcodesGs1ClientApplication = output("client-java");
		final AbsoluteDirectory barcodesGs1ClientApplicationDistribution = barcodesGs1ClientApplication.subDirectory("distribution");
		final AbsoluteDirectory barcodesGs1ClientApplicationJars = barcodesGs1ClientApplicationDistribution.subDirectory("jars");
		task("client-java").dependsOn("compile " + "barcodes-gs1-client-application").does
		(
			makeDirectory(barcodesGs1ClientApplicationJars),
			jarTogether(registeredPaths("barcodes-gs1-client-application")).capturing(dependentJarFilesExcludingLibraries).to(barcodesGs1ClientApplicationJars.file("xxx" + ".jar")).withClassPath(filesFilteredAbsolutePaths(registeredPaths("barcodes-gs1-client-application"), isInLibrary)).withMainClass("uk.nhs.hdn.barcodes.gs1.client.application" + "Gs1BarcodesClientConsoleEntryPoint"),
			flatHardLinkFiles(isInLibrary.and(Jar)).from(registeredPaths("barcodes-gs1-client-application")).to(barcodesGs1ClientApplicationJars),
			zipTogether(registeredPaths("barcodes-gs1-client-application" + ".source.zip")).capturing(fileHasExtension("source.zip")).to(barcodesGs1ClientApplicationJars.file("xxx" + ".source.zip"))
			//flatHardLinkFiles(Jar).from(registeredPaths("nio-bootclasspath")).to(barcodesGs1ClientApplicationJars),
		);

		/*
		task("generate changelog template").dependsOn("make output").does
		(
			ExecuteAction.execute(source("build").file("generate-changelog-template")).inWorkingDirectory(source("build")).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments()
		);

		debianPackagesPackageTask("stormmq-apt", "generate changelog template");

		debianPackagesPackageTask("stormmq-broker", "compile amqp-server-application", "compile amqp-administrator-application", "compile jmx-agent");

		debianPackagesPackageTask("stormmq-client-java", "client-java");

		debianPackagesPackageTask("stormmq-client-java-source", "client-java");

		debianPackagesPackageTask("stormmq-date", "generate changelog template");

		debianPackagesPackageTask("stormmq-debugging", "generate changelog template");

		debianPackagesPackageTask("stormmq-firewall", "generate changelog template");

		debianPackagesPackageTask("stormmq-generate-password", "generate changelog template");

		debianPackagesPackageTask("stormmq-harden", "generate changelog template");

		debianPackagesPackageTask("stormmq-hardware", "generate changelog template");

		debianPackagesPackageTask("stormmq-ipmitool", "generate changelog template");

		debianPackagesPackageTask("stormmq-ipsec", "generate changelog template");

		debianPackagesPackageTask("stormmq-java-common", "generate changelog template");

		debianPackagesPackageTask("stormmq-java6-6.30", "make output");

		debianPackagesPackageTask("stormmq-jstatd", "generate changelog template");

		debianPackagesPackageTask("stormmq-kernel", "generate changelog template");

		debianNonRepositoryPackageTask("stormmq-keyring-private", "generate changelog template");

		debianPackagesPackageTask("stormmq-keyring-public", "generate changelog template");

		debianPackagesPackageTask("stormmq-locale", "generate changelog template");

		debianPackagesPackageTask("stormmq-logging", "generate changelog template");

		debianPackagesPackageTask("stormmq-minimal", "generate changelog template");

		debianPackagesPackageTask("stormmq-nginx", "make output");

		debianPackagesPackageTask("stormmq-nginx-website", "make output");

		debianPackagesPackageTask("stormmq-pxeboot", "generate changelog template");

		debianPackagesPackageTask("stormmq-repository", "generate changelog template");

		debianPackagesPackageTask("stormmq-required", "generate changelog template");

		debianPackagesPackageTask("stormmq-smtp", "generate changelog template");

		debianPackagesPackageTask("stormmq-ssh-client", "generate changelog template");

		debianPackagesPackageTask("stormmq-ssh-server", "generate changelog template");

		debianPackagesPackageTask("stormmq-ssh-server-denyhosts", "generate changelog template");

		debianPackagesPackageTask("stormmq-sysctl", "generate changelog template");

		debianPackagesPackageTask("stormmq-template", "generate changelog template");

		debianPackagesPackageTask("stormmq-tty", "generate changelog template");

		task("package packages").dependsOn(debianPackagesPackageTasks).does
		(
			ExecuteAction.execute(source("build").file("create-insecure-apt-repository")).inWorkingDirectory(output()).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments("packages")
		);

		task("package servers").dependsOn("generate changelog template").does
		(
			ExecuteAction.execute(source("build").file("create-server-packages")).inWorkingDirectory(output()).forUpTo(TenMinutes).withInheritedEnvironmentVariables(),
			ExecuteAction.execute(source("build").file("create-insecure-apt-repository")).inWorkingDirectory(output()).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments("servers")
		);

		task("package non-repository").dependsOn(debianNonRepositoryPackageTasks).does
		(
			ExecuteAction.execute(source("build").file("create-insecure-apt-repository")).inWorkingDirectory(output()).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments("non-repository")
		);

		task("all").dependsOn("package packages", "package servers", "package non-repository").does
		(
			echo("execute source/build/rsync-build-to-stormmq-repository-queues")
		);
		*/
	}
}
