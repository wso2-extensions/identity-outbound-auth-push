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
import {View, Image, Text, StyleSheet} from 'react-native';
import {WhiteButton} from '../components/Button';

const AuthFailedScreen = ({navigation}) => {
    return (
        <View style={styles.body}>
            <View style={styles.failedImageContainer}>
                <Image
                    source={require('../assets/img/error-icon.png')}
                    style={styles.failedImage}
                />
            </View>
            <View style={styles.failedTextContainer}>
                <Text style={styles.failedtitle}>Something Went Wrong!</Text>
                <Text style={styles.failedText}>Login Failed... Please Try Again</Text>
            </View>
            <View style={styles.buttonView}>
                <WhiteButton
                    title="close"
                    action={() => {
                        navigation.navigate('Main');
                    }}
                />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    body: {
        backgroundColor: '#585858',
        height: '100%',
    },
    buttonRoot: {
        marginTop: 64,
    },
    buttonView: {
        marginVertical: 10,
        marginHorizontal: '25%',
        bottom: '-30%',
    },
    failedImage: {
        alignSelf: 'center',
        width: '40%',
        resizeMode: 'contain',
    },
    failedImageContainer: {
        marginHorizontal: '5%',
        marginTop: '25%',
        marginBottom: '10%',
        top: '25%',
    },
    failedtitle: {
        fontSize: 18,
        fontFamily: 'Roboto-Regular',
        textAlign: 'center',
        alignSelf: 'center',
        color: '#FFF',
        margin: 3,
    },
    failedText: {
        fontSize: 16,
        fontFamily: 'Roboto-Light',
        textAlign: 'center',
        color: '#FFF',
        alignSelf: 'center',
        margin: 3,
    },
    failedTextContainer: {
        marginTop: '25%',
        top: '10%',
        alignSelf: 'center',
        color: '#FFF',
    },
    button: {
        top: '90%',
        paddingHorizontal: '30%',
    },
});

export default AuthFailedScreen;
