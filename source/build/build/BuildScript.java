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
import org.jetbrains.annotations.NotNull;

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


		final String barcodesClientModule = "barcodes-gs1-client-application";

		final String gs1BarcodesClientConsoleEntryPoint = intellijModuleHasMainClass(barcodesClientModule, "uk.nhs.hdn.barcodes.gs1.client.application", "Gs1BarcodesClientConsoleEntryPoint");

		final String barcodesServerModule = "barcodes-gs1-server-application";

		final String gs1BarcodesServerConsoleEntryPoint = intellijModuleHasMainClass(barcodesServerModule, "uk.nhs.hdn.barcodes.gs1.server.application", "Gs1BarcodesServerConsoleEntryPoint");




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

		executable("hdn-gs1-client", barcodesClientModule, gs1BarcodesClientConsoleEntryPoint);

		executable("hdn-gs1-server", barcodesServerModule, gs1BarcodesServerConsoleEntryPoint);

		task("executables").dependsOn("hdn-gs1-client", "hdn-gs1-server");

		/*
		task("generate changelog template").dependsOn("make output").does
		(
			ExecuteAction.execute(source("build").file("generate-changelog-template")).inWorkingDirectory(source("build")).forUpTo(TenMinutes).withInheritedEnvironmentVariables().withArguments()
		);

		debianPackagesPackageTask("stormmq-kernel", "generate changelog template");

		debianNonRepositoryPackageTask("stormmq-keyring-private", "generate changelog template");

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

	private void executable(@NotNull final String taskName, @NotNull final String barcodesClientModule, @NotNull final String consoleEntryPoint)
	{
		final FindFilesFilter isInLibrary = isInRoot(library());
		final FindFilesFilter dependentJarFilesExcludingLibraries = isInLibrary.not().and(Jar);

		final AbsoluteDirectory application = output(taskName);
		final AbsoluteDirectory applicationDistribution = application.subDirectory("distribution");
		final AbsoluteDirectory applicationJars = applicationDistribution.subDirectory("jars");

		task(taskName).dependsOn("compile " + barcodesClientModule).does
		(
			makeDirectory(applicationJars),
			jarTogether(registeredPaths(barcodesClientModule)).capturing(dependentJarFilesExcludingLibraries).to(applicationJars.file(taskName + ".jar")).withClassPath(filesFilteredAbsolutePaths(registeredPaths(barcodesClientModule), isInLibrary)).withMainClass(consoleEntryPoint),
			flatHardLinkFiles(isInLibrary.and(Jar)).from(registeredPaths(barcodesClientModule)).to(applicationJars),
			zipTogether(registeredPaths(barcodesClientModule + ".source.zip")).capturing(fileHasExtension("source.zip")).to(applicationJars.file(taskName + ".source.zip"))
			//flatHardLinkFiles(Jar).from(registeredPaths("nio-bootclasspath")).to(barcodesGs1ClientApplicationJars),
		);
	}
}
