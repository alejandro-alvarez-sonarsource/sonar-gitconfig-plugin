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

package org.sonarsource.plugins.gitconfig;

import org.sonar.api.Plugin;
import org.sonarsource.plugins.gitconfig.languages.GitConfigLanguage;
import org.sonarsource.plugins.gitconfig.languages.GitConfigQualityProfile;
import org.sonarsource.plugins.gitconfig.rules.GitConfigRuleDefinition;
import org.sonarsource.plugins.gitconfig.rules.GitIgnoreSensor;
import org.sonarsource.plugins.gitconfig.rules.GitModulesSensor;
import org.sonarsource.plugins.gitconfig.settings.GitConfigLanguageProperties;

/**
 * This class is the entry point for all extensions. It is referenced in pom.xml.
 */
public class GitConfigPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtensions(GitConfigLanguage.class, GitConfigQualityProfile.class, GitConfigRuleDefinition.class);
    context.addExtensions(GitConfigLanguageProperties.getProperties());
    context.addExtensions(GitIgnoreSensor.class, GitModulesSensor.class);
  }
}
