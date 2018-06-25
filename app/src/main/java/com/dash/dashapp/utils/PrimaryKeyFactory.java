/*
 * Copyright 2017 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dash.dashapp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmFieldType;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Factory class for creating auto-incremented integral keys for Realm.
 * <p>
 * This class should be initialized before creating <i>any</i> RealmObjects for a given Realm.
 * <p>
 * <b>WARNING:</b>
 * This class is not safe to use if you Realm is accessed from multiple devices or processes.
 * <p>
 * Usage:
 * <pre>
 * {@code
 * public class MyApplication extends Application {
 *    @Override
 *    public void onCreate() {
 *        super.onCreate();
 *        Realm.init(this);
 *        Realm realm = Realm.getDefaultInstance();
 *        PrimaryKeyFactory.init(realm);
 *        realm.close();
 *    }
 * }
 *
 * // In objects
 * public class Person extends RealmObject {
 *     \@PrimaryKey
 *     private int id = PrimaryKeyFactory.nextKey(this.class);
 *     private String name;
 * }
 *
 * // When creating objects directly
 * Person p = realm.createObject(Person.class, PrimaryKeyFactory.nextKey(Person.class));
 * }
 * </pre>
 * <p>
 * Known limitations:
 * <ul>
 * <li>
 * This class does not work if the Realm Model class names have been obfuscated.
 * </li>
 * <li>
 * No error checking when generating keys to keep it as fast as possible. A {@code NullPointerException}
 * indicate wrong use of the class.
 * </li>
 * <p>
 * <li>
 * This class does not work with {@code DynamicRealm}s.
 * </li>
 * </ul>
 *
 * @see <a href="https://github.com/realm/realm-java/issues/469#issuecomment-196798253">Background issue for this class</a>
 */
public class PrimaryKeyFactory {

    private static Map<Class<? extends RealmModel>, AtomicLong> keyMap;

    /**
     * Initialize the factory. Must be called before any primary key is generated.
     * Note
     *
     * @param realm Realm to configure primary keys for.
     */
    public static synchronized void init(Realm realm) {
        if (keyMap != null) {
            throw new IllegalStateException("Factory has already been initialized. Call reset() before initializing again.");
        }

        RealmSchema schema = realm.getSchema();
        HashMap<Class<? extends RealmModel>, AtomicLong> map = new HashMap<>();

        final RealmConfiguration configuration = realm.getConfiguration();
        for (final Class<? extends RealmModel> c : configuration.getRealmObjectClasses()) {
            String className = c.getSimpleName();
            RealmObjectSchema objectSchema = schema.get(className);
            if (objectSchema.hasPrimaryKey()) {
                String fieldName = objectSchema.getPrimaryKey();
                RealmFieldType type = objectSchema.getFieldType(fieldName);

                if (type == RealmFieldType.INTEGER) {
                    Number val = realm.where(c).max(fieldName);
                    AtomicLong keyGenerator = new AtomicLong(val != null ? val.longValue() : -1);
                    map.put(c, keyGenerator);
                }
            }
        }

        keyMap = map;
    }

    /**
     * Reset this factory and all generated values.
     * Call {@link #init(Realm)} before using this class again.
     */
    public static synchronized void reset() {
        keyMap = null;
    }

    /**
     * Generate the next primary key for a class. Starting from {@code 0}.
     *
     * @param clazz class to generate the next key for.
     */
    public static long nextKey(final Class<? extends RealmObject> clazz) {
        AtomicLong generator = keyMap.get(clazz);
        return generator.incrementAndGet();
    }
}