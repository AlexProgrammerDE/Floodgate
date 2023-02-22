plugins {
    id("floodgate.shadow-conventions")
    id("net.kyori.indra.publishing")
}

indra {
    configurePublications {
        if (shouldAddBranchName()) {
            version = versionWithBranchName()
        }
    }

    publishSnapshotsTo("geysermc", "https://repo.opencollab.dev/artifactory/maven-snapshots")
    publishReleasesTo("geysermc", "https://repo.opencollab.dev/artifactory/maven-releases")
}