import { DiscoveryDataInterface } from "../models/discoveryData";

/**
 * Interface for Registration request model
 */
export interface RegistrationRequestInterface {
  deviceId: string;
  deviceName?: string;
  model?: string;
  pushID: string;
  publicKey: string;
  signature: string;
}
