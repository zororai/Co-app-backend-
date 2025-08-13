package com.commstack.coapp.Configs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalTime;

public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Accept both string and object
        if (p.currentToken().isStructStart()) {
            // Parse as object {"hour":10,"minute":30}
            com.fasterxml.jackson.databind.JsonNode node = p.getCodec().readTree(p);
            int hour = node.has("hour") ? node.get("hour").asInt() : 0;
            int minute = node.has("minute") ? node.get("minute").asInt() : 0;
            int second = node.has("second") ? node.get("second").asInt() : 0;
            return LocalTime.of(hour, minute, second);
        } else {
            // Parse as string "10:30:00"
            String text = p.getText();
            return LocalTime.parse(text);
        }
    }
}
