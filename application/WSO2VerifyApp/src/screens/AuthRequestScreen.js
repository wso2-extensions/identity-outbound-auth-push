import React, {useState} from 'react';
import {View, Image, Text, StyleSheet, TouchableOpacity} from 'react-native';
import {AuthorizationService} from '@wso2/auth-push-react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const storeData = async (authData) => {
  try {
    await AsyncStorage.getItem('activity').then(async (activity) => {
      let newActivity;
      console.log('Saved async activity: ' + activity);
      console.log('Type of newAccounts: ' + typeof newActivity);
      if (activity == null) {
        console.log('Type of newAccounts: ' + typeof newActivity);
        newActivity = [];
        console.log('New accounts length: ' + newActivity.length);
      } else {
        newActivity = JSON.parse(activity);
        console.log('New accounts length: ' + newActivity.length);
      }
      console.log('Type of newAccounts 2: ' + typeof newActivity);
      let ad = JSON.parse(authData);
      ad.activityId = newActivity.length == 0 ? 1 : newActivity.length + 1;
      console.log('activity ID: ' + ad.activityId);
      console.log('activity to save: ' + JSON.stringify(ad));
      newActivity.push(ad);
      await AsyncStorage.setItem('activity', JSON.stringify(newActivity)).then(
        () => {
          console.log('New activity added to async storage');
        },
      );
    });
  } catch (e) {
    console.log('Async storage error: ' + e);
  }
};

// const getAccountData = async (id) => {
//   console.log('Get accounts at main');
//   return await AsyncStorage.getItem('accounts').then((accounts) => {
//     console.log(
//       'Accounts from Async at auth request:' + JSON.stringify(accounts),
//     );
//     return accounts;
//   });
// };

let requestAccount;
const getAccountByDeviceId = async (id) => {
  return await AsyncStorage.getItem('accounts').then((accounts) => {
    console.log(
      'Accounts from Async at auth request:' + JSON.stringify(accounts),
    );

    let accountsObject = JSON.parse(accounts);

    console.log(
      'Required account for authentication:' +
        JSON.stringify(accountsObject.find(({deviceID}) => deviceID === id)),
    );

    requestAccount = accountsObject.find(({deviceID}) => deviceID === id);
  });
};

const AuthRequestScreen = ({route, navigation}) => {
  // const [account, setAccount] = useState();

  let authData = AuthorizationService.processAuthRequest(route.params);
  getAccountByDeviceId(route.params.data.deviceId).then((account) => {
    console.log('Got the required account: ' + JSON.stringify(account));
    return account;
  });

  return (
    <View>
      {/* Timer view */}
      <View />

      {/* Logo view */}
      <View style={styles.logoView}>
        <Image
          source={require('../assets/img/wso2logo.png')}
          style={styles.logo}
        />
      </View>

      {/* Auth request information view */}
      <View>
        <View style={styles.titleView}>
          <Text style={styles.title}>Are you trying to sign in?</Text>
        </View>

        <View>
          <View style={[styles.center, styles.connectionCodeView]}>
            <Text style={[styles.connectionCodeTitle, styles.center]}>
              Connection Code
            </Text>
            <Text style={styles.connectionCode}>
              {authData.connectionCode} {/* 216 765 */}
            </Text>
          </View>
        </View>

        {/* Information cards */}
        <View style={styles.infoSection}>
          <View style={styles.infoCardSection}>
            <View style={styles.infoCardView}>
              <Image
                source={require('../assets/img/awesome-user.png')}
                style={styles.infoCardImage}
              />
              <View style={styles.infoCardTextView}>
                <Text style={styles.infoCardTextBig}>
                  {authData.displayName}
                  {/* Johnn Doe */}
                </Text>
                <Text style={styles.infoCardTextSmall}>
                  {authData.username}
                  {/* john@wso2.com */}
                </Text>
              </View>
            </View>

            <View style={styles.infoCardView}>
              <Image
                source={require('../assets/img/awesome-building.png')}
                style={[styles.infoCardImage, {height: '100%'}]}
              />
              <View style={styles.infoCardTextView}>
                <Text style={styles.infoCardTextBig}>
                  {authData.organization}
                  {/* WSO2*/}
                </Text>
              </View>
            </View>

            <View style={styles.infoCardView}>
              <Image
                source={require('../assets/img/material-web-asset.png')}
                style={styles.infoCardImage}
              />
              <View style={styles.infoCardTextView}>
                <Text style={styles.infoCardTextBig}>
                  {authData.applicationName}
                  {/*Pickup-Dispatch*/}
                </Text>
                <Text style={styles.infoCardTextSmall}>
                  {authData.applicationUrl}
                  {/* pickup-dispatch.com */}
                </Text>
              </View>
            </View>
          </View>

          <View style={[styles.infoCardSection, {marginTop: '10%'}]}>
            <View style={styles.infoCardView}>
              <Image
                source={require('../assets/img/material-laptop-mac.png')}
                style={styles.infoCardImage}
              />
              <View style={styles.infoCardTextView}>
                <Text style={styles.infoCardTextBig}>
                  {authData.deviceName}
                  {/* MacBook Pro */}
                </Text>
                <Text style={styles.infoCardTextSmall}>
                  {authData.browserName}
                  {/* Chrome */}
                </Text>
              </View>
            </View>

            <View style={styles.infoCardView}>
              <Image
                source={require('../assets/img/material-location.png')}
                style={styles.infoCardImage}
              />
              <View style={styles.infoCardTextView}>
                <Text style={styles.infoCardTextBig}>
                  {authData.ipAddress}
                  {/* 192.168.1.1 */}
                </Text>
                <Text style={styles.infoCardTextSmall} />
              </View>
            </View>
          </View>
        </View>
      </View>

      <View style={styles.responseButtonContainer}>
        <TouchableOpacity
          style={styles.responseButton}
          activeOpacity={0.7}
          onPress={() => {
            AuthorizationService.sendAuthRequest(
                authData,
                'DENIED',
                requestAccount
            )
              .then((res) => {
                let response = JSON.parse(res);
                console.log(
                  'Authorization response: ' +
                    response.data.authenticationStatus,
                );

                if (response.res == 'OK') {
                  console.log(
                    'Activity data at success: ' + JSON.stringify(authData),
                  );
                  storeData(JSON.stringify(authData));
                }

                navigation.navigate(
                  response.res == 'OK' ? 'Main' : 'Authorization Failed',
                );
              })
              .catch((err) => {
                console.log('Send auth error: ' + err);
              });
          }}>
          <Image source={require('../assets/img/deny-button.png')} />
          <Text style={[styles.responseButtonText, {color: '#DB4234'}]}>
            No
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.responseButton}
          onPress={() => {
            console.log('Yes auth response body: ', requestAccount.privateKey);
            AuthorizationService.sendAuthRequest(
              authData,
              'SUCCESSFUL',
              requestAccount,
            )
              .then((res) => {
                let response = JSON.parse(res);
                console.log(
                  'Authorization response: ' +
                    response.data.authenticationStatus,
                );

                if (response.res == 'OK') {
                  console.log(
                    'Activity data at success: ' + JSON.stringify(authData),
                  );
                  storeData(JSON.stringify(authData));
                }

                navigation.navigate(
                  response.res == 'OK' ? 'Main' : 'Authorization Failed',
                );
              })
              .catch((err) => {
                console.log('Send auth error: ' + err);
              });
            // console.log('Authorization response: ' + auth[0]);

            // navigation.navigate(
            //   auth[0] == 'OK' ? 'Main' : 'Authorization Failed',
            // );
          }}
          activeOpacity={0.7}>
          <Image source={require('../assets/img/accept-button.png')} />
          <Text style={[styles.responseButtonText, {color: '#21AD03'}]}>
            Yes
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  logo: {
    alignSelf: 'center',
    width: '20%',
    resizeMode: 'contain',
  },
  logoText: {
    color: '#f47b20',
    textAlign: 'center',
    fontWeight: '600',
    fontSize: 18,
    top: -30,
    left: 20,
  },
  logoView: {
    marginTop: '5%',
  },
  title: {
    fontSize: 20,
    fontFamily: 'Roboto-Light',
    textAlign: 'center',
  },
  titleView: {
    marginTop: '5%',
    marginBottom: '2%',
  },
  connectionCodeView: {
    margin: '5%',
  },
  connectionCodeTitle: {
    color: '#FD7322',
    fontFamily: 'Roboto-Regular',
    fontSize: 16,
    textAlign: 'center',
  },
  connectionCode: {
    fontFamily: 'Roboto-Regular',
    fontSize: 40,
    textAlign: 'center',
    color: '#7C7C7C',
  },
  infoSection: {
    alignSelf: 'center',
  },
  infoCardSection: {
    alignSelf: 'flex-start',
    marginLeft: '5%',
  },
  infoCardView: {
    flexDirection: 'row',
    marginBottom: '6%',
  },
  infoCardImage: {
    marginVertical: '3.5%',
    height: '70%',
    width: '20%',
    alignSelf: 'center',
    resizeMode: 'contain',
  },
  infoCardTextView: {
    marginLeft: '10%',
    justifyContent: 'center',
  },
  infoCardTextBig: {
    fontFamily: 'Roboto-Regular',
    fontSize: 18,
    color: '#000',
  },
  infoCardTextSmall: {
    fontFamily: 'Roboto-Regular',
    fontSize: 16,
    color: '#FD7322',
  },
  responseButtonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
    paddingHorizontal: '10%',
    marginTop: '5%',
  },
  responseButton: {
    alignItems: 'center',
    width: '90%',
  },
  responseButtonText: {
    fontFamily: 'Roboto-Bold',
    fontSize: 16,
    marginTop: '3%',
  },
});

export default AuthRequestScreen;
