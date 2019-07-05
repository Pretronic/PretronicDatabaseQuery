/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 11.06.19, 21:20
 *
 * The PrematicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.prematic.databasequery.core.datatype.adapter;

/**
 * This class is for converting the specific class {@link W} in {@link R} and reversed.
 *
 * {@link W} represents the type, you get by inserting the value and with
 * {@link R} you specify in which type, it will be convert.
 *
 * So reversed it means, by reading, you get the type of {@link R} and you have to return it
 * with the type of {@link W}.
 *
 * @param <W> write class
 * @param <R> read class
 */
public interface DataTypeAdapter<W, R> {

    /**
     * Converts the input type {@link W} into the output type {@link R}.
     *
     * @param value to convert in {@link R}
     * @return the converted value {@link R}
     */
    R write(W value);

    /**
     * Converts input type {@link R} into the output type {@link W}.
     *
     * @param value to connert in {@link W}
     * @return the converted value {@link W}
     */
    W read(R value);
}