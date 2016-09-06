/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.scanplus.notesphonenumbers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author t.genannt
 */
public class JSONUtils {

    private static final Logger LOG = LogManager.getLogger(JSONUtils.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
        MAPPER.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    public static List<AddressLink> parseList(InputStream stream) throws IOException {
        return MAPPER.readValue(stream, new TypeReference<List<AddressLink>>() {
        });
    }

    public static AddressData parseAddressData(InputStream stream) throws IOException {
        return MAPPER.readValue(stream, AddressData.class);
    }

    public static String writeToJSON(AddressData ad) {
        try {
            return MAPPER.writeValueAsString(ad);
        } catch (JsonProcessingException ex) {
            LOG.error(ex);
            return "{}";
        }
    }
}
