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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;


public class GitModulesFile {
  private final List<Submodule> submoduleList = new ArrayList<>();
  private final InputFile file;

  public GitModulesFile(InputFile inputFile) {
    file = inputFile;
    try {
      var parser = new GitModulesParser(inputFile);
      parser.parse(submoduleList);
    } catch (IOException e) {
      throw new IllegalStateException(e.toString());
    }
  }

  public InputFile getFile() {
    return file;
  }

  public List<Submodule> getSubmoduleList() {
    return submoduleList;
  }

  public static class Fragment<T> {
    private final T value;
    private final TextRange position;

    public Fragment(TextRange position, T value) {
      this.position = position;
      this.value = value;
    }

    @Override
    public String toString() {
      return value.toString();
    }

    public TextRange getPosition() {
      return position;
    }

    public T getValue() {
      return value;
    }
  }


  // https://git-scm.com/docs/gitmodules
  public static class Submodule {
    private Fragment<String> name;
    private Fragment<String> path;
    private Fragment<String> url;
    private Fragment<Update> update;
    private Fragment<String> branch;
    private Fragment<Boolean> fetchRecurseSubmodules;
    private Fragment<Ignore> ignore;
    private Fragment<Boolean> shallow;

    @Override
    public String toString() {
      return String.format(
        "[%s]\tpath=%s%n\turl=%s%n\tupdate=%s%n\tbranch=%s%n\trecurse=%s%n\tignore=%s%n\tshallow=%s",
        name, path, url, update, branch, fetchRecurseSubmodules, ignore, shallow);
    }

    public Fragment<Boolean> getShallow() {
      return shallow;
    }

    public Fragment<Ignore> getIgnore() {
      return ignore;
    }

    public Fragment<Boolean> getFetchRecurseSubmodules() {
      return fetchRecurseSubmodules;
    }

    public Fragment<String> getBranch() {
      return branch;
    }

    public Fragment<Update> getUpdate() {
      return update;
    }

    public Fragment<String> getName() {
      return name;
    }

    public Fragment<String> getPath() {
      return path;
    }

    public Fragment<String> getUrl() {
      return url;
    }

    public enum Update {
      CHECKOUT, REBASE, MERGE, NONE
    }

    public enum Ignore {
      ALL, DIRTY, UNTRACKED, NONE
    }
  }

  private static class GitModulesParser {
    InputFile file;
    StreamTokenizer tokenizer;

    GitModulesParser(InputFile inputFile) throws IOException {
      file = inputFile;
      tokenizer = new StreamTokenizer(new InputStreamReader(file.inputStream()));
      tokenizer.quoteChar('"');
      tokenizer.eolIsSignificant(true);
      tokenizer.commentChar('#');
      tokenizer.ordinaryChar('/');
      tokenizer.ordinaryChar('\\');
      tokenizer.slashStarComments(false);
      tokenizer.slashSlashComments(false);
    }

    private void parse(List<Submodule> submoduleList) throws IOException {
      Submodule submodule = null;
      for (int tok = tokenizer.nextToken(); tok != StreamTokenizer.TT_EOF; tok = tokenizer.nextToken()) {
        switch (tok) {
          case '[':
            if (submodule != null) {
              submoduleList.add(submodule);
            }
            submodule = new Submodule();
            submodule.name = parseSubmoduleName();
            break;
          case StreamTokenizer.TT_WORD:
            parseSubmoduleAttribute(submodule);
            break;
          default:
            break;
        }
      }
      if (submodule != null) {
        submoduleList.add(submodule);
      }
    }

    private String consume(int... tok) throws IOException {
      int nextTok = tokenizer.nextToken();
      if (Arrays.stream(tok).noneMatch(t -> t == nextTok)) {
        throw new IllegalStateException(String.format("Expected one of %s, got %d", Arrays.toString(tok), nextTok));
      }
      if (tokenizer.sval == null)
        return null;
      return tokenizer.sval.strip();
    }

    private void consume(String word) throws IOException {
      String nextWord = consume(StreamTokenizer.TT_WORD);
      if (!Objects.equals(nextWord, word)) {
        throw new IllegalStateException(String.format("Expected word %s, got %s", word, nextWord));
      }
    }

    private String consumeUntilEOL() throws IOException {
      var builder = new StringBuilder();
      for (int tok = tokenizer.nextToken(); tok != StreamTokenizer.TT_EOL && tok != StreamTokenizer.TT_EOF; tok = tokenizer.nextToken()) {
        if (tok == StreamTokenizer.TT_WORD || tok == '"') {
          builder.append(tokenizer.sval);
        } else {
          builder.append((char) tok);
        }
      }
      return builder.toString();
    }

    private Fragment<String> parseSubmoduleName() throws IOException {
      consume("submodule");
      var submoduleName = consume(StreamTokenizer.TT_WORD, '"');
      consume(']');
      return new Fragment<>(file.selectLine(tokenizer.lineno()), submoduleName);
    }

    private void parseSubmoduleAttribute(Submodule submodule) throws IOException {
      assert submodule != null;
      var attr = tokenizer.sval;
      consume('=');
      var location = file.selectLine(tokenizer.lineno());
      var value = consumeUntilEOL();
      switch (attr) {
        case "path":
          submodule.path = new Fragment<>(location, value);
          break;
        case "url":
          submodule.url = new Fragment<>(location, value);
          break;
        case "update":
          submodule.update = new Fragment<>(location, Submodule.Update.valueOf(value.toUpperCase(Locale.ROOT)));
          break;
        case "branch":
          submodule.branch = new Fragment<>(location, value);
          break;
        case "fetchRecurseSubmodules":
          submodule.fetchRecurseSubmodules = new Fragment<>(location, Boolean.parseBoolean(value));
          break;
        case "ignore":
          submodule.ignore = new Fragment<>(location, Submodule.Ignore.valueOf(value.toUpperCase(Locale.ROOT)));
          break;
        case "shallow":
          submodule.shallow = new Fragment<>(location, Boolean.parseBoolean(value));
          break;
        default:
          break;
      }
    }
  }
}
