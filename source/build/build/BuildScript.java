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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.softwarecraftsmen.orogeny.actions.CopyFilesAction.flatHardLinkFiles;
import static com.softwarecraftsmen.orogeny.actions.DeleteDirectoryAction.deleteDirectory;
import static com.softwarecraftsmen.orogeny.actions.ExecuteAction.TenMinutes;
import static com.softwarecraftsmen.orogeny.actions.ExecuteAction.execute;
import static com.softwarecraftsmen.orogeny.actions.JarTogetherAction.jarTogether;
import static com.softwarecraftsmen.orogeny.actions.MakeDirectoryAction.makeDirectory;
import static com.softwarecraftsmen.orogeny.actions.ZipTogetherAction.zipTogether;
import static com.softwarecraftsmen.orogeny.filing.FilesFilteredAbsolutePaths.filesFilteredAbsolutePaths;
import static com.softwarecraftsmen.orogeny.filing.findFileFilters.AbstractFindFilesFilter.Jar;
import static com.softwarecraftsmen.orogeny.filing.findFileFilters.AbstractFindFilesFilter.isInRoot;
import static com.softwarecraftsmen.orogeny.filing.findFileFilters.FileHasExtensionFindFilesFilter.fileHasExtension;
import static java.lang.System.arraycopy;

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


		final String barcodesClientConsoleEntryPoint = intellijModuleHasMainClassByConvention("barcodes-gs1-client-application", "Gs1BarcodesClientConsoleEntryPoint");

		final String barcodesServerConsoleEntryPoint = intellijModuleHasMainClassByConvention("barcodes-gs1-server-application", "Gs1BarcodesServerConsoleEntryPoint");

		final String dtsClientOutConsoleEntryPoint = intellijModuleHasMainClassByConvention("dts-client-out", "OutClientConsoleEntryPoint");

		final String dtsClientReadConsoleEntryPoint = intellijModuleHasMainClassByConvention("dts-client-read", "ReadClientConsoleEntryPoint");

		final String dtsClientRatsConsoleEntryPoint = intellijModuleHasMainClassByConvention("dts-client-rats", "RatsClientConsoleEntryPoint");


		task("clean").does
		(
			deleteDirectory(output())
		);

		task("make output").dependsOn("clean").does
		(
			makeDirectory(output())
		);

		intellijProject("subprojects", "make output", "build");

		compile();

		executable("hdn-gs1-client", "barcodes-gs1-client-application", barcodesClientConsoleEntryPoint);

		executable("hdn-gs1-server", "barcodes-gs1-server-application", barcodesServerConsoleEntryPoint);

		executable("hdn-dts-out", "dts-client-out", dtsClientOutConsoleEntryPoint);

		executable("hdn-dts-read", "dts-client-read", dtsClientReadConsoleEntryPoint);

		executable("hdn-dts-rats", "dts-client-rats", dtsClientRatsConsoleEntryPoint);

		task("executables").dependsOn("hdn-gs1-client", "hdn-gs1-server", "hdn-dts-out", "hdn-dts-read", "hdn-dts-rats");

		task("generate changelog template").dependsOn("make output").does
		(
			execute(source("build", "build").file("generate-changelog-template")).inWorkingDirectory(source("build")).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments()
		);

		debianPackagesPackageTask("hdn-gs1-client", "generate changelog template", "hdn-gs1-client");

		debianPackagesPackageTask("hdn-gs1-server", "generate changelog template", "hdn-gs1-server");

		debianPackagesPackageTask("hdn-dts-out", "generate changelog template", "hdn-dts-out");

		debianPackagesPackageTask("hdn-dts-read", "generate changelog template", "hdn-dts-read");

		debianPackagesPackageTask("hdn-dts-rats", "generate changelog template", "hdn-dts-rats");

		debianPackagesPackageTask("hdn-template", "generate changelog template");

		debianPackagesPackageTask("hdn-apt", "generate changelog template");

		debianPackagesPackageTask("hdn-sysctl", "generate changelog template");

		debianPackagesPackageTask("hdn-firewall", "generate changelog template");

		debianPackagesPackageTask("hdn-smtp", "generate changelog template");

		debianPackagesPackageTask("hdn-java-common", "generate changelog template");

		debianPackagesPackageTask("hdn-jstatd", "generate changelog template");

		debianNonRepositoryPackageTask("hdn-keyring-private", "generate changelog template");

		debianPackagesPackageTask("hdn-ssh-server", "generate changelog template");

		task("packages").dependsOn(debianPackagesPackageTasks).does
		(
			execute(source("build", "build").file("create-insecure-apt-repository")).inWorkingDirectory(output()).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments("packages")
		);
	}

	private String intellijModuleHasMainClassByConvention(final String moduleName, final String consoleEntryPoint)
	{
		return intellijModuleHasMainClass(moduleName, getPackageName(moduleName), consoleEntryPoint);
	}

	private static String getPackageName(@NotNull final String moduleName)
	{
		return "uk.nhs.hdn." + moduleName.replace("-", ".");
	}

	private void executable(@NotNull final String taskName, @NotNull final String moduleName, @NotNull final String consoleEntryPoint)
	{
		final FindFilesFilter isInLibrary = isInRoot(library());
		final FindFilesFilter dependentJarFilesExcludingLibraries = isInLibrary.not().and(Jar);

		final AbsoluteDirectory distributionFolder = output(taskName);

		task(taskName).dependsOn("compile " + moduleName).does
		(
			makeDirectory(distributionFolder),
			jarTogether(registeredPaths(moduleName)).capturing(dependentJarFilesExcludingLibraries).to(distributionFolder.file(taskName + "-without-depdencies.jar")).withClassPath(filesFilteredAbsolutePaths(registeredPaths(moduleName), isInLibrary)).withMainClass(consoleEntryPoint),
			flatHardLinkFiles(isInLibrary.and(Jar)).from(registeredPaths(moduleName)).to(distributionFolder),
			zipTogether(registeredPaths(moduleName + ".source.zip")).capturing(fileHasExtension("source.zip")).to(distributionFolder.file(taskName + ".source.zip")),
			jarTogether(registeredPaths(moduleName)).capturing(Jar).to(distributionFolder.file(taskName + ".jar")).withoutClassPath().withMainClass(consoleEntryPoint)
		);
	}

	private void debianPackagesPackageTask(@NotNull @NonNls final String packageName, @NotNull @NonNls final String... dependsOnTaskNames)
	{
		debianPackagesPackageTasks = debianPackageTaskInternal(debianPackagesPackageTasks, "packages", packageName, dependsOnTaskNames);
	}

	private void debianNonRepositoryPackageTask(@NotNull @NonNls final String packageName, @NotNull @NonNls final String... dependsOnTaskNames)
	{
		debianNonRepositoryPackageTasks = debianPackageTaskInternal(debianNonRepositoryPackageTasks, "non-repository", packageName, dependsOnTaskNames);
	}

	@NotNull
	private String[] debianPackageTaskInternal(@NotNull final String[] debianPackageTasks, @NotNull @NonNls final String packagesFolder, @NotNull final String packageName, @NotNull final String... dependsOnTaskNames)
	{
		@NonNls final String taskName = "package " + packageName;
		final int oldLength = debianPackageTasks.length;
		final String[] expandedDebianPackageTasks = new String[oldLength + 1];
		arraycopy(debianPackageTasks, 0, expandedDebianPackageTasks, 0, oldLength);
		expandedDebianPackageTasks[oldLength] = taskName;

		task(taskName).dependsOn(dependsOnTaskNames).does
		(
			execute(source("build", "build").file("create-debian-package")).inWorkingDirectory(output()).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments(packagesFolder, packageName)
		);

		return expandedDebianPackageTasks;
	}

	static
	{
		configure();
	}
}
