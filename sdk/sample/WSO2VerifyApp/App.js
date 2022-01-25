/**
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import React, {useEffect} from 'react';
import {StyleSheet, StatusBar} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import messaging from '@react-native-firebase/messaging';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {DeviceInfoUtil} from '@wso2/auth-push-react-native';

import StartScreen from './src/screens/StartScreen';
import AddAccountScreen from './src/screens/AddAccountScreen';
import AddAccountSuccessScreen from './src/screens/AddAccountSuccessScreen';
import AddAccountFailedScreen from './src/screens/AddAccountFailedScreen';
import AuthFailedScreen from './src/screens/AuthFailedScreen';
import AuthRequestScreen from './src/screens/AuthRequestScreen';
import MainScreen from './src/screens/MainScreen';
import QRScannerScreen from './src/screens/QRScannerScreen';
import {navigationRef, isReadyRef, navigate} from './src/utils/RootNavigation';

const Stack = createStackNavigator();

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
new DeviceInfoUtil();

const App: () => React$Node = () => {
    getToken();

    /**
     * Handle auth request from running state
     */
    useEffect(() => {
        const unsubscribe = messaging().onMessage(async (remoteMessage) => {
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

    return (
        <>
            <StatusBar barStyle="default"/>
            <NavigationContainer
                ref={navigationRef}
                onReady={() => {
                    isReadyRef.current = true;
                }}>
                <Stack.Navigator initialRouteName="Main" headerMode="none">
                    <Stack.Screen name="Start" component={StartScreen}/>
                    <Stack.Screen name="Main" component={MainScreen}/>
                    <Stack.Screen name="Add Account" component={AddAccountScreen}/>
                    <Stack.Screen name="QR Scanner" component={QRScannerScreen}/>
                    <Stack.Screen
                        name="Add Success"
                        component={AddAccountSuccessScreen}
                    />
                    <Stack.Screen name="Add Failed" component={AddAccountFailedScreen}/>
                    <Stack.Screen
                        name="Authorization Request"
                        component={AuthRequestScreen}
                    />
                    <Stack.Screen
                        name="Authorization Failed"
                        component={AuthFailedScreen}
                    />
                </Stack.Navigator>
            </NavigationContainer>
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
