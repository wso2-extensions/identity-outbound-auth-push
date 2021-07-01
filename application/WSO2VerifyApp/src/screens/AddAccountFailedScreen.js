import React from 'react';
import {View, Image, Text, StyleSheet} from 'react-native';
import {LargeButton, LineButton} from '../components/Button';

const AddAccountFailedScreen = ({navigation}) => {
    return (
        <View style={{backgroundColor: '#FFF', height: '100%'}}>
            <View style={styles.logoView}>
                <Image
                    source={require('../assets/img/wso2logo.png')}
                    style={styles.logo}
                />
            </View>
            <View>
                <View style={styles.resultContainer}>
                    <Image
                        source={require('../assets/img/fail-icon.png')}
                        style={styles.resultImage}
                    />
                </View>
                <View>
                    <Text style={styles.resultText}>
                        Something Went Wrong {'\n'} Please Try Again
                    </Text>
                </View>
            </View>
            <View style={styles.buttonView}>
                <LineButton title="Close" action={() => navigation.navigate('Main')}/>
                <LargeButton
                    title="Retry"
                    action={() => navigation.navigate('Add Account')}
                />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    buttonRoot: {
        marginTop: 64,
    },
    buttonView: {
        marginVertical: 10,
        marginHorizontal: '25%',
        bottom: '-15%',
    },
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
    resultImage: {
        alignSelf: 'center',
        width: '50%',
        resizeMode: 'contain',
    },
    resultContainer: {
        marginHorizontal: '5%',
        marginTop: '25%',
        marginBottom: '10%',
    },
    title: {
        fontSize: 36,
        fontWeight: '300',
        fontFamily: 'Helvetica',
        textAlign: 'center',
    },
    resultText: {
        fontSize: 20,
        fontFamily: 'Roboto-Regular',
        textAlign: 'center',
        color: '#000',
    },
    titleView: {
        marginTop: '20%',
    },
    button: {
        top: '90%',
        paddingHorizontal: '30%',
    },
});

export default AddAccountFailedScreen;
