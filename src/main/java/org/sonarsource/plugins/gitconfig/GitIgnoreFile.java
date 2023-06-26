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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.sonar.api.batch.fs.InputFile;

public class GitIgnoreFile {
  private final InputFile file;
  private final List<String> lines;

  public GitIgnoreFile(InputFile inputFile) {
    file = inputFile;
    lines = new ArrayList<>(file.lines());
    try (var reader = new BufferedReader(new InputStreamReader(file.inputStream()))) {
      while (reader.ready()) {
        lines.add(reader.readLine().strip());
      }
    } catch (IOException e) {
      throw new IllegalStateException(e.toString());
    }
  }

  public List<String> getLines() {
    return lines;
  }

  public InputFile getFile() {
    return file;
  }
}
