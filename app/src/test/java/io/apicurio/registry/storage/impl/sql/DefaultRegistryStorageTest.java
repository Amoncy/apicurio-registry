/*
 * Copyright 2022 Red Hat
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

package io.apicurio.registry.storage.impl.sql;

import io.apicurio.registry.storage.RegistryStorage;
import io.apicurio.registry.types.Current;
import jakarta.inject.Inject;

import io.apicurio.registry.noprofile.storage.AbstractRegistryStorageTest;
import io.quarkus.test.junit.QuarkusTest;

/**
 * @author eric.wittmann@gmail.com
 */
@QuarkusTest
public class DefaultRegistryStorageTest extends AbstractRegistryStorageTest {

    @Inject
    @Current
    RegistryStorage storage;

    /**
     * @see io.apicurio.registry.storage.AbstractRegistryStorageTest#storage()
     */
    @Override
    protected RegistryStorage storage() {
        return storage;
    }

}