package sh.jfm.springbootdemos.aiagent;

import org.eclipse.jgit.api.Git;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GitTools {
    private final String basePath;

    public GitTools(String basePath) {
        var trimmed = basePath.trim();
        if (!Files.isDirectory(Paths.get(trimmed))) {
            throw new IllegalArgumentException("Base path %s is not a directory".formatted(trimmed));
        }
        this.basePath = trimmed;
    }

    /// Returns the git log (most-recent first) for the given repository that
    /// lives directly under `basePath`.
    ///
    /// @param repoName   directory name of the repo (immediate child of base path)
    /// @param maxEntries maximum number of commits to return; zero or negative for all
    /// @throws IllegalArgumentException if the directory is not a git repo
    @Tool(description = "Get the git log for the given repository. Latest commits are returned first.")
    public List<GitLogEntry> getLog(
            @ToolParam(description = "folder name of git repository") String repoName,
            @ToolParam(description = """
                    Pass a positive number to limit how many commits are returned.
                    Zero or a negative number returns the full log.
                    """)
            int maxEntries
    ) throws Exception {
        var repoPath = Paths.get(basePath, repoName);
        if (!Files.isDirectory(repoPath.resolve(".git"))) {
            throw new IllegalArgumentException("%s is not a git repository".formatted(repoName));
        }

        try (Git git = Git.open(repoPath.toFile())) {
            // negative or zero ⇒ return the full log
            long limit = maxEntries > 0 ? maxEntries : Long.MAX_VALUE;
            return StreamSupport.stream(git.log().call().spliterator(), false)
                    .limit(limit)
                    .map(commit -> new GitLogEntry(
                            commit.getAuthorIdent().getName(),
                            Instant.ofEpochSecond(commit.getCommitTime())
                                    .atOffset(ZoneOffset.UTC)
                                    .toString(),
                            commit.getShortMessage()
                    ))
                    .toList();
        }
    }

    @Tool(description = "Find Git repos in the available directory")
    public Collection<String> getRepos() throws IOException {
        try (var stream = Files.list(Paths.get(basePath))) {
            return stream
                    .filter(Files::isDirectory)               // only immediate children
                    .filter(dir -> Files.isDirectory(dir.resolve(".git")))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    @Tool(description = "Return the working–tree status of the given repository")
    public GitStatus getStatus(
            @ToolParam(description = "folder name of git repository") String repoName
    ) throws Exception {
        var repoPath = Paths.get(basePath, repoName);
        if (!Files.isDirectory(repoPath.resolve(".git"))) {
            throw new IllegalArgumentException("%s is not a git repository".formatted(repoName));
        }

        try (Git git = Git.open(repoPath.toFile())) {
            var status = git.status().call();
            return new GitStatus(
                    status.getAdded(),
                    status.getChanged(),
                    status.getMissing(),
                    status.getModified(),
                    status.getRemoved(),
                    status.getUntracked()
            );
        }
    }

    /// Single git-commit entry.
    public record GitLogEntry(String author, String date, String message) {
    }

    /// Current working-tree status of a Git repo.
    public record GitStatus(
            java.util.Set<String> added,
            java.util.Set<String> changed,
            java.util.Set<String> missing,
            java.util.Set<String> modified,
            java.util.Set<String> removed,
            java.util.Set<String> untracked) {
    }
}
