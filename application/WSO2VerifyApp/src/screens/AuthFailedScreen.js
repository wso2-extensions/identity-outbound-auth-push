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
