package org.sonarsource.plugins.gitconfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.annotation.CheckForNull;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.TextRange;
import org.sonarsource.plugins.gitconfig.languages.GitConfigLanguage;

public class MockInputFile implements InputFile {
  final File file;
  final String content;
  final int numberOfLines;

  public MockInputFile(String path, String[] lines, String lineEnd) {
    file = new File(path);
    numberOfLines = lines.length;
    content = String.join(lineEnd, lines);
  }

  public MockInputFile(String path, String[] lines) {
    this(path, lines, "\n");
  }

  @Override
  public String relativePath() {
    return file.getPath();
  }

  @Override
  public String absolutePath() {
    return file.getAbsolutePath();
  }

  @Override
  public File file() {
    return file;
  }

  @Override
  public Path path() {
    return file.toPath();
  }

  @Override
  public URI uri() {
    return file.toURI();
  }

  @Override
  public String filename() {
    return file.getName();
  }

  @CheckForNull
  @Override
  public String language() {
    return "GitConfig";
  }

  @Override
  public Type type() {
    return Type.MAIN;
  }

  @Override
  public InputStream inputStream() throws IOException {
    return new ByteArrayInputStream(content.getBytes());
  }

  @Override
  public String contents() throws IOException {
    return content;
  }

  @Override
  public Status status() {
    return Status.ADDED;
  }

  @Override
  public int lines() {
    return numberOfLines;
  }

  @Override
  public boolean isEmpty() {
    return numberOfLines == 0;
  }

  @Override
  public TextPointer newPointer(int i, int i1) {
    return null;
  }

  @Override
  public TextRange newRange(TextPointer textPointer, TextPointer textPointer1) {
    return null;
  }

  @Override
  public TextRange newRange(int i, int i1, int i2, int i3) {
    return null;
  }

  @Override
  public TextRange selectLine(int i) {
    return null;
  }

  @Override
  public Charset charset() {
    return StandardCharsets.UTF_8;
  }

  @Override
  public String key() {
    return GitConfigLanguage.KEY;
  }

  @Override
  public boolean isFile() {
    return true;
  }
}
