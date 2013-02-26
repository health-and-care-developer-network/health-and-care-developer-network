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


		final String hdnGs1ClientConsoleEntryPoint = intellijModuleHasMainClassByConvention("barcodes-gs1-client-application", "HdnGs1ClientConsoleEntryPoint");

		final String hdnGs1ServerConsoleEntryPoint = intellijModuleHasMainClassByConvention("barcodes-gs1-server-application", "HdnGs1ServerConsoleEntryPoint");

		final String hdnDtsOutConsoleEntryPoint = intellijModuleHasMainClassByConvention("dts-client-out", "HdnDtsOutConsoleEntryPoint");

		final String hdnDtsReadConsoleEntryPoint = intellijModuleHasMainClassByConvention("dts-client-read", "HdnDtsReadConsoleEntryPoint");

		final String hdnDtsRatsConsoleEntryPoint = intellijModuleHasMainClassByConvention("dts-client-rats", "HdnDtsRatsConsoleEntryPoint");

		final String hdnNumberClientConsoleEntryPoint = intellijModuleHasMainClassByConvention("number-client", "HdnNumberClientConsoleEntryPoint");

		final String hdnDbsResponseConsoleEntryPoint = intellijModuleHasMainClassByConvention("dbs-response-client", "HdnDbsResponseConsoleEntryPoint");

		final String hdnDbsRequestConsoleEntryPoint = intellijModuleHasMainClassByConvention("dbs-request-client", "HdnDbsRequestConsoleEntryPoint");

		final String hdnCkanDatasetSearchConsoleEntryPoint = intellijModuleHasMainClassByConvention("ckan-client-dataset-search", "HdnCkanDatasetSearchConsoleEntryPoint");

		final String hdnCkanResourceSearchConsoleEntryPoint = intellijModuleHasMainClassByConvention("ckan-client-resource-search", "HdnCkanResourceSearchConsoleEntryPoint");

		final String hdnCkanDetailsConsoleEntryPoint = intellijModuleHasMainClassByConvention("ckan-client-details", "HdnCkanDetailsConsoleEntryPoint");

		final String hdnCkanListConsoleEntryPoint = intellijModuleHasMainClassByConvention("ckan-client-list", "HdnCkanListConsoleEntryPoint");

		final String hdnCkanQueryConsoleEntryPoint = intellijModuleHasMainClassByConvention("ckan-client-query", "HdnCkanQueryConsoleEntryPoint");

		final String hdnCkanRelationshipsConsoleEntryPoint = intellijModuleHasMainClassByConvention("ckan-client-relationships", "HdnCkanRelationshipsConsoleEntryPoint");

		task("clean").does
		(
			deleteDirectory(output())
		);

		task("make output").dependsOn("clean").does
		(
			makeDirectory(output())
		);

		// stick in generated source
		task("make version").dependsOn("make output").does
		(
			execute(source("build", "build").file("generate-version-string")).inWorkingDirectory(output()).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArgument("version")
		);

		intellijProject("subprojects", "make version", "build");

		compile();

		executable("hdn-gs1-client", "barcodes-gs1-client-application", hdnGs1ClientConsoleEntryPoint);

		executable("hdn-gs1-server", "barcodes-gs1-server-application", hdnGs1ServerConsoleEntryPoint);

		executable("hdn-dts-out", "dts-client-out", hdnDtsOutConsoleEntryPoint);

		executable("hdn-dts-read", "dts-client-read", hdnDtsReadConsoleEntryPoint);

		executable("hdn-dts-rats", "dts-client-rats", hdnDtsRatsConsoleEntryPoint);

		executable("hdn-number-client", "number-client", hdnNumberClientConsoleEntryPoint);

		executable("hdn-dbs-response", "dbs-response-client", hdnDbsResponseConsoleEntryPoint);

		executable("hdn-dbs-request", "dbs-request-client", hdnDbsRequestConsoleEntryPoint);

		executable("hdn-ckan-dataset-search", "ckan-client-dataset-search", hdnCkanDatasetSearchConsoleEntryPoint);

		executable("hdn-ckan-resource-search", "ckan-client-resource-search", hdnCkanResourceSearchConsoleEntryPoint);

		executable("hdn-ckan-details", "ckan-client-details", hdnCkanDetailsConsoleEntryPoint);

		executable("hdn-ckan-list", "ckan-client-list", hdnCkanListConsoleEntryPoint);

		executable("hdn-ckan-query", "ckan-client-query", hdnCkanQueryConsoleEntryPoint);

		executable("hdn-ckan-relationships", "ckan-client-relationships", hdnCkanRelationshipsConsoleEntryPoint);

		task("executables").dependsOn("hdn-gs1-client", "hdn-gs1-server", "hdn-dts-out", "hdn-dts-read", "hdn-dts-rats", "hdn-dbs-response", "hdn-dbs-request");

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

		debianPackagesPackageTask("hdn-reverse-proxy", "generate changelog template");

		debianPackagesPackageTask("hdn-ckan-client", "generate changelog template", "hdn-ckan-dataset-search", "hdn-ckan-resource-search", "hdn-ckan-details", "hdn-ckan-list", "hdn-ckan-query", "hdn-ckan-relationships");

		debianPackagesPackageTask("hdn-data", "generate changelog template");

		debianPackagesPackageTask("hdn-services", "generate changelog template");

		debianPackagesPackageTask("hdn-itk-samples", "generate changelog template");

		debianPackagesPackageTask("hdn-itk-documentation", "generate changelog template");

		debianPackagesPackageTask("hdn-avahi-daemon", "generate changelog template");

		debianPackagesPackageTask("hdn-4store", "generate changelog template");

		debianPackagesPackageTask("hdn-jetty", "generate changelog template");

		debianPackagesPackageTask("hdn-elda", "generate changelog template");

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
			execute(source("build", "build").file("create-insecure-apt-repository")).inWorkingDirectory(output()).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments("packages"),
			execute(source("build", "build").file("create-secure-apt-repository")).inWorkingDirectory(output()).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments("packages", "hdn")
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
