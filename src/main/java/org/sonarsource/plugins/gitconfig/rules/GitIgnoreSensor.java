/*
 * GitConfig analyzer :: Sonar Plugin
 * Copyright (c) 2023 SonarSource SA
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonarsource.plugins.gitconfig.rules;

import java.util.List;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.gitconfig.GitIgnoreFile;
import org.sonarsource.plugins.gitconfig.rules.gitignore.DoNotIgnoreGitConfig;
import org.sonarsource.plugins.gitconfig.rules.gitignore.GitIgnoreRule;

public class GitIgnoreSensor implements Sensor {

  private final Checks<GitIgnoreRule> checks;
  private final Logger log = Loggers.get(GitIgnoreSensor.class);

  public GitIgnoreSensor(CheckFactory checkFactory) {
    checks = checkFactory.create(GitConfigRuleDefinition.REPO_KEY);
    checks.addAnnotatedChecks(checks());
  }

  public static List<Class<?>> checks() {
    return List.of(DoNotIgnoreGitConfig.class);
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .name(".gitignore sensor")
      .processesFilesIndependently();
  }

  @Override
  public void execute(SensorContext sensorContext) {
    FilePredicates p = sensorContext.fileSystem().predicates();
    var inputFiles = sensorContext.fileSystem().inputFiles(p.and(p.matchesPathPattern("**/.gitignore")));
    for (InputFile inputFile : inputFiles) {
      log.info("Analyzing {}", inputFile.filename());
      var gitIgnore = new GitIgnoreFile(inputFile);
      checks.all().forEach(check -> check.execute(sensorContext, gitIgnore, checks.ruleKey(check)));
    }
  }
}
