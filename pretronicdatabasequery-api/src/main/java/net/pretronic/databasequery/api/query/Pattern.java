/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.03.20, 20:28
 * @website %web%
 *
 * The PretronicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
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

package net.pretronic.databasequery.api.query;

public class Pattern {

    private String pattern, startsWith, endsWith, contains;

    public Pattern withPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public Pattern startsWith(String startsWith) {
        this.startsWith = startsWith;
        return this;
    }

    public Pattern endsWith(String endsWith) {
        this.endsWith = endsWith;
        return this;
    }

    public Pattern contains(String contains) {
        this.contains = contains;
        return this;
    }

    public String build() {
        if(pattern != null) return pattern;
        StringBuilder builder = new StringBuilder();
        if(startsWith != null) builder.append(startsWith);
        builder.append("%");
        if(endsWith != null) builder.append(endsWith);
        if(contains != null) {
            builder.append(contains);
            builder.append("%");
        }
        return builder.toString();
    }
}
