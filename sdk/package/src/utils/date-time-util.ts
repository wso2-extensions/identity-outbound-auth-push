/**
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {DateTimeInterface} from "src/models/date-time";

/**
 * Util class for handling datetime functions.
 */
export class DateTimeUtil {
    private dateTime: DateTimeInterface;

    public constructor() {
        const current = new Date();

        this.dateTime = {
            day: current.getDate(),
            month: current.getMonth(),
            year: current.getFullYear(),
            hour: current.getHours(),
            minute: current.getMinutes(),
            seconds: current.getSeconds(),
            date:
                current.getDate() +
                "-" +
                current.getMonth() +
                "-" +
                current.getFullYear(),
            time:
                (current.getHours() < 12
                    ? current.getHours()
                    : current.getHours() - 12) +
                ":" +
                current.getMinutes() +
                (current.getHours() < 12)
                    ? " a.m."
                    : " p.m."
        };
    }

    public getDateTime(): DateTimeInterface {
        return this.dateTime;
    }
}
