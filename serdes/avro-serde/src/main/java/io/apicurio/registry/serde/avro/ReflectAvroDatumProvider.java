/*
 * Copyright 2020 Red Hat
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

package io.apicurio.registry.serde.avro;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;

/**
 * @author Ales Justin
 */
public class ReflectAvroDatumProvider<T> implements AvroDatumProvider<T> {

    private Schema readerSchema;
    private final ReflectData reflectData;

    public ReflectAvroDatumProvider() {
        this(ReflectData.get());
    }

    public ReflectAvroDatumProvider(ReflectData reflectData) {
        this.reflectData = reflectData;
    }

    public ReflectAvroDatumProvider(Class<T> clazz) {
        this(ReflectData.get(),clazz);
    }

    public ReflectAvroDatumProvider(ReflectData reflectData,Class<T> clazz) {
        this(reflectData);
        this.readerSchema = AvroSchemaUtils.getReflectSchema(reflectData,clazz);
    }

    @Override
    public DatumWriter<T> createDatumWriter(T data, Schema schema) {
        return new ReflectDatumWriter<>(schema,reflectData);
    }

    @Override
    public DatumReader<T> createDatumReader(Schema schema) {
        if (readerSchema == null) {
            return new ReflectDatumReader<>(schema,schema,reflectData);
        } else {
            return new ReflectDatumReader<>(schema, readerSchema,reflectData);
        }
    }

    @Override
    public Schema toSchema(T data) {
        return AvroSchemaUtils.getReflectSchema(reflectData,data);
    }
}
