/*
 *     Copyright (C) 2019  Vikas Kumar Verma
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.vedanta.vidiyalay.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class Utility {

    private final Environment env;

    private final ObjectMapper objectMapper;

    public Utility(Environment env, ObjectMapper objectMapper) {
        this.env = env;
        this.objectMapper = objectMapper;
    }

    public static int getSessionYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }


    public static Date getCurrentDateTime() {
        return Calendar.getInstance().getTime();
    }
    public static String emptyValueCheck(String value) {
        return StringUtils.isEmpty(value)  ? null : value;
    }


    public Optional<String> getObjectToJson(Object object)  {
        try {
            return Optional.of(objectMapper.writeValueAsString(object));
        } catch (IOException e) {
            log.error("Error: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public  String getHostName() {
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        return hostAddress;
    }

    public  String getPort() {
        return env.getProperty("server.port");
    }

    public String getProtocol() {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        return protocol;
    }

    public String getHostAndPortName() {
        return String.format("%s//%s:%s",getProtocol(),getHostName(), getPort());
    }

    public String getHostAndPortNameWithContextPath() {
        return String.format("%s//%s:%s/%s",getProtocol(),getHostName(), getPort(), getContextPath());
    }


      String getContextPath() {
        String contextPath = env.getProperty("server.servlet.context-path");
        if (!(StringUtils.hasText(contextPath))) {
            contextPath = "/";
        }
        return contextPath;
    }

    public static <T>  T getDeserialize(byte[] messageBody, Class<T> clazz) {
        return (T) new Jackson2JsonRedisSerializer<>(clazz.getClass())
                .deserialize(messageBody);
    }

}
