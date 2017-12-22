package pw.avvero.crools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pw.avvero.crools.analize.DataSetExtractor;
import pw.avvero.crools.service.RuleService;

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

    @Autowired
    private RuleService ruleService;

    @RequestMapping(value = "/feature", method = RequestMethod.GET)
    public Object feature() throws IOException {
        String path = "src/main/resources/group_distribution.feature";
        String feature = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
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
        return ruleService.analise(temp.getPath());
    }

}
