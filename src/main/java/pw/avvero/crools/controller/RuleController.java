package pw.avvero.crools.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pw.avvero.crools.service.FeatureService;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Created by Avvero on 21.12.2017.
 */
@RestController
@RequestMapping(value = "/api")
public class RuleController {

    public static final String FEATURE = "group_distribution.feature";

    @Autowired
    private FeatureService featureService;

    @RequestMapping(value = "/feature", method = RequestMethod.GET)
    public Object feature() throws IOException, URISyntaxException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(FEATURE);
        String feature = IOUtils.toString(in, Charset.defaultCharset());
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

        return featureService.analyse(temp.getPath());
    }

}
