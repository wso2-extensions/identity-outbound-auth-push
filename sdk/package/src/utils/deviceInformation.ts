import DeviceInfo from 'react-native-device-info';


export class DeviceInformation {
  private static deviceName: string;
  private static deviceBrand: string;
  private static deviceModel: string;


  /**
   * Constructor for device information class
   * Assigns the values to the static variables for later use
   */
  public constructor() { 
    console.log("Device info initialized");

    // TODO: Is there a requirement to check if the device name has changed?

    // Adding device name
    if (DeviceInformation.deviceName == null) {
      console.log("Constructor called to add details.");
      DeviceInfo.getDeviceName()
        .then((deviceName: string) => {
          console.log('Device Name: ' + deviceName);
          DeviceInformation.deviceName = deviceName;
        })
        .catch((err: string) => {
          console.log('Get device name: ' + err);
        })
    } else { 
      console.log("Device info already added.");
    }

    // Adding device brand
    if (DeviceInformation.deviceBrand == null) {
      console.log("Adding device model");
      DeviceInformation.deviceBrand = DeviceInfo.getBrand();
    } else { 
      console.log("Model already added.")
    }

    // Adding device model
    if (DeviceInformation.deviceModel == null) { 
      console.log("Adding device model");
      DeviceInformation.deviceModel = DeviceInfo.getModel();
    }

  }

  /**
   * Returns the name of the device
   * 
   * @returns device name
   */
  public static getDeviceName():string {
    return this.deviceName;
  }

  /**
   * Returns the model name of the device
   * 
   * @returns device model
   */
  public static getDeviceModel(): string {
    return DeviceInformation.deviceModel;
  }

  /**
   * Returns the brand name of the device
   * 
   * @returns device brand
   */
  public static getDeviceBrand(): string { 
    return DeviceInformation.deviceBrand;
  }

}



