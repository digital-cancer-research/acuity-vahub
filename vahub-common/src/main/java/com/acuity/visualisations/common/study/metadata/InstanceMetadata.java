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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toList;

/**
 * Represents the all metadata of an instance
 *
 * @author ksnd199
 */
public class InstanceMetadata {

    private static Gson gson = GsonBuilder.GSON;

    private List<MetadataItem> metadataItems = newArrayList();

    public InstanceMetadata add(MetadataItem item) {
        metadataItems.add(item);
        return this;
    }

    public void addItemProperty(String key, String name, List list) {
        Optional<MetadataItem> itemOptional = metadataItems.stream().filter(i -> i.getKey().equals(key)).findFirst();
        MetadataItem item;
        if (!itemOptional.isPresent()) {
            item = new MetadataItem(key);
            metadataItems.add(item);
        } else {
            item = itemOptional.get();
        }
        item.add(name, list);
    }

    public String build() {
        JsonObject rootObject = new JsonObject();

        for (MetadataItem metadataItem : metadataItems) {
            rootObject.add(metadataItem.getKey(), metadataItem.getItemObject());
        }

        return gson.toJson(rootObject);
    }

    public static InstanceMetadata read(String stringInstanceMetadata) {

        InstanceMetadata newInstanceMetadata = new InstanceMetadata();
        /**
         * <code>
         * "{
         * "aes": {
         *  "listOfUser": [
         *    "glen",
         *   "sam"
         * ],
         * "aesList": [
         *    "glen",
         *    "sam"
         *  ],
         *  "string1": "string",
         *   "hasData": true,
         *   "count": 11
         * },
         *"labs": {
         *  "listOfUser": [
         *    "glen1",
         *   "sam1"
         *   ],
         *  "labsList": [
         *   "glen1",
         *   "sam1"
         * ]
         *}
         *}"
         * </code>
         */
        JsonObject rootObject = gson.fromJson(stringInstanceMetadata, JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : rootObject.entrySet()) {
            /**
             * <code>
             * key "labs":
             * value  "listOfUser": [ "glen1", "sam1" ], "labsList": [ "glen1", "sam1" ]
             * </code>
             */
            String key = entry.getKey();

            MetadataItem metadataItem = new MetadataItem(key, (JsonObject) entry.getValue());

            newInstanceMetadata.add(metadataItem);
        }

        return newInstanceMetadata;
    }

    public static InstanceMetadata merge(boolean allowCounts, String... stringInstanceMetadatas) {
        return merge(allowCounts, newArrayList(stringInstanceMetadatas));
    }
    
    public static InstanceMetadata merge(boolean allowCounts, List<String> stringInstanceMetadatas) {
        InstanceMetadata newInstanceMetadata = new InstanceMetadata();
        /**
         * <code>
         * "{
         * "aes": {
         *  "listOfUser": [
         *    "glen",
         *   "sam"
         * ],
         * "aesList": [
         *    "glen",
         *    "sam"
         *  ],
         *  "string1": "string",
         *   "hasData": true,
         *   "count": 11
         * },
         *"labs": {
         *  "listOfUser": [
         *    "glen1",
         *   "sam1"
         *   ],
         *  "labsList": [
         *   "glen1",
         *   "sam1"
         * ]
         *}
         *}"
         * </code>
         */
        List<JsonObject> rootObjects = newArrayList(stringInstanceMetadatas).stream().
                map(smi -> gson.fromJson(smi, JsonObject.class)).collect(toList());

        Set<String> allKeys = newHashSet();

        for (JsonObject itemObject : rootObjects) {
            for (Map.Entry<String, JsonElement> entry : itemObject.entrySet()) {
                allKeys.add(entry.getKey());
            }
        }

        for (String key : allKeys) { // keys "aes", "labs" etc

            List<String> metadataItems = getMetadataItems(key, rootObjects).stream().map(mi -> mi.build()).collect(toList());

            MetadataItem mergeMetadataItem = MetadataItem.merge(allowCounts, metadataItems);
            
            newInstanceMetadata.add(mergeMetadataItem);
        }

        return newInstanceMetadata;
    }

    private static List<MetadataItem> getMetadataItems(String key, List<JsonObject> rootObjects) {
        List<MetadataItem> metadataItems = newArrayList();

        for (JsonObject itemObject : rootObjects) {
            for (Map.Entry<String, JsonElement> entry : itemObject.entrySet()) {
                String metadataItemkey = entry.getKey();
                if (metadataItemkey.equals(key)) {

                    MetadataItem metadataItem = new MetadataItem(key, (JsonObject) entry.getValue());

                    metadataItems.add(metadataItem);
                }
            }
        }

        return metadataItems;
    }
}
