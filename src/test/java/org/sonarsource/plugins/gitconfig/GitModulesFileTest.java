package org.sonarsource.plugins.gitconfig;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;


public class GitModulesFileTest {

  @Test
  public void parseEmpty() {
    var gitModulesFile = new GitModulesFile(new MockInputFile(".gitmodules", new String[0]));
    assertNotNull(gitModulesFile.getFile());
    assertTrue(gitModulesFile.getSubmoduleList().isEmpty());
  }

  @Test
  public void parseExample() {
    var gitModulesFile = new GitModulesFile(new MockInputFile(".gitmodules", new String[]{
      "[submodule \"libfoo\"]",
      "\tpath = include/foo",
      "\turl=git://foo.com/git/lib.git",
      "\tignore=none",
      "\tshallow=true",
      "",
      "[submodule \"libbar\"]",
      "path = \"include/bar\"",
      "url = https://bar.com/git/lib.git",
      "update = checkout",
      "branch = v2.2",
      "fetchRecurseSubmodules = true",
      "foo = bar"
    }));
    assertNotNull(gitModulesFile.getFile());
    assertEquals(2, gitModulesFile.getSubmoduleList().size());
    // libfoo
    var libfoo = gitModulesFile.getSubmoduleList().get(0);
    assertEquals("libfoo", libfoo.getName().getValue());
    assertEquals("include/foo", libfoo.getPath().getValue());
    assertEquals("git://foo.com/git/lib.git", libfoo.getUrl().getValue());
    assertNull(libfoo.getUpdate());
    assertNull(libfoo.getBranch());
    assertNull(libfoo.getFetchRecurseSubmodules());
    assertEquals(GitModulesFile.Submodule.Ignore.NONE, libfoo.getIgnore().getValue());
    assertTrue(libfoo.getShallow().getValue());
    // libbar
    var libbar = gitModulesFile.getSubmoduleList().get(1);
    assertEquals("libbar", libbar.getName().getValue());
    assertEquals("include/bar", libbar.getPath().getValue());
    assertEquals("https://bar.com/git/lib.git", libbar.getUrl().getValue());
    assertEquals(GitModulesFile.Submodule.Update.CHECKOUT, libbar.getUpdate().getValue());
    assertEquals("v2.2", libbar.getBranch().getValue());
    assertTrue(libbar.getFetchRecurseSubmodules().getValue());
    assertNull(libbar.getIgnore());
    assertNull(libbar.getShallow());
  }
}
