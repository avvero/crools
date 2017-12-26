package pw.avvero.crools.impl;

import pw.avvero.crools.service.FeatureService;

import java.io.IOException;
import java.util.Map;

public abstract class FeatureAnalyzer {

    public abstract Map analyse(FeatureService featureService) throws IOException;

}
