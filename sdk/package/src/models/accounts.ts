/**
 * Interface for the registered Account
 */
export interface AccountsInterface {
  deviceID: string;
  username?: string;
  firstName?: string;
  lastName?: string;
  tenantDomain?: string;
  host?: string;
  basePath?: string;
  authenticationEndpoint?: string;
  removeDeviceEndpoint?: string;
  privateKey?: string;
}
