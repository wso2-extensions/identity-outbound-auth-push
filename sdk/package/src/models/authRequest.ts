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
