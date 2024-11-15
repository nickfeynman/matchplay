/*
 * Copyright 2024 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.matchplay.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GamesApiTests {

    @Autowired
    GamesApi gamesApi;
    @Test
    @Disabled("Round behavior seems broken now, but not rounds in best single play tourny")
    void getGame() {
        //Integer tournyId = 160189;  // the harvest  // arena id congo 161603
        Integer tournyId = 129750; // midieval madness tourny

        String gameName = gamesApi.getGameName(tournyId, 1531986);
        System.out.println(gameName);
    }
}
