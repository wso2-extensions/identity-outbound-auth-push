/**
 * @format
 */

import * as React from 'react';
import {AppRegistry} from 'react-native';
import App from './App';
import {name as appName} from './app.json';
import messaging from '@react-native-firebase/messaging';

// Register background handler
// messaging().setBackgroundMessageHandler(async (remoteMessage) => {
//   console.log('Message handled in the background!', remoteMessage);
//   console.log('A new FCM message arrived!', JSON.stringify(remoteMessage));
//   React.createRef().current.navigate('Authorization Request', remoteMessage);
// });

AppRegistry.registerComponent(appName, () => App);
