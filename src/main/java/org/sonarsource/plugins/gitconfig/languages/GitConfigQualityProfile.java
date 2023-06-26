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

package org.sonarsource.plugins.gitconfig.languages;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonarsource.analyzer.commons.BuiltInQualityProfileJsonLoader;
import org.sonarsource.plugins.gitconfig.rules.GitConfigRuleDefinition;

/**
 * Default, BuiltIn Quality Profile for the projects having files of the language "foo"
 */
public final class GitConfigQualityProfile implements BuiltInQualityProfilesDefinition {

  @Override
  public void define(Context context) {
    NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("GitConfig Rules", GitConfigLanguage.KEY);
    BuiltInQualityProfileJsonLoader.load(profile, GitConfigRuleDefinition.REPO_KEY, GitConfigRuleDefinition.QUALITY_PROFILE);
    profile.setDefault(true);
    profile.done();
  }

}
