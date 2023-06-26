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

import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;
import org.sonarsource.plugins.gitconfig.languages.GitConfigLanguage;

public final class GitConfigRuleDefinition implements RulesDefinition {

  public static final String RESOURCES = "org/sonarsource/plugins/gitconfig/rules";
  public static final String QUALITY_PROFILE = RESOURCES + "/profile.json";
  static final String KEY = "rules";
  public static final String REPO_KEY = GitConfigLanguage.KEY + "-" + KEY;
  private static final String REPO_NAME = GitConfigLanguage.KEY + "- " + KEY + " repo";
  public final SonarRuntime runtime;

  public GitConfigRuleDefinition(SonarRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(REPO_KEY, GitConfigLanguage.KEY).setName(REPO_NAME);

    RuleMetadataLoader metadataLoader = new RuleMetadataLoader(RESOURCES, QUALITY_PROFILE, runtime);
    metadataLoader.addRulesByAnnotatedClass(repository, GitIgnoreSensor.checks());
    metadataLoader.addRulesByAnnotatedClass(repository, GitModulesSensor.checks());

    repository.done();
  }
}
