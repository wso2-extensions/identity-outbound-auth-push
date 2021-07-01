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

import React from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    TouchableOpacity,
    Linking,
    View,
    Image,
} from 'react-native';
import {
    widthPercentageToDP as wp,
    heightPercentageToDP as hp,
} from 'react-native-responsive-screen';

import QRCodeScanner from 'react-native-qrcode-scanner';
import {RNCamera} from 'react-native-camera';
import AsyncStorage from '@react-native-async-storage/async-storage';

import {AccountsService} from '@wso2/auth-push-react-native';

let pushId;

const getPushId = async () => {
    try {
        let value = await AsyncStorage.getItem('pushId');
        pushId = value;
        console.log('Async pushID: ' + pushId);
    } catch (e) {
        console.log('No Push ID available');
    }
};

const QRScannerScreen = ({navigation}) => {
    if (pushId == null) {
        getPushId();
    }

    let onSuccess = (e) => {
        console.log('Scanned: ', e.data);

        try {
            let account = new AccountsService();
            account
                .addAccount(
                    JSON.parse(e.data),
                    pushId,
                )
                .then((response) => {
                    let res = JSON.parse(response);
                    console.log('Add account response: ' + response);
                    if (res.res == 'OK') {
                        navigation.navigate('Add Success', res.data);
                    } else if (res.res == 'FAILED') {
                        navigation.navigate('Add Failed');
                    }
                });
        } catch (err) {
            console.log(err);
            navigation.navigate('Add Failed');
        }
    };

    return (
        <View
            style={{
                flexDirection: 'column',
                height: hp('97%'),
                justifyContent: 'center',
                marginTop: '3%',
                flex: 1,
            }}>
            <View style={{flex: 1}}>
                <View style={styles.logoView}>
                    <Image
                        source={require('../assets/img/wso2logo.png')}
                        style={styles.logo}
                    />
                </View>
                <View>
                    <TouchableOpacity
                        onPress={() => {
                            console.log('Back Pressed!');
                            navigation.goBack();
                        }}
                        activeOpacity={0.9}
                        style={styles.backButton}>
                        <Image source={require('../assets/img/material-arrow-back.png')}/>
                    </TouchableOpacity>
                </View>
                <View style={styles.titleView}>
                    <Text style={styles.title}>Scan QR Code</Text>
                </View>
            </View>
            <View style={{flex: 4, alignSelf: 'flex-end'}}>
                <QRCodeScanner
                    onRead={onSuccess}
                    showMarker={true}
                    flashMode={RNCamera.Constants.FlashMode.off}
                    cameraStyle={{
                        marginTop: hp('3%'),
                        height: hp('75%'),
                        alignSelf: 'flex-end',
                    }}
                />
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
        flex: 3,
    },
    title: {
        fontSize: 36,
        fontFamily: 'Roboto-Light',
        textAlign: 'center',
    },
    centerText: {
        flex: 1,
        fontSize: 18,
        padding: 32,
        color: '#777',
    },
    textBold: {
        fontWeight: '500',
        color: '#000',
    },
    buttonText: {
        fontSize: 21,
        color: 'rgb(0,122,255)',
    },
    buttonTouchable: {
        padding: 16,
    },
    backButton: {
        left: wp('5%'),
        bottom: hp('5%'),
        alignSelf: 'flex-start',
        position: 'absolute',
    },
});

export default QRScannerScreen;
