/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useState, useEffect} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';
import {NavigationContainer, useNavigation} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import messaging from '@react-native-firebase/messaging';
import firebase from '@react-native-firebase/app';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {DeviceInformation} from '@wso2/auth-push-react-native';

import StartScreen from './src/screens/StartScreen';
import AddAccountScreen from './src/screens/AddAccountScreen';
import AddAccountSuccessScreen from './src/screens/AddAccountSuccessScreen';
import AddAccountFailedScreen from './src/screens/AddAccountFailedScreen';
import AuthFailedScreen from './src/screens/AuthFailedScreen';
import AccountsScreen from './src/screens/AccountsScreen';
import AuthRequestScreen from './src/screens/AuthRequestScreen';
import ActivityScreen from './src/screens/ActivityScreen';
import MainScreen from './src/screens/MainScreen';
import QRScannerScreen from './src/screens/QRScannerScreen';

import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';

import {navigationRef, isReadyRef, navigate} from './src/utils/RootNavigation';

// import TabViewExample from './src/screens/TabScreenTest';
const Tab = createBottomTabNavigator();

const accounts = [];

const Stack = createStackNavigator();

const accountsAvailable = () => {
  console.log(accounts.length);
  return accounts.length == 0 ? 'Main' : 'Start';
};

const requestUserPermission = async () => {
  const authStatus = await messaging().requestPermission();
  const enabled =
    authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
    authStatus === messaging.AuthorizationStatus.PROVISIONAL;

  if (enabled) {
    console.log('Authorization status:', authStatus);
  }
};

const getToken = async () => {
  requestUserPermission()
    .then(async () => {
      let fcmToken = await messaging().getToken();
      console.log(fcmToken);
      await AsyncStorage.setItem('pushId', fcmToken);
    })
    .catch((err) => {
      throw new Error(err);
    });
};

// Initialize device information
new DeviceInformation();
// TODO: Do a proper implementation in the SDK

const App: () => React$Node = () => {
  // const accountsAvailable = 'Start';
  // const navigation = useNavigation();
  // const [initialRoute, setInitialRoute] = useState('Main');
  // const [loading, setLoading] = useState(true);

  getToken();

  /**
   * Handle auth request from running state
   */
  useEffect(() => {
    const unsubscribe = messaging().onMessage(async (remoteMessage) => {
      // Alert.alert('A new FCM message arrived!', JSON.stringify(remoteMessage));
      console.log(
        'A new FCM message arrived!',
        JSON.stringify(remoteMessage.data),
      );
      navigate('Authorization Request', remoteMessage);
    });

    return unsubscribe;
  }, []);

  /**
   * Handle auth request from sleep state
   */
  useEffect(() => {
    const unsubscribe = messaging().setBackgroundMessageHandler(
      async (remoteMessage) => {
        console.log('Message handled in the background!', remoteMessage);
        console.log(
          'A new FCM message arrived!',
          JSON.stringify(remoteMessage),
        );
        navigate('Authorization Request', remoteMessage);
      },
    );
    return unsubscribe;
  }, []);

  /**
   * Set component mounted indicator for navigation
   */
  useEffect(() => {
    return () => {
      isReadyRef.current = false;
    };
  }, []);

  // useEffect(() => {
  //   // Assume a message-notification contains a "type" property in the data payload of the screen to open

  //   messaging().onNotificationOpenedApp((remoteMessage) => {
  //     console.log(
  //       'Notification caused app to open from background state:',
  //       remoteMessage.notification,
  //     );
  //     navigation.navigate('Start');
  //   });

  //   // Check whether an initial notification is available
  //   messaging()
  //     .getInitialNotification()
  //     .then((remoteMessage) => {
  //       if (remoteMessage) {
  //         console.log(
  //           'Notification caused app to open from quit state:',
  //           remoteMessage.notification,
  //         );
  //         setInitialRoute('Start'); // e.g. "Settings"
  //       }
  //       setLoading(false);
  //     });
  // }, []);

  // if (loading) {
  //   return null;
  // }

  // messaging().setBackgroundMessageHandler(async (remoteMessage) => {
  //   console.log('Message handled in the background!', remoteMessage);
  //   console.log('A new FCM message arrived!', JSON.stringify(remoteMessage));
  //   navigate('Authorization Request', JSON.parse(remoteMessage));
  // });

  return (
    <>
      <StatusBar barStyle="default" />
      {/* <SafeAreaView> */}
      <NavigationContainer
        ref={navigationRef}
        onReady={() => {
          isReadyRef.current = true;
        }}>
        <Stack.Navigator initialRouteName="Main" headerMode="none">
          <Stack.Screen name="Start" component={StartScreen} />
          <Stack.Screen name="Main" component={MainScreen} />
          <Stack.Screen name="Add Account" component={AddAccountScreen} />
          <Stack.Screen name="QR Scanner" component={QRScannerScreen} />
          <Stack.Screen
            name="Add Success"
            component={AddAccountSuccessScreen}
          />
          <Stack.Screen name="Add Failed" component={AddAccountFailedScreen} />
          <Stack.Screen
            name="Authorization Request"
            component={AuthRequestScreen}
          />
          <Stack.Screen
            name="Authorization Failed"
            component={AuthFailedScreen}
          />

          {/* <StartScreen /> */}
          {/* <AddAccountScreen /> */}
          {/* <AddAccountSuccessScreen /> */}
          {/* <AddAccountFailedScreen /> */}
          {/* <AuthFailedScreen /> */}
          {/* <AccountsScreen /> */}
          {/* <AuthRequestScreen /> */}
          {/* <ActivityScreen /> */}
          {/* <NavigationContainer> */}
          {/* <MainScreen /> */}
          {/* <QRScannerScreen /> */}
        </Stack.Navigator>
      </NavigationContainer>

      {/* <TabViewExample /> */}
      {/* </SafeAreaView> */}
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
