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

package org.sonarsource.plugins.gitconfig.rules.gitmodules;

import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.rule.RuleKey;
import org.sonar.check.Rule;
import org.sonarsource.plugins.gitconfig.GitModulesFile;

@Rule(key = DoNotUseSshRemote.RULE_KEY)
public class DoNotUseSshRemote implements GitModulesRule {
  public static final String RULE_KEY = "GM01";

  @Override
  public void execute(SensorContext sensorContext, GitModulesFile gitModules, RuleKey ruleKey) {
    gitModules.getSubmoduleList().stream().filter(s -> !s.getUrl().getValue().startsWith("https://")).forEach(s -> {
      NewIssue newIssue = sensorContext.newIssue().forRule(ruleKey);
      var urlValue = s.getUrl().getValue();

      String message;
      if (urlValue.startsWith("/") || urlValue.startsWith("file:/")) {
        message = "Do not use a local repository";
      } else if (urlValue.startsWith("ssh://") || !urlValue.contains("://")) {
        message = "Do not use git over ssh";
      } else if (urlValue.startsWith("git://")) {
        message = "Do not use the git protocol";
      } else if (urlValue.startsWith("http://")) {
        message = "Do not use the http protocol";
      } else {
        message = "Unknown protocol";
      }
      message += " for the submodule " + s.getName().getValue();

      newIssue.at(newIssue.newLocation()
        .on(gitModules.getFile())
        .at(s.getUrl().getPosition())
        .message(message));

      newIssue.save();
    });
  }
}
