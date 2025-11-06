package com.example.functions;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.execution.DefaultToolCallResultConverter;
import org.springframework.ai.tool.execution.ToolCallResultConverter;
import org.springframework.ai.util.json.JsonParser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Map;

public class MyToolCallResultConverter  implements ToolCallResultConverter {
    private static final Logger logger = LoggerFactory.getLogger(MyToolCallResultConverter.class);

    /**
     * Given an Object returned by a tool, convert it to a String compatible with the
     * given class type.
     *
     * @param result
     * @param returnType
     */
    @Override
    public String convert(Object result, Type returnType) {
        if (returnType == Void.TYPE) {
            logger.info("The tool has no return type. Converting to conventional response.");
            return JsonParser.toJson("Done");
        }
        if (result instanceof RenderedImage) {
            final var buf = new ByteArrayOutputStream(1024 * 4);
            try {
                ImageIO.write((RenderedImage) result, "PNG", buf);
            }
            catch (IOException e) {
                return "Failed to convert tool result to a base64 image: " + e.getMessage();
            }
            final var imgB64 = Base64.getEncoder().encodeToString(buf.toByteArray());
            return JsonParser.toJson(Map.of("mimeType", "image/png", "data", imgB64));
        }
        else {
            logger.info("Converting tool result to JSON.{}", JSON.toJSONString(result));
            try {
                return JsonParser.toJson(result);
            }catch (Exception e){
                logger.error("",e);
              return  JsonParser.toJson(StringEscapeUtils.escapeJson(result+""));
            }
        }
    }


}
