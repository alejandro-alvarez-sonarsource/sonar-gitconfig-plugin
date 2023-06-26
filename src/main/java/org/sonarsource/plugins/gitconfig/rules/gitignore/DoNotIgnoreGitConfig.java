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

package org.sonarsource.plugins.gitconfig.rules.gitignore;

import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.rule.RuleKey;
import org.sonar.check.Rule;
import org.sonarsource.plugins.gitconfig.GitIgnoreFile;
import org.sonarsource.plugins.gitconfig.languages.GitConfigLanguage;

@Rule(key = DoNotIgnoreGitConfig.RULE_KEY)
public class DoNotIgnoreGitConfig implements GitIgnoreRule {
  public static final String RULE_KEY = "GI01";

  @Override
  public void execute(SensorContext sensorContext, GitIgnoreFile gitIgnore, RuleKey ruleKey) {
    for (var fileName : GitConfigLanguage.SUFFIXES) {
      var line = gitIgnore.getLines().indexOf(fileName) + 1;
      if (line > 0) {
        NewIssue newIssue = sensorContext.newIssue().forRule(ruleKey);
        newIssue.at(newIssue.newLocation()
          .on(gitIgnore.getFile())
          .at(gitIgnore.getFile().selectLine(line))
          .message(String.format("Do not ignore %s", fileName))
        );
        newIssue.save();
      }
    }
  }
}
