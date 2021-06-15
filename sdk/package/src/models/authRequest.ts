/**
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import { DateTimeInterface } from "./dateTime";

export interface AuthDataInterface {
  deviceId: string;
  challenge: string;
  authUrl: string;
  privateKey: string;
}

export interface AuthRequestInterface extends AuthDataInterface {
  connectionCode?: string;
  displayName?: string;
  username?: string;
  organization?: string;
  applicationName?: string;
  applicationUrl?: string;
  deviceName?: string;
  browserName?: string;
  ipAddress?: string;
  location?: string;
  expiryTime?: string;
  challenge: string;
  deviceId: string;
  sessionDataKey?: string;
  authUrl: string;
  privateKey: string;
  authenticationStatus?: String;
  requestTime?: DateTimeInterface;
  // TODO: Consider if only the default string for time is sent or a destructured time for custom changes
}
