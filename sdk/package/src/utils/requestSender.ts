import { fetch } from "react-native-ssl-pinning";
import { Alert } from "react-native";

export class RequestSender {
  public constructor() {}

  public async sendRequest(
    url: string,
    requestMethod: any,
    requestHeaders: any,
    body: any
  ): Promise<string> {
    return fetch(url, {
      method: requestMethod,
      disableAllSecurity: true,
      sslPinning: {
        certs: ["wso2carbon"], // TODO: make the certificate name configurable
      },
      headers: requestHeaders,
      body: body,
    })
      .then((response: { bodyString: string; status: string }) => {
        console.log(`response received ${response.bodyString}`);
        // Alert.alert(
        //   "Request",
        //   "Status: " + response.status + "\nMessage: " + response.bodyString,
        //   [{ text: "OK", onPress: () => console.log("OK Pressed") }],
        //   { cancelable: false }
        // );
        return response.status == "200" ? "OK" : "FAILED";
      })
      .catch((err: any) => {
        console.log(`error: ${err.status}`);
        return "FAILED";
      });
  }
}
