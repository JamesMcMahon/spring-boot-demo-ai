package sh.jfm.springbootdemos.aiagent;

import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GitToolsTest {

    @TempDir
    private Path tempDir;

    @Test
    void constructor_throwsExceptionWhenBasePathIsNotDirectory() throws Exception {
        var nonDirectory = Files.createFile(tempDir.resolve("someFile.txt"));

        assertThatThrownBy(() -> new GitTools(nonDirectory.toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Base path %s is not a directory".formatted(nonDirectory));
    }

    @Test
    void getRepos_returnsImmediateGitRepositoriesOnly() throws Exception {
        Files.createDirectories(tempDir.resolve("repo1").resolve(".git"));
        Files.createDirectories(tempDir.resolve("repo2").resolve(".git"));
        Files.createDirectories(tempDir.resolve("notRepo"));

        var repos = new GitTools(tempDir.toString()).getRepos();

        assertThat(repos).containsExactlyInAnyOrder(
                "repo1",
                "repo2"
        );
    }

    @Test
    void getRepos_ignoresNestedGitRepositories() throws Exception {
        var nestedParent = Files.createDirectories(tempDir.resolve("parent"));
        Files.createDirectories(nestedParent.resolve("nestedRepo").resolve(".git"));

        var repos = new GitTools(tempDir.toString()).getRepos();

        assertThat(repos).isEmpty();
    }

    @Test
    void getLog_returnsOrderedCommitEntries() throws Exception {
        // create a git repo with two commits
        Path repoDir = Files.createDirectories(tempDir.resolve("repo1"));
        try (Git git = Git.init().setDirectory(repoDir.toFile()).call()) {
            Path file = Files.writeString(repoDir.resolve("file.txt"), "v1");
            git.add().addFilepattern("file.txt").call();
            git.commit().setMessage("first commit").setAuthor("Alice", "alice@example.com").call();

            Files.writeString(file, "v2");
            git.add().addFilepattern("file.txt").call();
            git.commit().setMessage("second commit").setAuthor("Bob", "bob@example.com").call();
        }

        var logEntries = new GitTools(tempDir.toString()).getLog("repo1", -1);   // -1 → all commits

        assertThat(logEntries).hasSize(2);

        assertThat(logEntries.getFirst().author()).isEqualTo("Bob");
        assertThat(logEntries.getFirst().message()).isEqualTo("second commit");
        assertThat(logEntries.getFirst().date()).isNotBlank();

        assertThat(logEntries.getLast().author()).isEqualTo("Alice");
        assertThat(logEntries.getLast().message()).isEqualTo("first commit");
        assertThat(OffsetDateTime.parse(logEntries.getLast().date()))
                .isBeforeOrEqualTo(OffsetDateTime.parse(logEntries.getFirst().date()));
    }

    @Test
    void getLog_throwsWhenDirectoryIsNotGitRepo() throws Exception {
        Files.createDirectories(tempDir.resolve("notRepo"));

        assertThatThrownBy(() -> new GitTools(tempDir.toString()).getLog("notRepo", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a git repository");
    }

    @Test
    void getLog_limitsNumberOfCommitEntries() throws Exception {
        var repoDir = Files.createDirectories(tempDir.resolve("repoLimit"));
        try (Git git = Git.init().setDirectory(repoDir.toFile()).call()) {
            var file = Files.writeString(repoDir.resolve("file.txt"), "v1");
            git.add().addFilepattern("file.txt").call();
            git.commit().setMessage("c1").setAuthor("A", "a@example.com").call();

            Files.writeString(file, "v2");
            git.add().addFilepattern("file.txt").call();
            git.commit().setMessage("c2").setAuthor("B", "b@example.com").call();
        }

        var logEntries = new GitTools(tempDir.toString()).getLog("repoLimit", 1);

        assertThat(logEntries).hasSize(1);
        assertThat(logEntries.getLast().message()).isEqualTo("c2");
    }

    @Test
    void getStatus_reportsAllStatusTypes() throws Exception {
        Path repoDir = Files.createDirectories(tempDir.resolve("repoStatus"));
        try (Git git = Git.init().setDirectory(repoDir.toFile()).call()) {

            // ── initial commit with four tracked files ──────────────────────────
            Path fileA = Files.writeString(repoDir.resolve("fileA.txt"), "v1");
            Path fileC = Files.writeString(repoDir.resolve("fileC.txt"), "v1");
            Path fileD = Files.writeString(repoDir.resolve("fileD.txt"), "v1");
            Path fileE = Files.writeString(repoDir.resolve("fileE.txt"), "v1");

            git.add().addFilepattern(".").call();
            git.commit().setMessage("initial").setAuthor("Alice", "a@x").call();

            // modified  (working-tree only)
            Files.writeString(fileA, "v2");

            // added     (staged brand-new file)
            Path fileB = Files.writeString(repoDir.resolve("fileB.txt"), "new");
            git.add().addFilepattern("fileB.txt").call();

            // removed   (tracked file deleted and staged)
            Files.delete(fileC);
            git.rm().addFilepattern("fileC.txt").call();

            // missing   (tracked file deleted but *not* staged)
            Files.delete(fileD);

            // changed   (tracked file modified *and* staged)
            Files.writeString(fileE, "v2");
            git.add().addFilepattern("fileE.txt").call();
        }

        var status = new GitTools(tempDir.toString()).getStatus("repoStatus");

        assertThat(status.added()).containsExactlyInAnyOrder("fileB.txt");
        assertThat(status.changed()).containsExactlyInAnyOrder("fileE.txt");
        assertThat(status.missing()).containsExactlyInAnyOrder("fileD.txt");
        assertThat(status.modified()).containsExactlyInAnyOrder("fileA.txt");
        assertThat(status.removed()).containsExactlyInAnyOrder("fileC.txt");
        assertThat(status.untracked()).isEmpty();
    }

    @Test
    void getStatus_throwsWhenDirectoryIsNotGitRepo() throws Exception {
        Files.createDirectories(tempDir.resolve("notRepo"));

        assertThatThrownBy(() -> new GitTools(tempDir.toString()).getStatus("notRepo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a git repository");
    }
}
