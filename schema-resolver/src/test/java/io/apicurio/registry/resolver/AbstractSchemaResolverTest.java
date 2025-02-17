/*
 * Copyright 2023 Red Hat
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
package io.apicurio.registry.resolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.apicurio.registry.resolver.data.Record;
import io.apicurio.registry.resolver.strategy.ArtifactReference;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractSchemaResolverTest {
    @Test
    void testConfigureInitializesSchemaCache() throws Exception {
        Map<String, String> configs = Collections.singletonMap(SchemaResolverConfig.REGISTRY_URL, "http://localhost");

        try (TestAbstractSchemaResolver<Object, Object> resolver = new TestAbstractSchemaResolver<>()) {
            resolver.configure(configs, null);

            assertDoesNotThrow(() -> {resolver.schemaCache.checkInitialized();});
        }
    }

    @Test
    void testSupportsFailureTolerantSchemaCache() throws Exception {
        Map<String, Object> configs = new HashMap<>();
        configs.put(SchemaResolverConfig.REGISTRY_URL, "http://localhost");
        configs.put(SchemaResolverConfig.FAULT_TOLERANT_REFRESH, true);

        try (TestAbstractSchemaResolver<Object, Object> resolver = new TestAbstractSchemaResolver<>()) {
            resolver.configure(configs, null);

            assertTrue(resolver.schemaCache.isFaultTolerantRefresh());
        }
    }

    @Test
    void testDefaultsToFailureTolerantSchemaCacheDisabled() throws Exception {
        Map<String, Object> configs = new HashMap<>();
        configs.put(SchemaResolverConfig.REGISTRY_URL, "http://localhost");

        try (TestAbstractSchemaResolver<Object, Object> resolver = new TestAbstractSchemaResolver<>()) {
            resolver.configure(configs, null);

            assertFalse(resolver.schemaCache.isFaultTolerantRefresh());
        }
    }

    @Test
    void testDefaultsToCacheLatestEnabled() throws Exception {
        Map<String, Object> configs = new HashMap<>();
        configs.put(SchemaResolverConfig.REGISTRY_URL, "http://localhost");

        try (TestAbstractSchemaResolver<Object, Object> resolver = new TestAbstractSchemaResolver<>()) {
            resolver.configure(configs, null);

            assertTrue(resolver.schemaCache.isCacheLatest());
        }
    }

    class TestAbstractSchemaResolver<SCHEMA, DATA> extends AbstractSchemaResolver<SCHEMA, DATA> {

        @Override
        public SchemaLookupResult<SCHEMA> resolveSchema(Record<DATA> data) {
            throw new UnsupportedOperationException("Unimplemented method 'resolveSchema'");
        }

        @Override
        public SchemaLookupResult<SCHEMA> resolveSchemaByArtifactReference(ArtifactReference reference) {
            throw new UnsupportedOperationException("Unimplemented method 'resolveSchemaByArtifactReference'");
        }
        
    }
}
