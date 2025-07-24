package org.tkit.quarkus.security.dynamic.test;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class SecurityDynamicTestBuild {

    /**
     * The extension name.
     */
    protected static final String FEATURE_NAME = "tkit-quarkus-dynamic-test";

    /**
     * The extension name.
     *
     * @return the feature build item.
     */
    @BuildStep
    FeatureBuildItem createFeatureItem() {
        return new FeatureBuildItem(FEATURE_NAME);
    }

}
