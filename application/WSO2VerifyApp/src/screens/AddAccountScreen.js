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
import {View, Image, Text, StyleSheet, TouchableOpacity} from 'react-native';
import {LargeButton} from '../components/Button';
import {
  widthPercentageToDP as wp,
  heightPercentageToDP as hp,
} from 'react-native-responsive-screen';

const AddAccountScreen = ({navigation}) => (
  <View style={styles.container}>
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
        <Image source={require('../assets/img/material-arrow-back.png')} />
      </TouchableOpacity>
    </View>
    <View style={styles.titleView}>
      <Text style={styles.title}>Add Account</Text>
    </View>
    <View style={styles.mainCardView}>
      <View>
        <Image
          source={require('../assets/img/add_account_image.png')}
          style={styles.addAccImage}
        />
      </View>
      <View>
        <Image
          source={require('../assets/img/add_acc_steps.png')}
          style={styles.addAccSteps}
        />
      </View>
      <View style={styles.button}>
        <LargeButton
          title="Scan QR Code"
          action={() => navigation.navigate('QR Scanner')} //TODO: Change back to QR
        />
      </View>
    </View>
  </View>
);

const styles = StyleSheet.create({
  container: {
    flexDirection: 'column',
    height: hp('97%'),
    justifyContent: 'center',
    marginTop: '3%',
  },
  logo: {
    alignSelf: 'center',
    width: '20%',
    resizeMode: 'contain',
  },
  logoView: {
    flex: 3,
  },
  title: {
    fontSize: 36,
    fontWeight: '300',
    fontFamily: 'Roboto-Light',
    textAlign: 'center',
  },
  mainCardView: {
    backgroundColor: '#FFF',
    height: '70%',
    shadowColor: '#000',
    shadowOpacity: 0.2,
    shadowOffset: {height: -2, width: 0},
    shadowRadius: 6,
    // marginTop: '10%',
    borderTopLeftRadius: 50,
    borderTopRightRadius: 50,
    padding: '10%',
    elevation: 20,
    flex: 12,
  },
  titleView: {
    flex: 2,
  },
  button: {
    top: '-35%',
    paddingHorizontal: '10%',
  },
  addAccImage: {
    alignSelf: 'center',
  },
  addAccSteps: {
    alignSelf: 'center',
    width: '85%',
    resizeMode: 'contain',
    top: '-15%',
  },
  backButton: {
    left: wp('5%'),
    bottom: hp('5%'),
    alignSelf: 'flex-start',
    position: 'absolute',
  },
});

export default AddAccountScreen;
