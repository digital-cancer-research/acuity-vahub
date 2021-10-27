/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.common.study.metadata;

import com.acuity.visualisations.common.vo.HasId;
import com.google.common.collect.Sets;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

/**
 * Represents metadata for a module tab.
 *
 * @author ksnd199
 */
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@Slf4j
public class MetadataItem implements HasId<String> {
    private static Gson gson = new com.google.gson.GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create();

    private static final Set<String> COUNT_KEYS = Sets.newHashSet("hasData", "count", "patientList", "detailsOnDemandTitledColumns");

    @Getter
    private JsonObject itemObject = new JsonObject();
    @Singular
    private Map<String, Object> itemObjects;
    @Getter
    private String key;

    public MetadataItem(String key) {
        this.key = key;
    }

    public MetadataItem(String key, JsonObject object) {
        this.key = key;
        this.itemObject = object;
    }

    @Override
    public String getId() {
        return key;
    }

    public JsonElement getJsonObject() {
        JsonObject rootObject = new JsonObject();
        rootObject.add(key, new JsonParser().parse(gson.toJson(itemObjects)));
        return rootObject;
    }

    JsonElement getJsonItemObject() {
        return new JsonParser().parse(gson.toJson(itemObjects));
    }

    /**
     * Converts an json string back to a MetadataItem
     * <p>
     * {
     * "aes": { "test": "sd" } }
     * <p>
     * Where key = aes and itemObject = { "test": "sd" }
     *
     * @param stringMetadataItem string to convert back into a MetadataItem
     * @return this MetadataItem
     */
    public static MetadataItem read(String stringMetadataItem) {
        JsonObject rootObject = gson.fromJson(stringMetadataItem, JsonObject.class);

        String key = rootObject.entrySet().iterator().next().getKey();
        JsonObject itemObject = rootObject.getAsJsonObject(key);

        MetadataItem metadataItem = new MetadataItem(key);

        for (Map.Entry<String, JsonElement> entry : itemObject.entrySet()) {
            String metadataItemkey = entry.getKey();
            JsonElement value = entry.getValue();

            metadataItem.add(metadataItemkey, value);
        }

        log.info("Key is " + key);
        log.info("Item object is " + itemObject);
        return metadataItem;
    }

    public static MetadataItem merge(boolean allowCounts, String... stringMetadataItems) {
        return merge(allowCounts, newArrayList(stringMetadataItems));
    }

    public static MetadataItem merge(boolean allowCounts, List<String> stringMetadataItems) {

        List<JsonObject> rootObjects = stringMetadataItems.stream().
                map(smi -> gson.fromJson(smi, JsonObject.class)).collect(toList());

        String key = rootObjects.get(0).entrySet().iterator().next().getKey();

        List<JsonObject> itemObjects = rootObjects.stream().
                map(rO -> rO.getAsJsonObject(key)).collect(toList());

        MetadataItem mergedMetadataItem = new MetadataItem(key);

        Set<String> miKeys = itemObjects.stream()
                .flatMap(itemObject -> itemObject.entrySet().stream())
                .map(Map.Entry::getKey)
                .filter(k -> allowCounts || !COUNT_KEYS.contains(k)) // remove keys that are not to be merged
                .collect(Collectors.toSet());

        for (String mikey : miKeys) {
            List<JsonElement> values = getValues(mikey, itemObjects);

            JsonElement mergedValue = null;
            if (values.get(0) instanceof JsonPrimitive) {
                JsonPrimitive jsonPrimitive = (JsonPrimitive) values.get(0);
                if (jsonPrimitive.isBoolean()) {
                    // all have to be true for merged to be true
                    boolean mergedBoolean = values.stream().allMatch(v -> ((JsonPrimitive) v).getAsBoolean());
                    mergedValue = new JsonPrimitive(mergedBoolean);
                }
                if (jsonPrimitive.isNumber() || jsonPrimitive.isString()) {
                    // take first
                    mergedValue = jsonPrimitive;
                }
            } else if (values.get(0) instanceof JsonArray) {
                log.debug("Merging lists {}", values);
                // all have to be true for merged to be true
                List<Object> mergedList = values.stream().map(jsonArray -> {
                    List<Object> list = newArrayList();
                    for (int i = 0; i < ((JsonArray) jsonArray).size(); i++) {

                        JsonElement el = ((JsonArray) jsonArray).get(i);
                        JsonPrimitive jp = null;

                        if (el.isJsonPrimitive()) {
                            Object object = null;
                            jp = el.getAsJsonPrimitive();
                            if (jp.isNumber()) {
                                if (jp.getAsString().contains(".")) {
                                    object = jp.getAsFloat();
                                } else {
                                    object = jp.getAsInt();
                                }
                            } else if (jp.isString()) {
                                object = jp.getAsString();
                            }
                            list.add(object);
                        } else {
                            list.add(el.getAsJsonObject());
                        }
                    }

                    return list;
                }).flatMap(List::stream).distinct().collect(toList());
                mergedValue = toJsonArray(mergedList);
                log.debug("MergedValue {}", mergedValue);
            } else {
                mergedValue = values.get(0);
            }

            mergedMetadataItem.add(mikey, mergedValue);
        }

        return mergedMetadataItem;
    }

    public static JsonArray toJsonArray(List<?> yaml) {
        JsonArray array = new JsonArray();
        for (Object o : yaml) {
            array.add(toJsonElement(o));
        }
        return array;
    }

    public static JsonElement toJsonElement(Object el) {
        if (el == null) {
            return JsonNull.INSTANCE;
        } else if (el instanceof List) {
            return toJsonArray((List<?>) el);
        } else if (el instanceof Map) {
            return toJsonObject((Map<?, ?>) el);
        } else if (el instanceof Number) {
            return new JsonPrimitive((Number) el);
        } else if (el instanceof JsonObject) {
            return (JsonObject) el;
        } else {
            return new JsonPrimitive(el.toString());
        }
    }

    public static JsonObject toJsonObject(Map<?, ?> yaml) {
        JsonObject obj = new JsonObject();
        for (Map.Entry<?, ?> entry : yaml.entrySet()) {
            obj.add(entry.getKey().toString(), toJsonElement(entry.getValue()));
        }
        return obj;
    }

    private static List<JsonElement> getValues(String key, List<JsonObject> itemObjects) {

        List<JsonElement> values = newArrayList();

        for (JsonObject itemObject : itemObjects) {
            for (Map.Entry<String, JsonElement> entry : itemObject.entrySet()) {
                String metadataItemkey = entry.getKey();
                if (metadataItemkey.equals(key)) {
                    values.add(entry.getValue());
                }
            }
        }

        return values;
    }

    /**
     * <code>
     * key "labs":
     * value  "listOfUser": [ "glen1", "sam1" ], "labsList": [ "glen1", "sam1" ]
     * </code>
     *
     * @deprecated
     */
    @Deprecated
    public static MetadataItem read(String key, String value) {
        JsonObject valueObject = gson.fromJson(value, JsonObject.class);

        String valueKey = valueObject.entrySet().iterator().next().getKey();
        JsonArray itemArray = valueObject.getAsJsonArray(valueKey);

        MetadataItem metadataItem = new MetadataItem(key);
        log.info("Key is " + key);
        log.info("Item object is " + itemArray);
        return metadataItem;
    }

    /**
     * Add either a Number, Boolean or String with a name
     *
     * @param name  key/name of the value
     * @param value either Number, Boolean or String
     * @return MetadataItem
     */
    public MetadataItem addProperty(String name, Object value) {
        if (value instanceof String) {
            itemObject.addProperty(name, (String) value);
        } else if (value instanceof Boolean) {
            itemObject.addProperty(name, (Boolean) value);
        } else if (value instanceof Number) {
            itemObject.addProperty(name, (Number) value);
        } else if (value instanceof Collection || value instanceof Map) {
            itemObject.add(name, gson.toJsonTree(value));
        } else {
            throw new IllegalArgumentException(value + " of type " + value.getClass().getSimpleName() + " isnt allowed");
        }

        return this;
    }

    /**
     * Adds a JsonElement
     *
     * @param name    key/name of the value
     * @param element
     * @return this MetadataItem
     */
    public MetadataItem add(String name, JsonElement element) {
        itemObject.add(name, element);
        return this;
    }

    /**
     * Adds a list
     *
     * @param name key/name of the value
     * @param list
     * @return this MetadataItem
     */
    public MetadataItem add(String name, Collection list) {
        JsonElement element = new JsonParser().parse(gson.toJson(list));
        return add(name, element);
    }

    /**
     * Adds a map
     *
     * @param name key/name of the value
     * @param map
     * @return this MetadataItem
     */
    public MetadataItem add(String name, Map map) {
        JsonElement element = new JsonParser().parse(gson.toJson(map));
        return add(name, element);
    }

    /**
     * Converts into a json string
     *
     * @return json string
     */
    public String build() {

        JsonObject rootObject = new JsonObject();
        rootObject.add(key, itemObject);
        return gson.toJson(rootObject);
    }


}
