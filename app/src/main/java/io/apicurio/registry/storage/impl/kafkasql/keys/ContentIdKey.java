/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apicurio.registry.storage.impl.kafkasql.keys;

import io.apicurio.registry.storage.impl.kafkasql.MessageType;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.UUID;

/**
 * @author eric.wittmann@gmail.com
 */
@RegisterForReflection
public class ContentIdKey implements MessageKey {

    private static final String CONTENT_ID_PARTITION_KEY = "__apicurio_registry_content_id__";

    private final String uuid = UUID.randomUUID().toString();

    /**
     * Creator method.
     *
     */
    public static final ContentIdKey create() {
        ContentIdKey key = new ContentIdKey();
        return key;
    }

    /**
     * @see MessageKey#getType()
     */
    @Override
    public MessageType getType() {
        return MessageType.ContentId;
    }

    /**
     * @see MessageKey#getPartitionKey()
     */
    @Override
    public String getPartitionKey() {
        return CONTENT_ID_PARTITION_KEY;
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format("ContentIdKey(super = %s)", super.toString());
    }

}
