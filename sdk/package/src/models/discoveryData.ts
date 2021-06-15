/**
 * Interface for the Discovery Data model
 */
export interface DiscoveryDataInterface {
  deviceId: string;
  username: string;
  firstName?: string;
  lastName?: string;
  tenantDomain?: string;
  host: string;
  basePath: string;
  registrationEndpoint: string;
  authenticationEndpoint: string;
  removeDeviceEndpoint: string;
  challenge: string;
}
