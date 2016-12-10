package com.github.blindpirate.gogradle.core.dependency.parse;

import com.github.blindpirate.gogradle.core.dependency.GitDependency;
import com.github.blindpirate.gogradle.core.dependency.GolangDependency;
import com.github.blindpirate.gogradle.util.Assert;
import com.google.inject.Injector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static com.github.blindpirate.gogradle.core.dependency.GitDependency.NEWEST_COMMIT;
import static com.github.blindpirate.gogradle.util.MapUtils.getBooleanValue;
import static com.github.blindpirate.gogradle.util.MapUtils.getString;
import static com.github.blindpirate.gogradle.util.StringUtils.allBlank;
import static com.github.blindpirate.gogradle.util.StringUtils.isBlank;
import static com.github.blindpirate.gogradle.util.StringUtils.isNotBlank;
import static com.github.blindpirate.gogradle.util.StringUtils.splitAndTrim;

@Singleton
public class GithubNotationParser extends MapStringNotationParser {

    // TODO there are too many "github.com"s in the code
    private static final String GITHUB_HOST = "github.com";

    private static final String TAG_SEPERATOR = "@";
    private static final String COMMIT_SEPERATOR = "#";

    @Inject
    private Injector injector;

    @Override
    public boolean acceptString(String notation) {
        return notation.startsWith(GITHUB_HOST);
    }

    @Override
    public boolean acceptMap(Map<String, Object> notation) {
        Object name = notation.get(NAME_KEY);
        return accept(name);
    }

    // github.com/a/b
    // github.com/a/b@v1.0.0
    // github.com/a/b@tagName
    // github.com/a/b@>=1.2.0
    // github.com/a/b#commitId
    // github.com/a/b@tag-with@
    // github.com/a/b@tag-with#
    // github.com/a/b@tag-contains@hahaha
    // github.com/a/b@tag-contains#hahaha
    // github.com/a/b@tag-contains@and#at-the-same-time

    // sem version


    @Override
    public GolangDependency parseString(String notation) {
        // Github doesn't allow '@' and '#' in repository name (and in user name either)
        // if there are multiple '@' and '#', they must be in tags.
        if (notation.contains(TAG_SEPERATOR)) {
            return buildByTag(notation);
        } else if (notation.contains(COMMIT_SEPERATOR)) {
            return buildByCommit(notation);
        } else {
            return buildByName(notation);
        }
    }

    @Override
    public GolangDependency parseMap(Map<String, Object> notation) {
        String name = getString(notation, "name");
        String url = getString(notation, "url");
        String version = getString(notation, "version");
        String tag = getString(notation, "tag");
        String commit = getString(notation, "commit");
        boolean transitive = getBooleanValue(notation, "transitive", true);
        boolean excludeVendor = getBooleanValue(notation, "excludeVendor", false);

        if (isBlank(url)) {
            url = buildUrl(name);
        }

        if (isBlank(tag) && isNotBlank(version)) {
            tag = version;
        }

        if (allBlank(version, tag, commit)) {
            commit = NEWEST_COMMIT;
        }

        GitDependency ret = new GitDependency(name);
        injector.injectMembers(ret);

        ret.setVersion(version)
                .setTag(tag)
                .setCommit(commit)
                .setUrl(url)
                .setName(name)
                .setTransitive(transitive)
                .setExcludeVendor(excludeVendor);

        return ret;
    }


    private GolangDependency buildByName(String notation) {
        String url = buildUrl(notation);
        GitDependency ret = new GitDependency(notation);
        injector.injectMembers(ret);
        ret.setUrl(url)
                .setCommit(NEWEST_COMMIT);
        return ret;
    }

    private GolangDependency buildByCommit(String notation) {
        String[] array = splitAndTrim(notation, COMMIT_SEPERATOR);

        Assert.isTrue(array.length == 2, "Invalid notation:" + notation);

        String name = array[0];
        String url = buildUrl(name);
        String commit = array[1];

        GitDependency ret = new GitDependency(name);
        injector.injectMembers(ret);
        ret.setUrl(url)
                .setCommit(commit);
        return ret;
    }

    private String buildUrl(String name) {
        return "https://" + name + ".git";
    }

    private GolangDependency buildByTag(String notation) {
        int indexOfAt = notation.indexOf(TAG_SEPERATOR);

        String name = notation.substring(0, indexOfAt);
        String url = buildUrl(name);
        String tag = notation.substring(indexOfAt + 1);

        GitDependency ret = new GitDependency(name);
        injector.injectMembers(ret);
        ret.setUrl(url)
                .setTag(tag);
        return ret;
    }

}
