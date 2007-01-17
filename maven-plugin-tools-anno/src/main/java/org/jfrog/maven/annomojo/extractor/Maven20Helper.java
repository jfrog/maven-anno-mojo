package org.jfrog.maven.annomojo.extractor;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.settings.RuntimeInfo;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;

/**
 * @author Jason van Zyl
 */
public class Maven20Helper implements MavenHelper {

    private static final String userHome = System.getProperty("user.home");

    private static final File userMavenConfigurationHome = new File(userHome, ".m2");

    private static final String mavenHome = System.getProperty("maven.home");

    // ----------------------------------------------------------------------
    // Settings
    // ----------------------------------------------------------------------

    private static final File defaultUserSettingsFile = new File(userMavenConfigurationHome, "settings.xml");

    private static final File defaultGlobalSettingsFile = new File(mavenHome, "conf/settings.xml");

    private static final String ALT_USER_SETTINGS_XML_LOCATION = "org.apache.maven.user-settings";

    private static final String ALT_GLOBAL_SETTINGS_XML_LOCATION = "org.apache.maven.global-settings";

    // ----------------------------------------------------------------------
    // Local Repository
    // ----------------------------------------------------------------------

    String ALT_LOCAL_REPOSITORY_LOCATION = "maven.repo.local";

    File defaultUserLocalRepository = new File(userMavenConfigurationHome, "repository");

    private ArtifactRepositoryLayout repositoryLayout;

    private ArtifactRepositoryFactory artifactRepositoryFactory;

    private MavenSettingsBuilder settingsBuilder;

    Maven20Helper(PlexusContainer container) throws ComponentLookupException {
        repositoryLayout =
                (ArtifactRepositoryLayout) container.lookup(ArtifactRepositoryLayout.ROLE);
        artifactRepositoryFactory =
                (ArtifactRepositoryFactory) container.lookup(ArtifactRepositoryFactory.ROLE);
        settingsBuilder =
                (MavenSettingsBuilder) container.lookup(MavenSettingsBuilder.ROLE);
    }

    // ----------------------------------------------------------------------------
    // ArtifactRepository
    // ----------------------------------------------------------------------------

    public ArtifactRepository createLocalRepository(File directory) {
        String localRepositoryUrl = directory.getAbsolutePath();

        if (!localRepositoryUrl.startsWith("file:")) {
            localRepositoryUrl = "file://" + localRepositoryUrl;
        }

        return createRepository("local", localRepositoryUrl, false, true, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);
    }

    public ArtifactRepository createRepository(String repositoryId,
                                               String repositoryUrl,
                                               boolean offline,
                                               boolean updateSnapshots,
                                               String globalChecksumPolicy) {
        ArtifactRepository localRepository =
                new DefaultArtifactRepository(repositoryId, repositoryUrl, repositoryLayout);

        boolean snapshotPolicySet = false;

        if (offline) {
            snapshotPolicySet = true;
        }

        if (!snapshotPolicySet && updateSnapshots) {
            artifactRepositoryFactory.setGlobalUpdatePolicy(ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS);
        }

        artifactRepositoryFactory.setGlobalChecksumPolicy(globalChecksumPolicy);

        return localRepository;
    }

    // ----------------------------------------------------------------------------
    // Settings
    // ----------------------------------------------------------------------------

    public Settings buildSettings(File userSettingsPath,
                                  File globalSettingsPath,
                                  boolean interactive,
                                  boolean offline,
                                  boolean usePluginRegistry,
                                  Boolean pluginUpdateOverride)
            throws Exception {
        Settings settings = buildSettings(userSettingsPath,
                globalSettingsPath,
                pluginUpdateOverride);
        if (offline) {
            settings.setOffline(true);
        }

        settings.setInteractiveMode(interactive);

        settings.setUsePluginRegistry(usePluginRegistry);

        return settings;
    }

    public Settings buildSettings(File userSettingsPath,
                                  File globalSettingsPath,
                                  Boolean pluginUpdateOverride)
            throws Exception {
        Settings settings;

        try {
            settings = settingsBuilder.buildSettings(userSettingsPath);
        }
        catch (IOException e) {
            throw new Exception("Error reading settings file", e);
        }
        catch (XmlPullParserException e) {
            throw new Exception(e.getMessage() + e.getDetail() + e.getLineNumber() +
                    e.getColumnNumber());
        }

        RuntimeInfo runtimeInfo = new RuntimeInfo(settings);

        runtimeInfo.setPluginUpdateOverride(pluginUpdateOverride);

        settings.setRuntimeInfo(runtimeInfo);

        return settings;
    }

    /**
     * Retrieve the user settings path using the followiwin search pattern:
     * <p/>
     * 1. System Property
     * 2. Optional path
     * 3. ${user.home}/.m2/settings.xml
     */
    public File getUserSettingsPath(String optionalSettingsPath) {
        File userSettingsPath = new File(System.getProperty(ALT_USER_SETTINGS_XML_LOCATION) + "");

        if (!userSettingsPath.exists()) {
            if (optionalSettingsPath != null) {
                File optionalSettingsPathFile = new File(optionalSettingsPath);

                if (optionalSettingsPathFile.exists()) {
                    userSettingsPath = optionalSettingsPathFile;
                } else {
                    userSettingsPath = defaultUserSettingsFile;
                }
            } else {
                userSettingsPath = defaultUserSettingsFile;
            }
        }

        return userSettingsPath;
    }

    /**
     * Retrieve the global settings path using the followiwin search pattern:
     * <p/>
     * 1. System Property
     * 2. CLI Option
     * 3. ${maven.home}/conf/settings.xml
     */
    public File getGlobalSettingsPath() {
        File globalSettingsFile = new File(System.getProperty(ALT_GLOBAL_SETTINGS_XML_LOCATION) + "");

        if (!globalSettingsFile.exists()) {
            globalSettingsFile = defaultGlobalSettingsFile;
        }

        return globalSettingsFile;
    }

    /**
     * Retrieve the local repository path using the followiwin search pattern:
     * <p/>
     * 1. System Property
     * 2. localRepository specified in user settings file
     * 3. ${user.home}/.m2/repository
     */
    public String getLocalRepositoryPath(Settings settings) {
        String localRepositoryPath = System.getProperty(ALT_LOCAL_REPOSITORY_LOCATION);

        if (localRepositoryPath == null) {
            localRepositoryPath = settings.getLocalRepository();
        }

        if (localRepositoryPath == null) {
            localRepositoryPath = defaultUserLocalRepository.getAbsolutePath();
        }

        return localRepositoryPath;
    }

    public String getLocalRepositoryPath()
            throws Exception {
        return getLocalRepositoryPath(buildSettings(getUserSettingsPath(null),
                getGlobalSettingsPath(),
                false,
                true,
                false,
                Boolean.FALSE));
    }

    public ArtifactRepository getLocalRepository()
            throws Exception {
        return createLocalRepository(new File(getLocalRepositoryPath()));
    }
}

