/*
 *
 * Copyright (C) 2019  Vikas Kumar Verma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * /
 */

package org.vedanta.vidiyalay.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.*;

@Slf4j
public class CloneObjects {
    private CloneObjects() {
        // do nothing
    }

    public static <T> Optional<T> clone(Object source, Class<T> sourceClass)  {
        return clone(source, sourceClass, new HashMap<>());
    }

    public static <T> Optional<T> clone(Object source, Class<T> sourceClass,
                                        Map<String, Object> updateFieldValues)  {

        Assert.notNull(source, "Source object is null");
        Assert.notNull(sourceClass, "target type is null");
        Assert.notNull(updateFieldValues, "parameter map is null");

        final Object target;
        try {
            target = source.getClass().newInstance();
        } catch (InstantiationException e) {
            log.error("Error while create clone of immutable object {} error: {}", source, e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            log.error("Error while create clone of immutable object {} error: {}", source, e.getMessage());
            e.printStackTrace();
            return null;
        }
        Arrays.asList(sourceClass.getDeclaredFields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        if(updateFieldValues.containsKey(field.getName())) {
                            field.set(target, updateFieldValues.get(field.getName()));
                        }else {
                            field.set(target, field.get(source));
                        }
                    } catch (IllegalAccessException e) {
                        log.error("Error while create clone of immutable object {} error: {}", source, e.getMessage());
                        e.printStackTrace();
                    }

                });
        return Optional.ofNullable(sourceClass.cast(target));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParameterMap {
        private Map<String, Object> map = new HashMap<>();

        public ParameterMap put(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public Map<String, Object> build () {
            return Collections.unmodifiableMap(map);
        }

    }
}
