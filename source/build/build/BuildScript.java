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

import java.util.ArrayList;
import java.util.List;

import static com.softwarecraftsmen.orogeny.actions.CopyFilesAction.flatHardLinkFiles;
import static com.softwarecraftsmen.orogeny.actions.DeleteDirectoryAction.deleteDirectory;
import static com.softwarecraftsmen.orogeny.actions.EchoAction.echo;
import static com.softwarecraftsmen.orogeny.actions.ExecuteAction.TenMinutes;
import static com.softwarecraftsmen.orogeny.actions.ExecuteAction.OneHour;
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

	private final List<String> executables = new ArrayList<>(10);

	{
		librarySubfolders("library");

		sourceSubfolders("source");

		outputSubfolders("release");

		packageTemplateSubFolders("source", "package-templates");

		// executable uses intellijModuleHasMainClassByConvention so must come before intellijProject

		executable("hdn-gs1-client", "barcodes-gs1-client-application", "HdnGs1ClientConsoleEntryPoint");

		executable("hdn-gs1-server", "barcodes-gs1-server-application", "HdnGs1ServerConsoleEntryPoint");

		executable("hdn-dts-out", "dts-client-out", "HdnDtsOutConsoleEntryPoint");

		executable("hdn-dts-read", "dts-client-read", "HdnDtsReadConsoleEntryPoint");

		executable("hdn-dts-rats", "dts-client-rats", "HdnDtsRatsConsoleEntryPoint");

		executable("hdn-number-client", "number-client", "HdnNumberClientConsoleEntryPoint");

		executable("hdn-dbs-response", "dbs-response-client", "HdnDbsResponseConsoleEntryPoint");

		executable("hdn-dbs-request", "dbs-request-client", "HdnDbsRequestConsoleEntryPoint");

		executable("hdn-ckan-dataset-search", "ckan-client-dataset-search", "HdnCkanDatasetSearchConsoleEntryPoint");

		executable("hdn-ckan-resource-search", "ckan-client-resource-search", "HdnCkanResourceSearchConsoleEntryPoint");

		executable("hdn-ckan-details", "ckan-client-details", "HdnCkanDetailsConsoleEntryPoint");

		executable("hdn-ckan-list", "ckan-client-list", "HdnCkanListConsoleEntryPoint");

		executable("hdn-ckan-query", "ckan-client-query", "HdnCkanQueryConsoleEntryPoint");

		executable("hdn-ckan-relationships", "ckan-client-relationships", "HdnCkanRelationshipsConsoleEntryPoint");

		task("clean").does
		(
			deleteDirectory(output())
		);

		task("make output").dependsOn("clean").does
		(
			makeDirectory(output())
		);

		task("prepare executables").dependsOn(allExecutablesPrepareTasks()).dependsOn("make output").does
		(
			echo("exists to ensure all executables have had version.txt generated and placed in source")
		);

		intellijProject("subprojects", "prepare executables", "build");

		compile();

		task("executables").dependsOn(allExecutables());

		task("generate changelog template").dependsOn("make output").does
		(
			execute(source("build", "build").file("generate-changelog-template")).inWorkingDirectory(source("build")).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments()
		);

		debianPackagesPackageTask("hdn-gs1-client", "generate changelog template", "hdn-gs1-client");

		debianPackagesPackageTask("hdn-gs1-server", "generate changelog template", "hdn-gs1-server");

		debianPackagesPackageTask("hdn-dts-out", "generate changelog template", "hdn-dts-out");

		debianPackagesPackageTask("hdn-dts-read", "generate changelog template", "hdn-dts-read");

		debianPackagesPackageTask("hdn-dts-rats", "generate changelog template", "hdn-dts-rats");

		debianPackagesPackageTask("hdn-number-client", "generate changelog template", "hdn-number-client");

		debianPackagesPackageTask("hdn-dbs-response", "generate changelog template", "hdn-dbs-response");

		debianPackagesPackageTask("hdn-dbs-request", "generate changelog template", "hdn-dbs-request");

		debianPackagesPackageTask("hdn-ckan-client", "generate changelog template", "hdn-ckan-dataset-search", "hdn-ckan-resource-search", "hdn-ckan-details", "hdn-ckan-list", "hdn-ckan-query", "hdn-ckan-relationships");

		debianPackagesPackageTask("hdn-reverse-proxy", "generate changelog template");

		debianPackagesPackageTask("hdn-data", "generate changelog template");

		debianPackagesPackageTask("hdn-services", "generate changelog template");

		debianPackagesPackageTask("hdn-itk-samples", "generate changelog template");

		debianPackagesPackageTask("hdn-itk-documentation", "generate changelog template");

		debianPackagesPackageTask("hdn-avahi-daemon", "generate changelog template");

		debianPackagesPackageTask("hdn-4store", "generate changelog template");

		debianPackagesPackageTask("hdn-jetty", "generate changelog template");

		debianPackagesPackageTask("hdn-elda", "generate changelog template");

		debianPackagesPackageTask("hdn-pubby", "generate changelog template");

		debianPackagesPackageTask("hdn-template", "generate changelog template");

		debianPackagesPackageTask("hdn-nginx", "generate changelog template");

		debianPackagesPackageTask("hdn-apt", "generate changelog template");

		debianPackagesPackageTask("hdn-sysctl", "generate changelog template");

		debianPackagesPackageTask("hdn-logging", "generate changelog template");

		debianPackagesPackageTask("hdn-firewall", "generate changelog template");

		debianPackagesPackageTask("hdn-smtp", "generate changelog template");

		debianPackagesPackageTask("hdn-java-common", "generate changelog template");

		debianPackagesPackageTask("hdn-jstatd", "generate changelog template");

		debianNonRepositoryPackageTask("hdn-keyring-private", "generate changelog template");

		debianPackagesPackageTask("hdn-ssh-server", "generate changelog template");

		task("packages").dependsOn(debianPackagesPackageTasks).does
		(
			execute(source("build", "build").file("create-insecure-apt-repository")).inWorkingDirectory(output()).forUpTo(OneHour).withInheritedEnvironmentVariables().withArguments("packages"),
			execute(source("build", "build").file("create-secure-apt-repository")).inWorkingDirectory(output()).forUpTo(OneHour).withInheritedEnvironmentVariables().withArguments("packages", "hdn")
		);
	}

	private String[] allExecutables() {return executables.toArray(new String[executables.size()]);}

	private String[] allExecutablesPrepareTasks()
	{
		final String[] allExecutablesPrepareTasks = new String[executables.size()];
		final int size = executables.size();
		for (int index = 0; index < size; index++)
		{
			allExecutablesPrepareTasks[index] = executablePrepareTaskName(executables.get(index));
		}
		return allExecutablesPrepareTasks;
	}

	private String intellijModuleHasMainClassByConvention(final String moduleName, final String consoleEntryPoint)
	{
		return intellijModuleHasMainClass(moduleName, getPackageName(moduleName), consoleEntryPoint);
	}

	private static String getPackageName(@NotNull final String moduleName)
	{
		return "uk.nhs.hdn." + moduleName.replace("-", ".");
	}

	private void executable(@NotNull final String taskName, @NotNull final String moduleName, @NotNull final String mainClassSimpleName)
	{
		@NotNull final String consoleEntryPoint = intellijModuleHasMainClassByConvention(moduleName, mainClassSimpleName);
		executables.add(taskName);

		final String[] folders = getPackageName(moduleName).split("\\.");
		final String[] generatedSourceFolders = new String[folders.length + 2];
		generatedSourceFolders[0] = moduleName;
		generatedSourceFolders[1] = "generatedSource";
		arraycopy(folders, 0, generatedSourceFolders, 2, folders.length);
		final AbsoluteDirectory generatedSourcePackageFolder = output(generatedSourceFolders);

		task(executablePrepareTaskName(taskName)).dependsOn("make output").does
		(
			makeDirectory(generatedSourcePackageFolder),
			execute(source("build", "build").file("generate-version-string")).inWorkingDirectory(generatedSourcePackageFolder).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArgument("version.txt")
		);

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

	private static String executablePrepareTaskName(final String taskName) {return "prepare " + taskName;}

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
			execute(source("build", "build").file("create-debian-package")).inWorkingDirectory(output()).forUpTo(OneHour).withInheritedEnvironmentVariables().withArguments(packagesFolder, packageName)
		);

		return expandedDebianPackageTasks;
	}

	static
	{
		configure();
	}
}
