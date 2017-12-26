package pw.avvero.crools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pw.avvero.crools.impl.group_destribution.GroupDistributionImpl;
import pw.avvero.crools.service.FeatureService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Avvero on 21.12.2017.
 */
@RestController
@RequestMapping(value = "/api")
public class RuleController {

    public static final String FEATURE = "src/main/resources/group_distribution.feature";

    @Autowired
    private FeatureService featureService;

    @RequestMapping(value = "/feature", method = RequestMethod.GET)
    public Object feature() throws IOException {
        String feature = new String(Files.readAllBytes(Paths.get(FEATURE)), Charset.defaultCharset());
        return new HashMap<String, String>() {{
            put("text", feature);
        }};
    }

    @RequestMapping(value = "/analise", method = RequestMethod.POST)
    public Object analise(@RequestBody String featureString) throws IOException {
        File temp;
        try {
            //create a temp file
            temp = File.createTempFile("distribution", ".feature");
            //write it
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            bw.write(featureString);
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GroupDistributionImpl groupDistribution = new GroupDistributionImpl(temp.getPath());
        return groupDistribution.analyse(featureService);
    }

}
